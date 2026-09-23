package com.clara.insurance_quote.quoting.service;

import com.clara.insurance_quote.catalog.dto.CoverageTypeDto;
import com.clara.insurance_quote.quoting.client.CatalogClient;
import com.clara.insurance_quote.quoting.client.FulfillmentClient;
import com.clara.insurance_quote.quoting.client.UnderwritingClient;
import com.clara.insurance_quote.quoting.entity.QuoteStatus;
import com.clara.insurance_quote.quoting.dto.QuoteResponseDto;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;
import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import com.clara.insurance_quote.quoting.event.QuoteEventPublisher;
import com.clara.insurance_quote.quoting.event.QuoteSubmittedEvent;
import com.clara.insurance_quote.quoting.exception.IneligibleQuotingDataException;
import com.clara.insurance_quote.quoting.exception.QuoteSubmissionFailedException;
import com.clara.insurance_quote.quoting.repository.QuoteRepository;
import com.clara.insurance_quote.quoting.service.impl.QuoteServiceImpl;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceImplTest {

    @Mock private QuoteRepository quoteRepository;
    @Mock private CatalogClient catalogClient;
    @Mock private UnderwritingClient underwritingClient;
    @Mock private FulfillmentClient fulfillmentClient;
    @Mock private PremiumCalculationEngine premiumCalculationEngine;
    @Mock private QuoteEventPublisher quoteEventPublisher;
    @Mock private CacheManager cacheManager;
    @Mock private Cache cache;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    @Test
    @DisplayName("Should throw IneligibleUnderwritingDataException when age <= 65 and supplemental health data is provided")
    void updateCoverage_ThrowsException_WhenUserAge65OrUnderWithHealthData() {
        // Given
        UUID quoteId = UUID.randomUUID();
        QuoteEntity quote = QuoteEntity.builder().id(quoteId).applicantAge(50).status(QuoteStatus.DRAFT).build();

        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto(
                "COMPREHENSIVE", true, List.of("HYPERTENSION"), false, false, false
        );

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(quote));

        // When / Then
        assertThatThrownBy(() -> quoteService.updateCoverage(quoteId, request))
                .isInstanceOf(IneligibleQuotingDataException.class)
                .hasMessage("Cannot receive data, user is less than 65.");

        verifyNoInteractions(catalogClient, underwritingClient);
    }

    @Test
    @DisplayName("Should update coverage and persist health data when applicant age > 65")
    void updateCoverage_Success_WhenUserAgeOver65() {
        // Given
        UUID quoteId = UUID.randomUUID();
        QuoteEntity quote = QuoteEntity.builder().id(quoteId).applicantAge(70).status(QuoteStatus.DRAFT).build();
        CoverageTypeDto coverageType = new CoverageTypeDto("GOLD", "Gold Coverage", new BigDecimal("100.00"), null);
        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto("GOLD", true, List.of("DIABETES"), false, true, false);

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(quote));
        when(catalogClient.getCoverageTypeByCode("GOLD")).thenReturn(Optional.of(coverageType));
        when(premiumCalculationEngine.calculateMonthlyPremium(any(), any(), any())).thenReturn(new BigDecimal("195.00"));
        when(quoteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        QuoteResponseDto response = quoteService.updateCoverage(quoteId, request);

        // Then
        assertThat(response.calculatedMonthlyPremium()).isEqualByComparingTo("195.00");
        verify(underwritingClient).saveOrUpdateSupplementalHealth(any(SupplementalHealthRequestDto.class));
        verify(quoteRepository).save(quote);
    }

    @Test
    @DisplayName("Submit should be idempotent when quote is already SUBMITTED")
    void submitQuote_Idempotent_WhenAlreadySubmitted() {
        // Given
        UUID quoteId = UUID.randomUUID();
        QuoteEntity quote = QuoteEntity.builder().id(quoteId).status(QuoteStatus.SUBMITTED).build();

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(quote));

        // When
        QuoteResponseDto response = quoteService.submitQuote(quoteId);

        // Then
        assertThat(response.status()).isEqualTo(QuoteStatus.SUBMITTED);
        verifyNoInteractions(fulfillmentClient, quoteEventPublisher);
    }

    @Test
    @DisplayName("Submit should transition status to SUBMITTED and publish Kafka event on provider success")
    void submitQuote_Success_TransitionsToSubmittedAndPublishesEvent() {
        // Given
        UUID quoteId = UUID.randomUUID();
        QuoteEntity quote = QuoteEntity.builder()
                .id(quoteId)
                .status(QuoteStatus.DRAFT)
                .coverageTypeCode("GOLD")
                .calculatedMonthlyPremium(new BigDecimal("150.00"))
                .applicantName("Misael Rodríguez")
                .applicantEmail("misael@example.com")
                .build();

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(quote));
        when(fulfillmentClient.submitQuoteToProvider(quote)).thenReturn(true);
        when(quoteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        QuoteResponseDto response = quoteService.submitQuote(quoteId);

        // Then
        assertThat(response.status()).isEqualTo(QuoteStatus.SUBMITTED);
        verify(quoteEventPublisher).publishQuoteSubmitted(any(QuoteSubmittedEvent.class));
    }

    @Test
    @DisplayName("Submit should transition status to SUBMISSION_FAILED and throw QuoteSubmissionFailedException on provider error")
    void submitQuote_Failure_TransitionsToFailedAndThrowsException() {
        // Given
        UUID quoteId = UUID.randomUUID();
        QuoteEntity quote = QuoteEntity.builder()
                .id(quoteId)
                .status(QuoteStatus.DRAFT)
                .coverageTypeCode("GOLD")
                .calculatedMonthlyPremium(new BigDecimal("150.00"))
                .build();

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(quote));
        when(fulfillmentClient.submitQuoteToProvider(quote)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> quoteService.submitQuote(quoteId))
                .isInstanceOf(QuoteSubmissionFailedException.class);

        assertThat(quote.getStatus()).isEqualTo(QuoteStatus.SUBMISSION_FAILED);
        verify(quoteRepository).save(quote);
        verifyNoInteractions(quoteEventPublisher);
    }
}
