package com.clara.insurance_quote.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record CoverageTypeDto(
        String code,
        String name,
        BigDecimal basePremium,
        List<CoverageItemDto> coverageItems
) {}