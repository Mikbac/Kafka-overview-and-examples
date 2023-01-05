package com.example.libraryconsumer.service;

import com.example.libraryconsumer.model.LibraryEventModel;
import com.example.libraryconsumer.repository.LibraryEventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by MikBac on 28.12.2022
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventsService {

    private final LibraryEventsRepository libraryEventsRepository;

    public void processLibraryEvent(ConsumerRecord<String, LibraryEventModel> consumerRecord) {
        LOGGER.info("Author : {} ", consumerRecord.value().getBook().getBookAuthor());
        final LibraryEventModel libraryEvent = consumerRecord.value();

        switch (libraryEvent.getLibraryEventType()) {
            case NEW:
                save(libraryEvent);
                break;
            case UPDATE:
                validate(libraryEvent);
                save(libraryEvent);
                break;
            default:
                LOGGER.error("Invalid Library Event Type");
        }
    }

    private void validate(LibraryEventModel libraryEvent) {

        if (libraryEvent != null && libraryEvent.getLibraryEventUUID().isBlank()) {
            throw new RecoverableDataAccessException("Access data issue.");
        }

        Optional<LibraryEventModel> libraryEventOptional = libraryEventsRepository.findByLibraryEventUUID(libraryEvent.getLibraryEventUUID());
        if (!libraryEventOptional.isPresent()) {
            throw new IllegalArgumentException("Not a valid library Event");
        }

        LOGGER.info("Validation is successful for the library Event : {} ", libraryEventOptional.get());
    }

    private void save(LibraryEventModel libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        LOGGER.info("Successfully Persisted the library Event {} ", libraryEvent);
    }

}
