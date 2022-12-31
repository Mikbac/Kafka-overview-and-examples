package com.example.libraryconsumer.service;

import com.example.libraryconsumer.model.LibraryEventModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Created by MikBac on 26.12.2022
 */

//@Service
@Slf4j
public class LibraryEventsConsumerManualOffsetService implements AcknowledgingMessageListener<String, LibraryEventModel> {

    /**
     * Requires:
     * factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
     */

    @Override
    @KafkaListener(topics = {"CustomTopic_library-books"})
    public void onMessage(final ConsumerRecord<String, LibraryEventModel> consumerRecord, final Acknowledgment acknowledgment) {
        LOGGER.info("ConsumerRecord (Manual Offset Consumer) -> {} ", consumerRecord);
        acknowledgment.acknowledge();
    }
}
