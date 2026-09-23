package com.clara.insurance_quote.quoting.service.impl;

import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;
import com.clara.insurance_quote.quoting.service.PremiumCalculationEngine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DefaultPremiumCalculationEngine implements PremiumCalculationEngine {

    private static final BigDecimal AGE_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal PREEXISTING_MULTIPLIER = new BigDecimal("1.3");
    private static final BigDecimal TOBACCO_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal SPOUSE_MULTIPLIER = new BigDecimal("1.4");

    @Override
    public BigDecimal calculateMonthlyPremium(BigDecimal basePremium, QuoteEntity quote, UpdateCoverageRequestDto request) {
        BigDecimal totalFactor = BigDecimal.ONE;

        // 1. Age Factor: > 65 applies 1.5x
        if (quote.getApplicantAge() != null && quote.getApplicantAge() > 65) {
            totalFactor = totalFactor.multiply(AGE_MULTIPLIER);
        }

        if (request != null) {
            // 2. Pre-existing Conditions Factor: Any selected applies 1.3x
            boolean hasConditions = (Boolean.TRUE.equals(request.hasPreexistingConditions()))
                    || (request.preexistingConditions() != null && !request.preexistingConditions().isEmpty());
            if (hasConditions) {
                totalFactor = totalFactor.multiply(PREEXISTING_MULTIPLIER);
            }

            // 3. Tobacco Use Factor: Yes applies 1.2x
            if (Boolean.TRUE.equals(request.usesTobacco())) {
                totalFactor = totalFactor.multiply(TOBACCO_MULTIPLIER);
            }

            // 4. Spouse Coverage Factor: Yes applies 1.4x
            if (Boolean.TRUE.equals(request.needsSpouseCoverage())) {
                totalFactor = totalFactor.multiply(SPOUSE_MULTIPLIER);
            }
        }

        return basePremium.multiply(totalFactor).setScale(2, RoundingMode.HALF_UP);
    }
}