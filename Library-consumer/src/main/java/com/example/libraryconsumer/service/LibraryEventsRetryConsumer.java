package com.example.libraryconsumer.service;

import com.example.libraryconsumer.model.LibraryEventModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Created by MikBac on 07.01.2023
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventsRetryConsumer {

    private final LibraryEventsService libraryEventsService;

    @KafkaListener(topics = {"${topics.retry}"},
            autoStartup = "${retryListener.startup:true}",
            groupId = "retry-listener-group")
    public void onMessage(ConsumerRecord<String, LibraryEventModel> consumerRecord) {
        LOGGER.info("ConsumerRecord in Retry Consumer: {} ", consumerRecord);
        consumerRecord.headers().forEach(h -> LOGGER.info("header key: {}, value: {}",
                h.key(),
                new String(h.value())));
        libraryEventsService.processLibraryEvent(consumerRecord);

    }

}
