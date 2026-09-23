package com.clara.insurance_quote.quoting.service;

import com.clara.insurance_quote.quoting.dto.CreateQuoteRequestDto;
import com.clara.insurance_quote.quoting.dto.QuoteResponseDto;
import com.clara.insurance_quote.quoting.dto.QuoteSummaryDto;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;
import com.clara.insurance_quote.quoting.entity.QuoteStatus;

import java.util.List;
import java.util.UUID;

public interface QuoteService {

    /**
     * Step 1: Initializes a new draft quote with applicant details.
     */
    QuoteResponseDto createQuote(CreateQuoteRequestDto request);

    /**
     * Fetches quote details by unique identifier.
     */
    QuoteResponseDto getQuoteById(UUID id);

    QuoteSummaryDto getQuoteSummaryById(UUID id);

    List<QuoteResponseDto> getAllQuotes(QuoteStatus status);

    /**
     * Step 2: Updates selected coverage type and calculates base monthly premium.
     */
    QuoteResponseDto updateCoverage(UUID id, UpdateCoverageRequestDto request);

    /**
     * Step 3 Submission
     */
    public QuoteResponseDto submitQuote(UUID id);
}
