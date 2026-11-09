package com.christophermicallef.joketeller.services.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriber")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expires_at")
    private LocalDateTime verificationExpiresAt;

    protected Subscriber() { }

    public Subscriber(String email, String verificationCode, LocalDateTime verificationExpiresAt) {
        this.email = email;
        this.verified = false;
        this.verificationCode = verificationCode;
        this.verificationExpiresAt = verificationExpiresAt;
    }

    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public boolean isVerified() { return verified; }

    public boolean verify(String code, LocalDateTime now) {
        if (verified || verificationCode == null || verificationExpiresAt == null
                || !now.isBefore(verificationExpiresAt) || !verificationCode.equals(code)) {
            return false;
        }
        verified = true;
        verificationCode = null;
        verificationExpiresAt = null;
        return true;
    }
}
