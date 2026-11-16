package com.christophermicallef.joketeller.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtTokenService {
    private static final String HEADER = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    private final byte[] secret;
    private final long lifetimeSeconds;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtTokenService(@Value("${admin.jwt.secret:}") String secret,
                           @Value("${admin.jwt.lifetime-seconds:1800}") long lifetimeSeconds) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.lifetimeSeconds = lifetimeSeconds;
    }

    public String createToken(String username) {
        requireSecret();
        long now = Instant.now().getEpochSecond();
        try {
            byte[] claims = objectMapper.writeValueAsBytes(Map.of("sub", username, "iat", now, "exp", now + lifetimeSeconds));
            String unsigned = HEADER + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(claims);
            return unsigned + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(sign(unsigned));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create admin token", ex);
        }
    }

    public Optional<String> getUsername(String token) {
        try {
            requireSecret();
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3 || !HEADER.equals(parts[0])) return Optional.empty();
            String unsigned = parts[0] + "." + parts[1];
            byte[] actual = Base64.getUrlDecoder().decode(parts[2]);
            if (!java.security.MessageDigest.isEqual(sign(unsigned), actual)) return Optional.empty();
            Map<?, ?> claims = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), Map.class);
            Object subject = claims.get("sub");
            Object expiresAt = claims.get("exp");
            if (!(subject instanceof String username) || !(expiresAt instanceof Number expiry)
                    || expiry.longValue() <= Instant.now().getEpochSecond()) return Optional.empty();
            return Optional.of(username);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public long getLifetimeSeconds() { return lifetimeSeconds; }

    private byte[] sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(value.getBytes(StandardCharsets.US_ASCII));
        } catch (Exception ex) { throw new IllegalStateException("JWT signing is unavailable", ex); }
    }

    private void requireSecret() {
        if (secret.length < 32) throw new IllegalStateException("ADMIN_JWT_SECRET must contain at least 32 characters");
        if (lifetimeSeconds <= 0) throw new IllegalStateException("admin.jwt.lifetime-seconds must be positive");
    }
}
