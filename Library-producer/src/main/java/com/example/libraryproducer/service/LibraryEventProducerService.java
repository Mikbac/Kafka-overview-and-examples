package com.example.libraryproducer.service;

import com.example.libraryproducer.model.LibraryEventModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

/**
 * Created by MikBac on 19.12.2022
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventProducerService {

    private final KafkaTemplate<Integer, LibraryEventModel> kafkaTemplate;

    public void sendLibraryEvent(final LibraryEventModel libraryEvent) {

        final Integer key = libraryEvent.getLibraryEventId();

        kafkaTemplate.sendDefault(key, libraryEvent).whenComplete((result, exception) -> {
            if (exception == null) {
                handleSuccess(key, libraryEvent, result);
            } else {
                handleFailure(exception);
            }
        });

    }

    private void handleFailure(final Throwable throwable) {
        log.error("Error Sending the Message and the exception is {}", throwable.getMessage());
    }

    private void handleSuccess(final Integer key,
                               final LibraryEventModel value,
                               final SendResult<Integer, LibraryEventModel> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}",
                key,
                value,
                result.getRecordMetadata().partition());
    }

}
