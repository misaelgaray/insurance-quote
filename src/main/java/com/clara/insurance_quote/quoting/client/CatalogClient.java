package com.clara.insurance_quote.quoting.client;

import com.clara.insurance_quote.catalog.dto.CoverageTypeDto;

import java.util.Optional;

public interface CatalogClient {

    /**
     * Fetches details for a specific coverage type by its unique code.
     */
    Optional<CoverageTypeDto> getCoverageTypeByCode(String code);
}
