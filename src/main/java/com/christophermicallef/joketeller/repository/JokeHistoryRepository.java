package com.christophermicallef.joketeller.repository;

import com.christophermicallef.joketeller.entity.JokeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JokeHistoryRepository extends JpaRepository<JokeHistory, Integer> {
}
