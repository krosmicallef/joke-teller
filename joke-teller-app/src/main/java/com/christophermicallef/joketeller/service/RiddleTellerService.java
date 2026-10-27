package com.christophermicallef.joketeller.service;

import com.christophermicallef.joketeller.agent.RiddleTellerAgent;
import com.openai.errors.OpenAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RiddleTellerService {

    private static final Logger log = LoggerFactory.getLogger(RiddleTellerService.class);

    private final RiddleTellerAgent riddleTellerAgent;

    @Autowired
    public RiddleTellerService(RiddleTellerAgent riddleTellerAgent) {
        this.riddleTellerAgent = riddleTellerAgent;
    }

    public String tellRiddle(String userMessage) {
        String result;
        try {
            result = riddleTellerAgent.tellRiddle(userMessage);
        } catch (OpenAIException ex) {
            log.warn("Failed to generate riddle, falling back", ex);
            result = fallbackRiddle();
        }
        return result;
    }

    private String fallbackRiddle() {
        return "Why was six afraid of seven? Because seven eight (ate) nine";
    }
}
