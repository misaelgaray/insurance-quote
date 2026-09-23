package com.clara.insurance_quote.quoting.client;

import com.clara.insurance_quote.quoting.entity.QuoteEntity;

public interface FulfillmentClient {
    boolean submitQuoteToProvider(QuoteEntity quote);
}