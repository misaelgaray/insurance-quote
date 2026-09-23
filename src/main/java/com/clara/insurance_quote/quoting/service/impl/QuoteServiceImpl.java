package com.clara.insurance_quote.quoting.service.impl;

import com.clara.insurance_quote.catalog.dto.CoverageTypeDto;
import com.clara.insurance_quote.quoting.client.CatalogClient;
import com.clara.insurance_quote.quoting.client.FulfillmentClient;
import com.clara.insurance_quote.quoting.client.UnderwritingClient;
import com.clara.insurance_quote.quoting.dto.QuoteSummaryDto;
import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import com.clara.insurance_quote.quoting.entity.QuoteStatus;
import com.clara.insurance_quote.quoting.dto.CreateQuoteRequestDto;
import com.clara.insurance_quote.quoting.dto.QuoteResponseDto;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;
import com.clara.insurance_quote.quoting.event.QuoteEventPublisher;
import com.clara.insurance_quote.quoting.event.QuoteSubmittedEvent;
import com.clara.insurance_quote.quoting.exception.IneligibleQuotingDataException;
import com.clara.insurance_quote.quoting.exception.QuoteSubmissionFailedException;
import com.clara.insurance_quote.quoting.repository.QuoteRepository;
import com.clara.insurance_quote.quoting.service.PremiumCalculationEngine;
import com.clara.insurance_quote.quoting.service.QuoteService;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthRequestDto;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final CatalogClient catalogClient;
    private final PremiumCalculationEngine premiumCalculationEngine;
    private final UnderwritingClient underwritingClient;
    private final FulfillmentClient fulfillmentClient;
    private final QuoteEventPublisher quoteEventPublisher;
    private final CacheManager cacheManager;

    @Override
    public QuoteResponseDto createQuote(CreateQuoteRequestDto request) {
        QuoteEntity quote = QuoteEntity.builder()
                .status(QuoteStatus.DRAFT)
                .applicantName(request.applicantName())
                .applicantEmail(request.applicantEmail())
                .applicantAge(request.applicantAge())
                .zipCode(request.zipCode())
                .build();

        QuoteEntity savedQuote = quoteRepository.save(quote);
        return mapToDto(savedQuote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteResponseDto> getAllQuotes(QuoteStatus status) {
        List<QuoteEntity> quotes = (status != null)
                ? quoteRepository.findByStatus(status)
                : quoteRepository.findAll();

        return quotes.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    //@Cacheable(value = "quotes", key = "#id")
    @Transactional(readOnly = true)
    public QuoteResponseDto getQuoteById(UUID id) {
        QuoteEntity quote = findEntityById(id);
        return mapToDto(quote);
    }

    @Override
    public QuoteResponseDto updateCoverage(UUID id, UpdateCoverageRequestDto request) {
        QuoteEntity quote = findEntityById(id);

        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new IllegalStateException("Cannot update coverage for a quote in status: " + quote.getStatus());
        }

        // Age Gating Validation: Applicants <= 65 must NOT supply supplemental underwriting data
        if (quote.getApplicantAge() <= 65 && request.containsSupplementalData()) {
            throw new IneligibleQuotingDataException("Cannot receive data, user is less than 65.");
        }

        // Fetch coverage type using the abstracted CatalogClient
        CoverageTypeDto coverageType = catalogClient.getCoverageTypeByCode(request.coverageTypeCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid coverage type code: " + request.coverageTypeCode()));

        quote.setCoverageTypeCode(coverageType.code());

        // Recalculate monthly premium via pricing engine
        BigDecimal finalPremium = premiumCalculationEngine.calculateMonthlyPremium(
                coverageType.basePremium(),
                quote,
                request
        );

        quote.setCalculatedMonthlyPremium(finalPremium);

        // If applicant is > 65 and provided supplemental health data, persist via UnderwritingClient port
        if (quote.getApplicantAge() > 65 && request.containsSupplementalData()) {
            SupplementalHealthRequestDto healthRequest = new SupplementalHealthRequestDto(
                    quote.getId(),
                    request.hasPreexistingConditions(),
                    request.takesPrescriptionMedication(),
                    request.usesTobacco(),
                    request.needsSpouseCoverage(),
                    request.preexistingConditions()
            );
            underwritingClient.saveOrUpdateSupplementalHealth(healthRequest);
        }

        QuoteEntity updatedQuote = quoteRepository.save(quote);
        return mapToDto(updatedQuote);
    }

    private QuoteEntity findEntityById(UUID id) {
        return quoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found with ID: " + id));
    }

    @Override
    @Transactional
    public QuoteResponseDto submitQuote(UUID id) {
        QuoteEntity quote = findEntityById(id);

        // 1. Idempotency Check: Already submitted quotes return successfully immediately
        if (quote.getStatus() == QuoteStatus.SUBMITTED) {
            log.info("Quote {} is already SUBMITTED. Returning idempotent success.", id);
            return mapToDto(quote);
        }

        // 2. Validate state transitions
        if (quote.getStatus() == QuoteStatus.EXPIRED) {
            throw new IllegalStateException("Cannot submit an EXPIRED quote.");
        }

        // 3. Validate complete quote data requirement
        if (quote.getCoverageTypeCode() == null || quote.getCalculatedMonthlyPremium() == null) {
            throw new IllegalStateException("Quote is incomplete. Select coverage before submitting.");
        }

        // 4. Invoke external real API (httpstat.us)
        boolean isSuccess = fulfillmentClient.submitQuoteToProvider(quote);

        if (isSuccess) {
            quote.setStatus(QuoteStatus.SUBMITTED);
            QuoteEntity saved = quoteRepository.save(quote);

            // Evict quote from cache upon successful state transition
            evictQuoteCache(id);

            // Publish QuoteSubmittedEvent to Kafka
            QuoteSubmittedEvent event = new QuoteSubmittedEvent(
                    saved.getId(),
                    saved.getApplicantName(),
                    saved.getApplicantEmail(),
                    saved.getCoverageTypeCode(),
                    saved.getCalculatedMonthlyPremium(),
                    OffsetDateTime.now()
            );
            quoteEventPublisher.publishQuoteSubmitted(event);


            return mapToDto(saved);
        } else {
            // Handle failure state transition
            quote.setStatus(QuoteStatus.SUBMISSION_FAILED);
            quoteRepository.save(quote);

            // Evict cache so GET reflect SUBMISSION_FAILED
            evictQuoteCache(id);

            throw new QuoteSubmissionFailedException(
                    "Quote submission failed due to upstream provider error or timeout. The quote remains resubmittable."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteSummaryDto getQuoteSummaryById(UUID id) {
        QuoteEntity quote = findEntityById(id);

        SupplementalHealthResponseDto supplementalHealth = underwritingClient
                .getSupplementalHealthByQuoteId(id)
                .orElse(null);

        return mapToSummaryDto(quote, supplementalHealth);
    }

    private QuoteResponseDto mapToDto(QuoteEntity entity) {
        return new QuoteResponseDto(
                entity.getId(),
                entity.getStatus(),
                entity.getApplicantName(),
                entity.getApplicantEmail(),
                entity.getApplicantAge(),
                entity.getZipCode(),
                entity.getCoverageTypeCode(),
                entity.getCalculatedMonthlyPremium(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private QuoteSummaryDto mapToSummaryDto(QuoteEntity entity, SupplementalHealthResponseDto supplementalHealth) {
        return new QuoteSummaryDto(
                entity.getId(),
                entity.getStatus(),
                entity.getApplicantName(),
                entity.getApplicantEmail(),
                entity.getApplicantAge(),
                entity.getZipCode(),
                entity.getCoverageTypeCode(),
                entity.getCalculatedMonthlyPremium(),
                supplementalHealth,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void evictQuoteCache(Object id) {
        if (id != null) {
            Cache cache = cacheManager.getCache("quotes");
            if (cache != null) {
                cache.evict(id);
            }
        }
    }
}
