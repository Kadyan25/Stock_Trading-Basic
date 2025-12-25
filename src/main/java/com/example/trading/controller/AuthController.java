package com.example.trading.controller;

import com.example.trading.dto.AuthResponseDto;
import com.example.trading.dto.LoginRequestDto;
import com.example.trading.dto.RegisterRequestDto;
import com.example.trading.entity.User;
import com.example.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public AuthResponseDto register(@RequestBody RegisterRequestDto request) {
        // For demo, give new users 100000 virtual cash
        User user = userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                100000.0
        );

        // Token: just userId as string (simple demo, not production)
        String token = String.valueOf(user.getId());
        return new AuthResponseDto(user.getId(), token, user.getName(), user.getEmail());
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto request) {
        User user = userService.login(request.getEmail(), request.getPassword());
        String token = String.valueOf(user.getId());
        return new AuthResponseDto(user.getId(), token, user.getName(), user.getEmail());
    }
}
