package com.clara.insurance_quote.catalog.controller;

import com.clara.insurance_quote.catalog.dto.CoverageItemDto;
import com.clara.insurance_quote.catalog.dto.CoverageTypeDto;
import com.clara.insurance_quote.catalog.dto.PreexistingConditionDto;
import com.clara.insurance_quote.catalog.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogs")
@RequiredArgsConstructor
@Tag(name = "Catalogs", description = "Endpoints for retrieving static/lookup insurance metadata")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/coverage-types")
    @Operation(summary = "Get available coverage types and base premiums")
    public ResponseEntity<List<CoverageTypeDto>> getCoverageTypes() {
        return ResponseEntity.ok(catalogService.getAllCoverageTypesWithItems());
    }

    @GetMapping("/preexisting-conditions")
    @Operation(summary = "Get active pre-existing condition options")
    public ResponseEntity<List<PreexistingConditionDto>> getPreexistingConditions() {
        return ResponseEntity.ok(catalogService.getActivePreexistingConditions());
    }

    @GetMapping("/coverage-items")
    @Operation(summary = "Get active coverage benefit items")
    public ResponseEntity<List<CoverageItemDto>> getCoverageItems() {
        return ResponseEntity.ok(catalogService.getActiveCoverageItems());
    }
}
