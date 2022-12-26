package com.example.libraryconsumer.model;

import jakarta.validation.constraints.NotBlank;
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
public class BookModel {

    @NotNull
    private Integer bookId;
    @NotBlank
    private String bookName;
    @NotBlank
    private String bookAuthor;

}
