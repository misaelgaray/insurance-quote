package com.clara.insurance_quote.quoting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.fulfillment.api-url:https://httpstat.us}")
    private String fulfillmentApiUrl;

    @Value("${app.fulfillment.connect-timeout-ms:3000}")
    private int connectTimeout;

    @Value("${app.fulfillment.read-timeout-ms:3000}")
    private int readTimeout;

    @Bean
    public RestClient fulfillmentRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(fulfillmentApiUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
