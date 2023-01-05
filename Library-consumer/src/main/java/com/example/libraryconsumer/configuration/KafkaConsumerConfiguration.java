package com.example.libraryconsumer.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.List;

/**
 * Created by MikBac on 26.12.2022
 */

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfiguration {

    private final KafkaProperties kafkaProperties;

    public DefaultErrorHandler newErrorHandler() {

        var fixedBackOff = new FixedBackOff(1000L, 2);

        var errorHandler = new DefaultErrorHandler(fixedBackOff);

        //  Add exception types to the default list
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        errorHandler
                .setRetryListeners((record, ex, deliveryAttempt) -> {
                    LOGGER.info("Failed Record in Retry Listener  exception : {} , deliveryAttempt : {} ",
                            ex.getMessage(),
                            deliveryAttempt);
                });

        return errorHandler;
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties())));

        factory.setConcurrency(3);

        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // Custom error handler
        factory.setCommonErrorHandler(newErrorHandler());
        return factory;
    }
}


