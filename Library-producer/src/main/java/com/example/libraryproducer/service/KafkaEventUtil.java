package com.example.libraryproducer.service;

import com.example.libraryproducer.model.LibraryEventModel;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Created by MikBac on 22.12.2022
 */

@UtilityClass
public class KafkaEventUtil {

    public static ProducerRecord<String, LibraryEventModel> buildProducerRecord(final String key,
                                                                                final LibraryEventModel value,
                                                                                final String topic) {
        return new ProducerRecord<>(topic, null, key, value, null);
    }

}
