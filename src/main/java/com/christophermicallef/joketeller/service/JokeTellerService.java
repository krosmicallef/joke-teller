package com.christophermicallef.joketeller.service;

import com.christophermicallef.joketeller.agent.JokeTellerAgent;
import com.christophermicallef.joketeller.entity.JokeHistory;
import com.christophermicallef.joketeller.repository.JokeHistoryRepository;
import com.openai.errors.OpenAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JokeTellerService {

    private static final Logger log = LoggerFactory.getLogger(JokeTellerService.class);

    private final JokeTellerAgent jokeTellerAgent;
    private final JokeHistoryRepository jokeHistoryRepository;

    @Autowired
    public JokeTellerService(JokeTellerAgent jokeTellerAgent, JokeHistoryRepository repository) {
        this.jokeTellerAgent = jokeTellerAgent;
        this.jokeHistoryRepository = repository;
    }

    public String tellJoke(String userMessage) {
        String result;
        try {
            result = jokeTellerAgent.tellJoke(userMessage);
            saveToRepo(result);
        } catch (OpenAIException ex) {
            log.warn("Failed to generate joke, falling back", ex);
            result = fallbackJoke();
        }
        return result;
    }

    private void saveToRepo(String returnedJoke) {
        try {
            JokeHistory jokeHistory = new JokeHistory(returnedJoke);
            jokeHistoryRepository.save(jokeHistory);
        }
        catch (Throwable throwable) {
            log.error("Error persisting to repo. Exception swallowed. ", throwable);
        }
    }

    private String fallbackJoke() {
        return "ha ha";
    }
}
