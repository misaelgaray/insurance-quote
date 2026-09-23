package com.clara.insurance_quote.catalog.dto;

public record PreexistingConditionDto(
        String code,
        String name,
        Boolean active
) {}
