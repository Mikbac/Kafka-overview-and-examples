package com.example.libraryconsumer.service;

import com.example.libraryconsumer.model.BookModel;
import com.example.libraryconsumer.model.LibraryEventModel;
import com.example.libraryconsumer.model.LibraryEventType;
import com.example.libraryconsumer.repository.LibraryEventsRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by MikBac on 30.12.2022
 */

@SpringBootTest
@EmbeddedKafka(topics = {"library-books"}, partitions = 3, value = 3)
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
//@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
class LibraryEventsConsumerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry endpointRegistry;

    @Autowired
    private KafkaTemplate<String, LibraryEventModel> kafkaTemplate;

    @Autowired
    LibraryEventsRepository libraryEventsRepository;

    @SpyBean
    LibraryEventConsumerBatchOffsetService libraryEventConsumerBatchOffsetServiceSpy;

    @SpyBean
    LibraryEventsService libraryEventsServiceSpy;

    private Consumer<String, LibraryEventModel> consumer;

    @BeforeEach
    void setUp() {
        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @AfterEach
    void tearDown() {
        libraryEventsRepository.deleteAll();
    }

    @Test
    void publishNewLibraryEvent() throws ExecutionException, InterruptedException {
        // given
        final BookModel book = BookModel.builder()
                .bookId(111)
                .bookAuthor("Bob")
                .bookName("Kafka")
                .build();

        final LibraryEventModel libraryEvent = LibraryEventModel.builder()
                .libraryEventUUID("111-222-333")
                .book(book)
                .libraryEventType(LibraryEventType.NEW)
                .build();

        kafkaTemplate.sendDefault(libraryEvent).get();

        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(3, TimeUnit.SECONDS);

        // then
        verify(libraryEventConsumerBatchOffsetServiceSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        List<LibraryEventModel> libraryEventModelList = libraryEventsRepository.findAll();
        assertEquals(1, libraryEventModelList.size());

        libraryEventModelList.forEach(le -> {
            assert le.getLibraryEventId() != null;
            assertEquals(111, le.getBook().getBookId());
        });
    }

    @Test
    void publishUpdateLibraryEvent() throws ExecutionException, InterruptedException {
        // given
        final BookModel book = BookModel.builder()
                .bookId(111)
                .bookAuthor("Bob")
                .bookName("Kafka")
                .build();

        final LibraryEventModel libraryEvent = LibraryEventModel.builder()
                .libraryEventUUID("111-222-333")
                .libraryEventType(LibraryEventType.NEW)
                .book(book)
                .build();

        libraryEventsRepository.save(libraryEvent);

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        final BookModel newBook = BookModel.builder()
                .bookId(2222)
                .bookAuthor("Alice")
                .bookName("Kafka 3.0")
                .build();
        libraryEvent.setBook(newBook);

        kafkaTemplate.sendDefault(libraryEvent.getLibraryEventUUID(), libraryEvent).get();
        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(3, TimeUnit.SECONDS);

        // then
        verify(libraryEventConsumerBatchOffsetServiceSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        final LibraryEventModel libraryEventFromDb = libraryEventsRepository.findByLibraryEventUUID(libraryEvent.getLibraryEventUUID()).get();
        assertEquals("Kafka 3.0", libraryEventFromDb.getBook().getBookName());

    }

    @Test
    void publishUpdateLibraryEventWhenEventWithUUIDNotExists() throws ExecutionException, InterruptedException {
        // given
        final BookModel book = BookModel.builder()
                .bookId(111)
                .bookAuthor("Bob")
                .bookName("Kafka")
                .build();

        final LibraryEventModel libraryEvent = LibraryEventModel.builder()
                .libraryEventUUID("111-222-333")
                .libraryEventType(LibraryEventType.NEW)
                .book(book)
                .build();

        libraryEventsRepository.save(libraryEvent);

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEvent.setLibraryEventUUID(""); // makes RecoverableDataAccessException
        final BookModel newBook = BookModel.builder()
                .bookId(2222)
                .bookAuthor("Alice")
                .bookName("Kafka 3.0")
                .build();
        libraryEvent.setBook(newBook);

        kafkaTemplate.sendDefault(libraryEvent.getLibraryEventUUID(), libraryEvent).get();
        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);

        // then
        // number of invocations depends on "newErrorHandler()" in configuration
        verify(libraryEventConsumerBatchOffsetServiceSpy, times(3)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(3)).processLibraryEvent(isA(ConsumerRecord.class));

    }
}