package com.clara.insurance_quote.quoting.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topics.quote-submitted:quote-submitted-topic}")
    private String quoteSubmittedTopic;

    @Bean
    public NewTopic quoteSubmittedTopic() {
        return TopicBuilder.name(quoteSubmittedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
