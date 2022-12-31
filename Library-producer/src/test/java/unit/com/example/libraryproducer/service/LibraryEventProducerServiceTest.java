package com.example.libraryproducer.service;

import com.example.libraryproducer.model.BookModel;
import com.example.libraryproducer.model.LibraryEventModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created by MikBac on 25.12.2022
 */

@ExtendWith(MockitoExtension.class)
class LibraryEventProducerServiceTest {

    @Mock
    KafkaTemplate<String, LibraryEventModel> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducerService eventProducer;

    @Test
    void failSendCustomKeyAsyncWithProducerRecord() {
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

        final CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.obtrudeException(new RuntimeException("Exception Calling Kafka"));

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(completableFuture);
        //when

        final var exception = assertThrows(Exception.class, () -> eventProducer.sendCustomKeyAsyncWithProducerRecord(libraryEvent).get());
        assertTrue(exception.getMessage().contains("Exception Calling Kafka"));
    }

    @Test
    void successSendCustomKeyAsyncWithProducerRecord() throws JsonProcessingException, ExecutionException, InterruptedException {
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

        final String record = objectMapper.writeValueAsString(libraryEvent);
        final CompletableFuture completableFuture = new CompletableFuture();

        final ProducerRecord<String, LibraryEventModel> producerRecord = new ProducerRecord("library-books", libraryEvent.getLibraryEventUUID(), record);
        final RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-books", 3),
                1, 1, System.currentTimeMillis(), 1, 2);
        final SendResult<String, LibraryEventModel> sendResult = new SendResult<>(producerRecord, recordMetadata);

        completableFuture.obtrudeValue(sendResult);
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(completableFuture);
        //when

        final CompletableFuture<SendResult<String, LibraryEventModel>> listenableFuture = eventProducer.sendCustomKeyAsyncWithProducerRecord(libraryEvent);

        //then
        final SendResult<String, LibraryEventModel> sendResult1 = listenableFuture.get();
        assert sendResult1.getRecordMetadata().partition() == 3;

    }
}