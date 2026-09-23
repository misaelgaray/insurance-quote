package com.clara.insurance_quote.underwriting.service;

import com.clara.insurance_quote.underwriting.dto.SupplementalHealthRequestDto;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthResponseDto;

import java.util.Optional;
import java.util.UUID;

public interface SupplementalHealthService {
    SupplementalHealthResponseDto saveOrUpdate(SupplementalHealthRequestDto request);
    Optional<SupplementalHealthResponseDto> getByQuoteId(UUID quoteId);
}
