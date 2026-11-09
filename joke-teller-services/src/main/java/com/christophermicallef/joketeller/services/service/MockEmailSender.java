package com.christophermicallef.joketeller.services.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockEmailSender {
    private static final Logger logger = LoggerFactory.getLogger(MockEmailSender.class);

    public void sendVerificationCode(String email, String code) {
        logger.info("Mock verification email sent to {} with code {}", email, code);
    }
}
