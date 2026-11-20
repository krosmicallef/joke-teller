package com.christophermicallef.joketeller.controller;

import com.christophermicallef.joketeller.security.AdminJwtInterceptor;
import com.christophermicallef.joketeller.security.JwtTokenService;
import com.christophermicallef.joketeller.services.entity.AdminUser;
import com.christophermicallef.joketeller.services.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminUserService users;
    private final JwtTokenService tokens;

    public AdminController(AdminUserService users, JwtTokenService tokens) {
        this.users = users;
        this.tokens = tokens;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request) {
        return users.authenticate(request.username(), request.password())
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(Map.of(
                        "token", tokens.createToken(user),
                        "tokenType", "Bearer",
                        "expiresIn", tokens.getLifetimeSeconds())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password")));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        users.invalidateTokens((Long) request.getAttribute(AdminJwtInterceptor.AUTHENTICATED_USER_ID));
        return ResponseEntity.ok(Map.of("message", "Logged out; the access token is no longer valid"));
    }

    @GetMapping("/users")
    public List<AdminUserSummary> listUsers() {
        return users.findAll().stream().map(AdminUserSummary::from).toList();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateAdminUserRequest request) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(AdminUserSummary.from(users.create(request.username(), request.password()))); }
        catch (IllegalStateException ex) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage())); }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @Valid @RequestBody UpdateAdminUserRequest request) {
        try {
            return ResponseEntity.ok(AdminUserSummary.from(users.update(id, request.username(), request.password())));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try {
            users.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public Map<String, String> currentUser(HttpServletRequest request) {
        return Map.of("username", (String) request.getAttribute(AdminJwtInterceptor.AUTHENTICATED_USERNAME));
    }

    public record AdminUserSummary(Long id, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        static AdminUserSummary from(AdminUser user) {
            return new AdminUserSummary(user.getId(), user.getUsername(), user.getCreatedAt(), user.getUpdatedAt());
        }
    }
}
