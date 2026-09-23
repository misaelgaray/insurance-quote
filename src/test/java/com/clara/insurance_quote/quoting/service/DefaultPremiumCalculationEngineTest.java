package com.clara.insurance_quote.quoting.service;

import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;
import com.clara.insurance_quote.quoting.service.impl.DefaultPremiumCalculationEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPremiumCalculationEngineTest {

    private DefaultPremiumCalculationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new DefaultPremiumCalculationEngine();
    }

    @Test
    @DisplayName("Should return base premium when age <= 65 and no supplemental factors apply")
    void calculateMonthlyPremium_BaseOnly() {
        // Given
        BigDecimal basePremium = new BigDecimal("100.00");
        QuoteEntity quote = QuoteEntity.builder().applicantAge(30).build();
        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto("BASIC", false, null, false, false, false);

        // When
        BigDecimal result = engine.calculateMonthlyPremium(basePremium, quote, request);

        // Then
        assertThat(result).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Should apply 1.5x multiplier when age > 65")
    void calculateMonthlyPremium_AgeOver65Only() {
        // Given
        BigDecimal basePremium = new BigDecimal("100.00");
        QuoteEntity quote = QuoteEntity.builder().applicantAge(68).build();
        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto("BASIC", false, null, false, false, false);

        // When
        BigDecimal result = engine.calculateMonthlyPremium(basePremium, quote, request);

        // Then (100 * 1.5 = 150.00)
        assertThat(result).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Should apply all combined multipliers for senior applicant with health risks")
    void calculateMonthlyPremium_AllMultipliersApplied() {
        // Given
        BigDecimal basePremium = new BigDecimal("100.00");
        QuoteEntity quote = QuoteEntity.builder().applicantAge(70).build(); // 1.5x
        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto(
                "GOLD",
                true,                           // Pre-existing (1.3x)
                List.of("DIABETES"),
                true,                           // Prescription med
                true,                           // Tobacco (1.2x)
                true                            // Spouse coverage (1.4x)
        );

        // When
        BigDecimal result = engine.calculateMonthlyPremium(basePremium, quote, request);

        // Calculation: 100 * 1.5 * 1.3 * 1.2 * 1.4 = 327.60
        assertThat(result).isEqualByComparingTo("327.60");
    }

    @ParameterizedTest
    @DisplayName("Should apply individual multiplier factors correctly")
    @CsvSource({
            "false, false, false, 100.00", // No factors -> 100
            "true,  false, false, 130.00", // Pre-existing -> 100 * 1.3
            "false, true,  false, 120.00", // Tobacco -> 100 * 1.2
            "false, false, true,  140.00"  // Spouse -> 100 * 1.4
    })
    void calculateMonthlyPremium_IndividualFactors(
            boolean preExisting, boolean tobacco, boolean spouse, String expectedPremium) {

        BigDecimal basePremium = new BigDecimal("100.00");
        QuoteEntity quote = QuoteEntity.builder().applicantAge(40).build();
        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto("STANDARD", preExisting, Collections.emptyList(), false, tobacco, spouse);

        BigDecimal result = engine.calculateMonthlyPremium(basePremium, quote, request);

        assertThat(result).isEqualByComparingTo(expectedPremium);
    }
}
