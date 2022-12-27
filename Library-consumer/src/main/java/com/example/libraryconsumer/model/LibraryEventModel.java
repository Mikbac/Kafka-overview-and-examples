package com.example.libraryconsumer.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by MikBac on 19.12.2022
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "libraryEvent")
public class LibraryEventModel {

    @Id
    @GeneratedValue
    private Integer id;
    private String libraryEventId;
    @Enumerated(EnumType.STRING)
    private LibraryEventType libraryEventType;
    @OneToOne(mappedBy = "libraryEvent", cascade = {CascadeType.ALL})
    @ToString.Exclude
    private BookModel book;

}
