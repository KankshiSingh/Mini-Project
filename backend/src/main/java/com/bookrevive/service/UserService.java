package com.bookrevive.service;

import com.bookrevive.model.User;
import com.bookrevive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ─── AUTH ─────────────────────────────────────────────────────────────────

    public Map<String, Object> register(User user) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("success", false);
            response.put("message", "Email already registered");
            return response;
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        // Return user without password
        response.put("success", true);
        response.put("message", "Registration successful");
        response.put("user", sanitizeUser(saved));
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> optUser = userRepository.findByEmail(email);

        if (optUser.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }

        User user = optUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid password");
            return response;
        }

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("user", sanitizeUser(user));
        return response;
    }

    // ─── PROFILE ───────────────────────────────────────────────────────────────

    public Optional<Map<String, Object>> getUserById(String id) {
        return userRepository.findById(id)
                .map(this::sanitizeUser);
    }

    public Map<String, Object> updateUser(String id, Map<String, Object> updates) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }

        User user = optUser.get();

        if (updates.containsKey("name"))    user.setName((String) updates.get("name"));
        if (updates.containsKey("phone"))   user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("address")) user.setAddress((String) updates.get("address"));
        if (updates.containsKey("bio"))     user.setBio((String) updates.get("bio"));
        if (updates.containsKey("age"))     user.setAge((String) updates.get("age"));

        // Allow password update with encryption
        if (updates.containsKey("password") && !((String) updates.get("password")).isEmpty()) {
            user.setPassword(passwordEncoder.encode((String) updates.get("password")));
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Profile updated successfully");
        response.put("user", sanitizeUser(user));
        return response;
    }

    // ─── ACTIVITY TRACKING ─────────────────────────────────────────────────────

    public void trackPurchase(String userId, String bookId) {
        userRepository.findById(userId).ifPresent(user -> {
            if (!user.getPurchasedBookIds().contains(bookId)) {
                user.getPurchasedBookIds().add(bookId);
                userRepository.save(user);
            }
        });
    }

    public void trackSearchCategory(String userId, String category) {
        userRepository.findById(userId).ifPresent(user -> {
            if (!user.getSearchedCategories().contains(category)) {
                user.getSearchedCategories().add(category);
                userRepository.save(user);
            }
        });
    }

    public void trackDonation(String userId, String bookId) {
        userRepository.findById(userId).ifPresent(user -> {
            if (!user.getDonatedBookIds().contains(bookId)) {
                user.getDonatedBookIds().add(bookId);
                userRepository.save(user);
            }
        });
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> safe = new HashMap<>();
        safe.put("id", user.getId());
        safe.put("name", user.getName());
        safe.put("email", user.getEmail());
        safe.put("phone", user.getPhone());
        safe.put("age", user.getAge());
        safe.put("address", user.getAddress());
        safe.put("bio", user.getBio());
        safe.put("createdAt", user.getCreatedAt());
        safe.put("purchasedBookIds", user.getPurchasedBookIds());
        safe.put("donatedBookIds", user.getDonatedBookIds());
        safe.put("searchedCategories", user.getSearchedCategories());
        // password intentionally omitted
        return safe;
    }
}
