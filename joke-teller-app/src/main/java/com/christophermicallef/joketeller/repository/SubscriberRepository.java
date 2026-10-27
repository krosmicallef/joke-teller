package com.christophermicallef.joketeller.repository;

import com.christophermicallef.joketeller.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {

    boolean existsByEmail(String email);
}
