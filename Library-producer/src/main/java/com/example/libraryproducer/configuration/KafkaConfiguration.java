package com.example.libraryproducer.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import static com.example.libraryproducer.configuration.KafkaConstants.KAFKA_LIBRARY_TOPIC;

/**
 * Created by MikBac on 19.12.2022
 */

@Configuration
@Profile("local")
public class KafkaConfiguration {

    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder.name(KAFKA_LIBRARY_TOPIC)
                .partitions(4)
                .replicas(3)
                .build();
    }

}
