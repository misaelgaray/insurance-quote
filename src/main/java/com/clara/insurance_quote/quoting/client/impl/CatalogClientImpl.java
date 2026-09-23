package com.clara.insurance_quote.quoting.client.impl;

import com.clara.insurance_quote.catalog.dto.CoverageTypeDto;
import com.clara.insurance_quote.catalog.service.CatalogService;
import com.clara.insurance_quote.quoting.client.CatalogClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.clients.catalog.type", havingValue = "local", matchIfMissing = true)
public class CatalogClientImpl implements CatalogClient {

    private final CatalogService catalogService;

    @Override
    public Optional<CoverageTypeDto> getCoverageTypeByCode(String code) {
        return catalogService.getAllCoverageTypesWithItems().stream()
                .filter(ct -> ct.code().equalsIgnoreCase(code))
                .findFirst();
    }
}