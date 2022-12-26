package com.example.libraryconsumer.service;

import com.example.libraryconsumer.model.LibraryEventModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Created by MikBac on 26.12.2022
 */

@Service
@Slf4j
public class LibraryEventConsumerService {

    @KafkaListener(
            topics = {"library-books"},
            autoStartup = "${libraryListener.startup:true}",
            groupId = "library-books-listener-group")
    public void onMessage(ConsumerRecord<String, LibraryEventModel> consumerRecord) {

        log.info("ConsumerRecord -> {} ", consumerRecord);
        log.info("Author : {} ", consumerRecord.value().getBook().getBookAuthor());

    }

}
