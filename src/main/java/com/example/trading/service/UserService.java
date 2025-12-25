package com.example.trading.service;

import com.example.trading.entity.PortfolioAccount;
import com.example.trading.entity.User;
import com.example.trading.repository.PortfolioAccountRepository;
import com.example.trading.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PortfolioAccountRepository portfolioAccountRepository;
    private final PasswordEncoder passwordEncoder; // NEW

    @Transactional
    public User registerUser(String name, String email, String rawPassword, double initialCash) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        // NEW: store hashed password
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        PortfolioAccount account = new PortfolioAccount();
        account.setUser(savedUser);
        account.setCashBalance(initialCash);
        account.setCreatedAt(LocalDateTime.now());

        portfolioAccountRepository.save(account);

        return savedUser;
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // NEW: verify password with BCrypt
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
}
