package com.example.libraryconsumer.service;

import com.example.libraryconsumer.model.BookModel;
import com.example.libraryconsumer.model.LibraryEventModel;
import com.example.libraryconsumer.model.LibraryEventType;
import com.example.libraryconsumer.repository.LibraryEventsRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by MikBac on 30.12.2022
 */

@SpringBootTest
@EmbeddedKafka(topics = {"library-books", "library-books.RETRY", "library-books.DLT"}, partitions = 3, value = 3)
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LibraryEventsConsumerIntegrationTest {

    @Value("${topics.retry}")
    private String retryTopic;

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

    private Consumer<String, LibraryEventModel> retryTopicConsumer;

    @BeforeEach
    void setUp() {
        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }

        final JsonDeserializer<LibraryEventModel> deserializer = new JsonDeserializer<>(LibraryEventModel.class);
        deserializer.addTrustedPackages("com.example.libraryproducer.model");

        final Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        retryTopicConsumer = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), deserializer).createConsumer();
        embeddedKafkaBroker.consumeFromEmbeddedTopics(retryTopicConsumer, retryTopic);
    }

    @AfterEach
    void tearDown() {
        libraryEventsRepository.deleteAll();
    }

    @Test
    @Order(1)
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
    @Order(2)
    void publishUpdateLibraryEvent() throws ExecutionException, InterruptedException {
        // given
        final BookModel book = BookModel.builder()
                .bookId(new Random().nextInt())
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
                .bookId(new Random().nextInt())
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
    @Order(3)
    @Timeout(30)
    void publishUpdateLibraryEventWhenEventWithUUIDNotExists() throws ExecutionException, InterruptedException {
        // given
        final BookModel book = BookModel.builder()
                .bookId(new Random().nextInt())
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
                .bookId(new Random().nextInt())
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
        verify(libraryEventConsumerBatchOffsetServiceSpy, atLeast(3)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, atLeast(4)).processLibraryEvent(isA(ConsumerRecord.class));
    }

    @Test
    @Order(4)
    void tryPublishUpdateEventWithoutUUID() throws ExecutionException, InterruptedException {
        // given
        final BookModel book = BookModel.builder()
                .bookId(new Random().nextInt())
                .bookAuthor("Bob")
                .bookName("Kafka")
                .build();

        final LibraryEventModel libraryEvent = LibraryEventModel.builder()
                .book(book)
                .libraryEventType(LibraryEventType.UPDATE)
                .build();

        kafkaTemplate.sendDefault(libraryEvent).get();

        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(3, TimeUnit.SECONDS);

        // then
        final ConsumerRecords<String, LibraryEventModel> consumerRecords = KafkaTestUtils.getRecords(retryTopicConsumer);

        final LibraryEventModel receivedLibraryEvent = LibraryEventModel.builder()
                .libraryEventType(LibraryEventType.UPDATE)
                .book(book)
                .build();

        assertEquals(1, consumerRecords.count());
        consumerRecords.forEach(cr -> assertEquals(receivedLibraryEvent, cr.value()));
    }

}