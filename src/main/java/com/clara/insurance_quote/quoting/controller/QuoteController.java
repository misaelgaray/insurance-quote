package com.clara.insurance_quote.quoting.controller;

import com.clara.insurance_quote.quoting.dto.CreateQuoteRequestDto;
import com.clara.insurance_quote.quoting.dto.QuoteResponseDto;
import com.clara.insurance_quote.quoting.dto.QuoteSummaryDto;
import com.clara.insurance_quote.quoting.entity.QuoteStatus;
import com.clara.insurance_quote.quoting.service.QuoteService;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
@Tag(name = "Quoting", description = "Endpoints for managing insurance quote lifecycles")
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    @Operation(summary = "Step 1: Initialize a new quote draft")
    public ResponseEntity<QuoteResponseDto> createQuote(@Valid @RequestBody CreateQuoteRequestDto request) {
        QuoteResponseDto response = quoteService.createQuote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List quotes, optionally filtered by status (DRAFT, SUBMITTED, SUBMISSION_FAILED, EXPIRED)")
    public ResponseEntity<List<QuoteResponseDto>> getQuotes(@RequestParam(required = false) QuoteStatus status) {
        List<QuoteResponseDto> quotes = quoteService.getAllQuotes(status);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch details of an existing quote")
    public ResponseEntity<QuoteResponseDto> getQuote(@PathVariable UUID id) {
        QuoteResponseDto response = quoteService.getQuoteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Step 3 Read-Only Review: Get quote summary including underwriting details")
    public ResponseEntity<QuoteSummaryDto> getQuoteSummary(@PathVariable UUID id) {
        QuoteSummaryDto summary = quoteService.getQuoteSummaryById(id);
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/{id}/coverage")
    @Operation(summary = "Step 2: Select/update coverage type and recalculate premium")
    public ResponseEntity<QuoteResponseDto> updateCoverage(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCoverageRequestDto request) {
        QuoteResponseDto response = quoteService.updateCoverage(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Step 3: Submit quote to external provider (Idempotent)")
    public ResponseEntity<QuoteResponseDto> submitQuote(@PathVariable UUID id) {
        QuoteResponseDto response = quoteService.submitQuote(id);
        return ResponseEntity.ok(response);
    }
}
