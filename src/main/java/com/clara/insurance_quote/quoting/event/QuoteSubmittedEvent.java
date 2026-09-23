package com.clara.insurance_quote.quoting.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record QuoteSubmittedEvent(
        UUID quoteId,
        String applicantName,
        String applicantEmail,
        String coverageTypeCode,
        BigDecimal monthlyPremium,
        OffsetDateTime submittedAt
) {}