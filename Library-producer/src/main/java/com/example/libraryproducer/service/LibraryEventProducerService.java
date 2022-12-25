package com.example.libraryproducer.service;

import com.example.libraryproducer.model.LibraryEventModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.libraryproducer.service.KafkaEventUtil.buildProducerRecord;

/**
 * Created by MikBac on 19.12.2022
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventProducerService {

    private static final String CUSTOM_TOPIC = "CustomTopic_library-events";

    private final KafkaTemplate<String, LibraryEventModel> kafkaTemplate;

    public void sendDefaultKeyAsyncLibraryEvent(final LibraryEventModel libraryEvent, final String libraryEventId) {

        final String key = libraryEventId != null ? libraryEventId : UUID.randomUUID().toString();

        // Uses default topic from configuration
        kafkaTemplate.sendDefault(key, libraryEvent).whenComplete((result, exception) -> {
            if (exception == null) {
                handleSuccess(key, libraryEvent, result);
            } else {
                handleFailure(exception);
            }
        });

    }

    public void sendCustomKeyAsyncLibraryEvent(final LibraryEventModel libraryEvent) {

        final String key = UUID.randomUUID().toString();

        kafkaTemplate.send(CUSTOM_TOPIC, key, libraryEvent).whenComplete((result, exception) -> {
            if (exception == null) {
                handleSuccess(key, libraryEvent, result);
            } else {
                handleFailure(exception);
            }
        });

    }

    public CompletableFuture<SendResult<String, LibraryEventModel>> sendCustomKeyAsyncWithProducerRecord(final LibraryEventModel libraryEvent) {

        final String key = UUID.randomUUID().toString();

        final ProducerRecord<String, LibraryEventModel> event = buildProducerRecord(key, libraryEvent, CUSTOM_TOPIC);

        final CompletableFuture<SendResult<String, LibraryEventModel>> completableFuture = kafkaTemplate.send(event);

        completableFuture.whenComplete((result, exception) -> {
            if (exception == null) {
                handleSuccess(key, libraryEvent, result);
            } else {
                handleFailure(exception);
            }
        });

        return completableFuture;
    }

    public SendResult<String, LibraryEventModel> sendSyncLibraryEvent(final LibraryEventModel libraryEvent) {

        final String key = UUID.randomUUID().toString();

        try {
            return kafkaTemplate.sendDefault(key, libraryEvent).get(10, TimeUnit.SECONDS);
        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            handleFailure(e);
        }

        return null;
    }

    private void handleFailure(final Throwable throwable) {
        log.error("Error Sending the Message and the exception is {}", throwable.getMessage());

        try {
            throw throwable;
        } catch (final Throwable e) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }

    }

    private void handleSuccess(final String key,
                               final LibraryEventModel value,
                               final SendResult<String, LibraryEventModel> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}",
                key,
                value,
                result.getRecordMetadata().partition());
    }

}
