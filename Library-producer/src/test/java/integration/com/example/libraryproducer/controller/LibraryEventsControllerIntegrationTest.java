package com.example.libraryproducer.controller;

import com.example.libraryproducer.model.BookModel;
import com.example.libraryproducer.model.LibraryEventModel;
import com.example.libraryproducer.model.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by MikBac on 24.12.2022
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-books"}, partitions = 3, value = 3)
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, LibraryEventModel> consumer;

    @BeforeEach
    void setUp() {
        final JsonDeserializer<LibraryEventModel> deserializer = new JsonDeserializer<>(LibraryEventModel.class);
        deserializer.addTrustedPackages("com.example.libraryproducer.model");

        final Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), deserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
    @Order(1)
    void postAsyncDefaultLibraryEvent() {
        // given
        final BookModel book = BookModel.builder()
                .bookId(111)
                .bookAuthor("Bob")
                .bookName("Kafka")
                .build();

        final LibraryEventModel libraryEvent = LibraryEventModel.builder()
                .libraryEventUUID(null)
                .book(book)
                .build();
        final HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        final HttpEntity<LibraryEventModel> request = new HttpEntity<>(libraryEvent, headers);

        // when
        final ResponseEntity<LibraryEventModel> responseEntity = restTemplate.exchange(
                "/v1/async/books",
                HttpMethod.POST,
                request,
                LibraryEventModel.class);

        // then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        final ConsumerRecords<String, LibraryEventModel> consumerRecords = KafkaTestUtils.getRecords(consumer);

        consumerRecords.forEach(cr -> {
            final LibraryEventModel value = cr.value();
            assertEquals(book.getBookAuthor(), value.getBook().getBookAuthor());
            assertEquals(book.getBookId(), value.getBook().getBookId());
        });

    }

    @Test
    @Timeout(5)
    @Order(2)
    void putLibraryEvent() {
        // given
        final BookModel book = BookModel.builder()
                .bookId(111)
                .bookAuthor("Bob")
                .bookName("Kafka")
                .build();

        final LibraryEventModel sentLibraryEvent = LibraryEventModel.builder()
                .libraryEventUUID("111-222-333")
                .book(book)
                .build();
        final HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        final HttpEntity<LibraryEventModel> request = new HttpEntity<>(sentLibraryEvent, headers);

        //when
        final ResponseEntity<LibraryEventModel> responseEntity = restTemplate.exchange("/v1/async/books", HttpMethod.PUT, request, LibraryEventModel.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        final ConsumerRecords<String, LibraryEventModel> consumerRecords = KafkaTestUtils.getRecords(consumer);

        final LibraryEventModel receivedLibraryEvent = LibraryEventModel.builder()
                .libraryEventUUID("111-222-333")
                .libraryEventType(LibraryEventType.UPDATE)
                .book(book)
                .build();

        consumerRecords.forEach(cr -> assertEquals(receivedLibraryEvent, cr.value()));

    }

}
