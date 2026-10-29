package com.christophermicallef.joketeller.services.service;

import com.christophermicallef.joketeller.services.entity.Subscriber;
import com.christophermicallef.joketeller.services.repository.SubscriberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public boolean subscribe(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (subscriberRepository.existsByEmail(normalizedEmail)) {
            return false;
        }

        try {
            subscriberRepository.saveAndFlush(new Subscriber(normalizedEmail));
            return true;
        }
        catch (DataIntegrityViolationException exception) {
            // The unique database constraint handles two concurrent requests for the same email.
            if (subscriberRepository.existsByEmail(normalizedEmail)) {
                return false;
            }
            throw exception;
        }
    }
}
