package com.example.libraryproducer.service;

import com.example.libraryproducer.model.LibraryEventModel;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.List;

/**
 * Created by MikBac on 22.12.2022
 */

@UtilityClass
public class KafkaEventUtil {

    public static ProducerRecord<String, LibraryEventModel> buildProducerRecord(final String key,
                                                                                final LibraryEventModel value,
                                                                                final String topic) {
        final List<Header> headers = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        return new ProducerRecord<>(topic, null, key, value, headers);
    }

}
