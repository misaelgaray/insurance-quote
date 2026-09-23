package com.clara.insurance_quote.quoting.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.quote-submitted:quote-submitted-topic}")
    private String quoteSubmittedTopic;

    public void publishQuoteSubmitted(QuoteSubmittedEvent event) {
        log.info("Publishing QuoteSubmittedEvent to Kafka topic: {} for quoteId: {}", quoteSubmittedTopic, event.quoteId());
        kafkaTemplate.send(quoteSubmittedTopic, event.quoteId().toString(), event);
    }
}