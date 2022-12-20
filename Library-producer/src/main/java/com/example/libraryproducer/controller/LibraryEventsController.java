package com.example.libraryproducer.controller;

import com.example.libraryproducer.model.LibraryEventModel;
import com.example.libraryproducer.model.LibraryEventType;
import com.example.libraryproducer.service.LibraryEventProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by MikBac on 19.12.2022
 */

@RestController
@RequiredArgsConstructor
public class LibraryEventsController {

    private final LibraryEventProducerService libraryEventProducer;

    @PostMapping("/v1/books")
    public ResponseEntity<LibraryEventModel> postLibraryEvent(@RequestBody @Valid final LibraryEventModel libraryEvent) {

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryEvent);
    }

}
