package com.clara.insurance_quote.quoting.client.impl;

import com.clara.insurance_quote.quoting.client.FulfillmentClient;
import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpStatFulfillmentClient implements FulfillmentClient {

    private final RestClient fulfillmentRestClient;

    @Value("${app.fulfillment.target-endpoint:/200}")
    private String targetEndpoint;

    @Override
    public boolean submitQuoteToProvider(QuoteEntity quote) {
        try {
            log.info("Sending quote submission for quoteId: {} to endpoint: {}", quote.getId(), targetEndpoint);

            return Boolean.TRUE.equals(
                    fulfillmentRestClient.post()
                            .uri(targetEndpoint)
                            .body(quote)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, (req, resp) -> {
                                log.error("External provider returned HTTP status: {}", resp.getStatusCode());
                            })
                            .toEntity(String.class)
                            .getStatusCode()
                            .is2xxSuccessful()
            );
        } catch (Exception ex) {
            log.error("Network or timeout error calling fulfillment provider for quoteId {}: {}",
                    quote.getId(), ex.getMessage());
            return false;
        }
    }
}