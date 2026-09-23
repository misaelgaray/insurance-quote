package com.clara.insurance_quote.quoting.client.impl;

import com.clara.insurance_quote.quoting.client.UnderwritingClient;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthRequestDto;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthResponseDto;
import com.clara.insurance_quote.underwriting.service.SupplementalHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UnderwritingClientImpl implements UnderwritingClient {

    private final SupplementalHealthService supplementalHealthService;

    @Override
    public SupplementalHealthResponseDto saveOrUpdateSupplementalHealth(SupplementalHealthRequestDto request) {
        return supplementalHealthService.saveOrUpdate(request);
    }

    @Override
    public Optional<SupplementalHealthResponseDto> getSupplementalHealthByQuoteId(UUID quoteId) {
        return supplementalHealthService.getByQuoteId(quoteId);
    }
}