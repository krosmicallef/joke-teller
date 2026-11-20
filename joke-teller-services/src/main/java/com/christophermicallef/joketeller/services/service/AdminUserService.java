package com.christophermicallef.joketeller.services.service;

import com.christophermicallef.joketeller.services.entity.AdminUser;
import com.christophermicallef.joketeller.services.repository.AdminUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Service
public class AdminUserService {
    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final AdminUserRepository repository;

    public AdminUserService(AdminUserRepository repository) { this.repository = repository; }

    public Optional<AdminUser> authenticate(String username, String password) {
        return repository.findByUsername(username).filter(user -> verify(password, user.getPasswordHash()));
    }

    public Optional<AdminUser> findActiveTokenUser(long userId, int tokenVersion) {
        return repository.findById(userId).filter(user -> user.getTokenVersion() == tokenVersion);
    }

    @Transactional
    public void createBootstrapUser(String username, String password) {
        if (username != null && !username.isBlank() && password != null && !password.isBlank()
                && !repository.existsByUsername(username)) {
            repository.save(new AdminUser(username, hash(password)));
        }
    }

    @Transactional
    public AdminUser create(String username, String password) {
        if (repository.existsByUsername(username)) throw new IllegalStateException("Username is already in use");
        return repository.save(new AdminUser(username, hash(password)));
    }

    @Transactional
    public AdminUser update(long id, String username, String password) {
        AdminUser user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Admin user not found"));
        String normalizedUsername = username == null ? null : username.trim();
        if (normalizedUsername != null) {
            if (normalizedUsername.isBlank()) throw new IllegalArgumentException("Username cannot be blank");
            repository.findByUsername(normalizedUsername)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> { throw new IllegalStateException("Username is already in use"); });
        }
        if (normalizedUsername == null && password == null) {
            throw new IllegalArgumentException("Provide a username or a new password");
        }
        user.updateProfile(normalizedUsername, password == null ? null : hash(password));
        return user;
    }

    @Transactional
    public void delete(long id) {
        AdminUser user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Admin user not found"));
        if (repository.count() <= 1) throw new IllegalStateException("The last admin user cannot be deleted");
        repository.delete(user);
    }

    @Transactional
    public void invalidateTokens(long id) {
        AdminUser user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Admin user not found"));
        user.invalidateTokens();
    }

    public List<AdminUser> findAll() { return repository.findAll(); }

    private static String hash(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] derived = derive(password, salt, ITERATIONS);
        return "pbkdf2-sha256$" + ITERATIONS + "$" + Base64.getEncoder().withoutPadding().encodeToString(salt)
                + "$" + Base64.getEncoder().withoutPadding().encodeToString(derived);
    }

    private static boolean verify(String password, String encoded) {
        try {
            String[] parts = encoded.split("\\$", -1);
            if (parts.length != 4 || !parts[0].equals("pbkdf2-sha256")) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 100_000 || iterations > 1_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(expected, derive(password, salt, iterations));
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (Exception ex) { throw new IllegalStateException("Password hashing is unavailable", ex); }
        finally { spec.clearPassword(); }
    }
}
