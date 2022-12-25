package com.example.libraryproducer.controller;

import com.example.libraryproducer.model.LibraryEventModel;
import com.example.libraryproducer.model.LibraryEventType;
import com.example.libraryproducer.service.LibraryEventProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by MikBac on 19.12.2022
 */

@RestController
@RequiredArgsConstructor
@Slf4j
public class LibraryEventsController {

    private final LibraryEventProducerService libraryEventProducer;

    @PostMapping("/v1/async/books")
    public ResponseEntity<LibraryEventModel> postAsyncDefaultLibraryEvent(@RequestBody @Valid final LibraryEventModel libraryEvent) {

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendDefaultKeyAsyncLibraryEvent(libraryEvent, null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryEvent);
    }

    @PostMapping("/v2/async/books")
    public ResponseEntity<LibraryEventModel> postAsyncCustomLibraryEvent(@RequestBody @Valid final LibraryEventModel libraryEvent) {

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendCustomKeyAsyncLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryEvent);
    }

    @PostMapping("/v3/async/books")
    public ResponseEntity<LibraryEventModel> postAsyncCustomLibraryEventWithProducerRecord(@RequestBody @Valid final LibraryEventModel libraryEvent) {

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendCustomKeyAsyncWithProducerRecord(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryEvent);
    }

    @PostMapping("/v1/sync/books")
    public ResponseEntity<LibraryEventModel> postSyncLibraryEvent(@RequestBody @Valid final LibraryEventModel libraryEvent) {

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        final SendResult<String, LibraryEventModel> sendResult = libraryEventProducer.sendSyncLibraryEvent(libraryEvent);
        log.info("SendResult is {}", sendResult.toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryEvent);
    }

    @PutMapping("/v1/async/books")
    public ResponseEntity<?> putAsyncDefaultLibraryEvent(@RequestBody @Valid final LibraryEventModel libraryEvent) {

        if(libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendDefaultKeyAsyncLibraryEvent(libraryEvent, libraryEvent.getLibraryEventId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(libraryEvent);
    }

}
