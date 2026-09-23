package com.clara.insurance_quote.quoting.service;

import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;

import java.math.BigDecimal;

public interface PremiumCalculationEngine {
    BigDecimal calculateMonthlyPremium(BigDecimal basePremium, QuoteEntity quote, UpdateCoverageRequestDto request);
}
