package com.example.libraryproducer.configuration;

import lombok.experimental.UtilityClass;

/**
 * Created by MikBac on 20.12.2022
 */

@UtilityClass
public class KafkaConstants {

    public static final String KAFKA_LIBRARY_TOPIC = "library-books";
    public static final String KAFKA_LIBRARY_RETRY_TOPIC = "library-books.RETRY";
    public static final String KAFKA_LIBRARY_DLT_TOPIC = "library-books.DLT";

}
