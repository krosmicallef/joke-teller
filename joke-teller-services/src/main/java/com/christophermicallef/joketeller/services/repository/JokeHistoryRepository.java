package com.christophermicallef.joketeller.services.repository;

import com.christophermicallef.joketeller.services.entity.JokeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JokeHistoryRepository extends JpaRepository<JokeHistory, Integer> {

    @Query(value = "SELECT * FROM jokes_history ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<JokeHistory> findRandom();
}
