package com.example.libraryconsumer.repository;

import com.example.libraryconsumer.model.LibraryEventModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by MikBac on 28.12.2022
 */

public interface LibraryEventsRepository extends JpaRepository<LibraryEventModel, Integer> {

    Optional<LibraryEventModel> findByLibraryEventUUID(String libraryEventUUID);

}
