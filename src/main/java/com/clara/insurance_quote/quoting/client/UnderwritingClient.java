package com.clara.insurance_quote.quoting.client;

import com.clara.insurance_quote.underwriting.dto.SupplementalHealthRequestDto;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthResponseDto;

import java.util.Optional;
import java.util.UUID;

public interface UnderwritingClient {

    SupplementalHealthResponseDto saveOrUpdateSupplementalHealth(SupplementalHealthRequestDto request);

    Optional<SupplementalHealthResponseDto> getSupplementalHealthByQuoteId(UUID quoteId);
}
