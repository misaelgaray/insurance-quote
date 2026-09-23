package com.clara.insurance_quote.quoting.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateCoverageRequestDto(
        @NotBlank(message = "Coverage type code is required")
        String coverageTypeCode,

        Boolean hasPreexistingConditions,
        List<String> preexistingConditions,
        Boolean takesPrescriptionMedication,
        Boolean usesTobacco,
        Boolean needsSpouseCoverage
) {
    public boolean containsSupplementalData() {
        return (hasPreexistingConditions != null && hasPreexistingConditions)
                || (preexistingConditions != null && !preexistingConditions.isEmpty())
                || (takesPrescriptionMedication != null && takesPrescriptionMedication)
                || (usesTobacco != null && usesTobacco)
                || (needsSpouseCoverage != null && needsSpouseCoverage);
    }
}
