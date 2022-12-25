package com.example.libraryproducer.controller;

import com.example.libraryproducer.model.BookModel;
import com.example.libraryproducer.model.LibraryEventModel;
import com.example.libraryproducer.service.LibraryEventProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by MikBac on 25.12.2022
 */

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
class LibraryEventsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventProducerService libraryEventProducerService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSuccessfullyPostAsyncDefaultLibraryEvent() throws Exception {
        // given
        final BookModel book = BookModel.builder()
                .bookId(111)
                .bookAuthor("Bob")
                .bookName("Kafka")
                .build();

        final LibraryEventModel libraryEvent = LibraryEventModel.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        // when
        final String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventProducerService
                .sendSyncLibraryEvent(isA(LibraryEventModel.class)))
                .thenReturn(new SendResult<>(null, null));

        // expect
        mockMvc.perform(post("/v1/sync/books")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void testFailPostAsyncDefaultLibraryEvent() throws Exception {
        // given
        final BookModel book = BookModel.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Kafka")
                .build();

        final LibraryEventModel libraryEvent = LibraryEventModel.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        // when
        final String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventProducerService
                .sendSyncLibraryEvent(isA(LibraryEventModel.class)))
                .thenReturn(new SendResult<>(null, null));

        // expect
        final String expectedErrorMessage = "book.bookAuthor because: must not be blank, book.bookId because: must not be null";
        mockMvc.perform(post("/v1/sync/books")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }
}