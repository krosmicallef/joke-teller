package com.christophermicallef.joketeller.service;

import com.christophermicallef.joketeller.agent.JokeTellerAgent;
import com.openai.errors.OpenAIException;
import com.openai.errors.RateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JokeTellerService {

    private static final Logger log = LoggerFactory.getLogger(JokeTellerService.class);

    private final JokeTellerAgent jokeTellerAgent;

    @Autowired
    public JokeTellerService(JokeTellerAgent jokeTellerAgent) {
        this.jokeTellerAgent = jokeTellerAgent;
    }

    public String tellJoke(String userMessage) {
        try {
            return jokeTellerAgent.tellJoke(userMessage);
        } catch (RateLimitException ex) {
            log.warn("OpenAI rate limit exceeded", ex);
            return fallbackJoke();
        } catch (OpenAIException ex) {
            log.error("OpenAI API error", ex);
            return fallbackJoke();
        }
    }

    private String fallbackJoke() {
        return "ha ha";
    }
}
