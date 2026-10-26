package com.christophermicallef.joketeller.service;

import com.christophermicallef.joketeller.entity.Subscriber;
import com.christophermicallef.joketeller.repository.SubscriberRepository;
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

        subscriberRepository.save(new Subscriber(normalizedEmail));
        return true;
    }
}
