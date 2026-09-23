package com.clara.insurance_quote.underwriting.service.impl;

import com.clara.insurance_quote.underwriting.entity.SupplementalHealthEntity;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthRequestDto;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthResponseDto;
import com.clara.insurance_quote.underwriting.repository.SupplementalHealthRepository;
import com.clara.insurance_quote.underwriting.service.SupplementalHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplementalHealthServiceImpl implements SupplementalHealthService {

    private final SupplementalHealthRepository repository;

    @Override
    public SupplementalHealthResponseDto saveOrUpdate(SupplementalHealthRequestDto request) {
        String conditionsCsv = !CollectionUtils.isEmpty(request.conditions())
                ? String.join(",", request.conditions())
                : null;

        SupplementalHealthEntity entity = repository.findById(request.quoteId())
                .orElseGet(() -> SupplementalHealthEntity.builder()
                        .quoteId(request.quoteId())
                        .build());

        entity.setHasPreexistingConditions(Boolean.TRUE.equals(request.hasPreexistingConditions()));
        entity.setTakesPrescriptionMedication(Boolean.TRUE.equals(request.takesPrescriptionMedication()));
        entity.setUsesTobacco(Boolean.TRUE.equals(request.usesTobacco()));
        entity.setHasSpouseCoverage(Boolean.TRUE.equals(request.hasSpouseCoverage()));
        entity.setConditions(conditionsCsv);

        SupplementalHealthEntity saved = repository.save(entity);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplementalHealthResponseDto> getByQuoteId(UUID quoteId) {
        return repository.findById(quoteId).map(this::mapToDto);
    }

    private SupplementalHealthResponseDto mapToDto(SupplementalHealthEntity entity) {
        List<String> conditionsList = StringUtils.hasText(entity.getConditions())
                ? Arrays.asList(entity.getConditions().split(","))
                : Collections.emptyList();

        return new SupplementalHealthResponseDto(
                entity.getQuoteId(),
                entity.getHasPreexistingConditions(),
                entity.getTakesPrescriptionMedication(),
                entity.getUsesTobacco(),
                entity.getHasSpouseCoverage(),
                conditionsList,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}