package com.clara.insurance_quote.catalog.dto;

import java.util.UUID;

public record CoverageItemDto(
        UUID id,
        String name,
        String description,
        Boolean active
) {}