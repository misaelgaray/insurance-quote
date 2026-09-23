package com.clara.insurance_quote.quoting.dto;

import com.clara.insurance_quote.quoting.entity.QuoteStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record QuoteResponseDto(
        UUID id,
        QuoteStatus status,
        String applicantName,
        String applicantEmail,
        Integer applicantAge,
        String zipCode,
        String coverageTypeCode,
        BigDecimal calculatedMonthlyPremium,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
