package com.example.trading.controller;

import com.example.trading.entity.User;
import com.example.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestUserController {

//    private final UserService userService;
//
//    @PostMapping("/create-user")
//    public User createUser(
//            @RequestParam String name,
//            @RequestParam String email,
//            @RequestParam(defaultValue = "password") String password,
//            @RequestParam(defaultValue = "100000") double initialCash
//    ) {
//        return userService.createUserWithPortfolio(name, email, password, initialCash);
//    }
}
