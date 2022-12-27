package com.example.libraryconsumer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Entity
public class BookModel {
    @Id
    private Integer bookId;
    private String bookName;
    private String bookAuthor;
    @OneToOne
    @JoinColumn(name = "id")
    private LibraryEventModel libraryEvent;
}
