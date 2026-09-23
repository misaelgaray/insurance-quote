package com.clara.insurance_quote.underwriting.dto;

import java.util.List;
import java.util.UUID;

public record SupplementalHealthRequestDto(
        UUID quoteId,
        Boolean hasPreexistingConditions,
        Boolean takesPrescriptionMedication,
        Boolean usesTobacco,
        Boolean hasSpouseCoverage,
        List<String> conditions
) {}