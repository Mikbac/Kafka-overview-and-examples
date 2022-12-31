package com.example.libraryconsumer.service;

import com.example.libraryconsumer.model.LibraryEventModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Created by MikBac on 26.12.2022
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventConsumerBatchOffsetService {

    private final LibraryEventsService libraryEventsService;

    @KafkaListener(
            topics = {"library-books"},
            autoStartup = "${libraryListener.startup:true}",
            groupId = "library-books-listener-group")
    public void onMessage(ConsumerRecord<String, LibraryEventModel> consumerRecord) {
        LOGGER.info("ConsumerRecord -> {} ", consumerRecord);
        libraryEventsService.processLibraryEvent(consumerRecord);
    }

}
