package com.example.libraryconsumer.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Created by MikBac on 26.12.2022
 */

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfiguration {

    @Value("${topics.retry:library-books.RETRY}")
    private String retryTopic;

    @Value("${topics.dlt:library-books.DLT}")
    private String deadLetterTopic;

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate kafkaTemplate;

    // https://docs.spring.io/spring-kafka/reference/html/#dead-letters
    public DeadLetterPublishingRecoverer publishingRecoverer() {

        return new DeadLetterPublishingRecoverer(
                kafkaTemplate, (consumerRecord, exception) -> {
            LOGGER.error("Exception in publishingRecoverer : {} ", exception.getMessage(), exception);
            if (exception.getCause() instanceof RecoverableDataAccessException) {
                return new TopicPartition(retryTopic, consumerRecord.partition());
            } else {
                return new TopicPartition(deadLetterTopic, consumerRecord.partition());
            }
        }
        );

    }

    ConsumerRecordRecoverer consumerRecordRecoverer = (record, exception) -> {
        LOGGER.error("Exception is : {} Failed Record : {} ", exception, record);
        if (exception.getCause() instanceof RecoverableDataAccessException) {
            LOGGER.info("Inside the recoverable logic");
            //Recovery Code here.
        } else {
            LOGGER.info("Inside the non recoverable logic and skipping the record : {}", record);
        }
    };

    public DefaultErrorHandler newErrorHandler() {

        // Every item is going to have a time interval 1 second
        var fixedBackOff = new FixedBackOff(1000L, 2);

        // Time interval increase per retry
        var exponentialBackOff = new ExponentialBackOffWithMaxRetries(2);
        exponentialBackOff.setInitialInterval(1_000L);
        exponentialBackOff.setMultiplier(2.0);
        exponentialBackOff.setMaxInterval(2_000L);

        // BackOff strategy fixedBackOff|exponentialBackOff
        // var errorHandler = new DefaultErrorHandler(fixedBackOff);
        var errorHandler = new DefaultErrorHandler(
                // consumerRecordRecoverer,
                publishingRecoverer(),
                exponentialBackOff
        );

        /**
         By default, the following exceptions will not be retried:
         * DeserializationException
         * MessageConversionException
         * ConversionException
         * MethodArgumentResolutionException
         * NoSuchMethodException
         * ClassCastException
         */
        // Add exception types to the default list
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        // Exception types that can be retried
        // errorHandler.addRetryableExceptions(RecoverableDataAccessException.class);

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


