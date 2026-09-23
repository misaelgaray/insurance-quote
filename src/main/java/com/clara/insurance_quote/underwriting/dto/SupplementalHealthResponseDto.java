package com.clara.insurance_quote.underwriting.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SupplementalHealthResponseDto(
        UUID quoteId,
        Boolean hasPreexistingConditions,
        Boolean takesPrescriptionMedication,
        Boolean usesTobacco,
        Boolean hasSpouseCoverage,
        List<String> conditions,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}