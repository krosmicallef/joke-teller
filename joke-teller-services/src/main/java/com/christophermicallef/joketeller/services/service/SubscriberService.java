package com.christophermicallef.joketeller.services.service;

import com.christophermicallef.joketeller.services.entity.Subscriber;
import com.christophermicallef.joketeller.services.repository.SubscriberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class SubscriberService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_BOUND = 1_000_000;

    private final SubscriberRepository subscriberRepository;
    private final MockEmailSender mockEmailSender;

    public SubscriberService(SubscriberRepository subscriberRepository, MockEmailSender mockEmailSender) {
        this.subscriberRepository = subscriberRepository;
        this.mockEmailSender = mockEmailSender;
    }

    @Transactional
    public boolean subscribe(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (subscriberRepository.existsByEmail(normalizedEmail)) {
            return false;
        }

        String code = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(CODE_BOUND));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        try {
            subscriberRepository.saveAndFlush(new Subscriber(normalizedEmail, code, expiresAt));
        }
        catch (DataIntegrityViolationException exception) {
            // The unique database constraint handles two concurrent requests for the same email.
            if (subscriberRepository.existsByEmail(normalizedEmail)) {
                return false;
            }
            throw exception;
        }
        mockEmailSender.sendVerificationCode(normalizedEmail, code);
        return true;
    }

    @Transactional
    public boolean verifySubscriber(String email, String code) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return subscriberRepository.findByEmail(normalizedEmail)
                .map(subscriber -> subscriber.verify(code, LocalDateTime.now()))
                .orElse(false);
    }

    public Page<Subscriber> findSubscribers(Pageable pageable) {
        return subscriberRepository.findAll(pageable);
    }
}
