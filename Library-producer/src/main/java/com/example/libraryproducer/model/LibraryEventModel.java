package com.example.libraryproducer.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by MikBac on 19.12.2022
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEventModel {

    private Integer libraryEventId;
    private LibraryEventType libraryEventType;
    @NotNull
    @Valid
    private BookModel book;

}
