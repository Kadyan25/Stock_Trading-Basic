package com.example.trading.controller;

import com.example.trading.dto.PerformancePointDto;
import com.example.trading.entity.User;
import com.example.trading.service.PortfolioService;
import com.example.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PerformanceController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    @GetMapping
    public List<PerformancePointDto> getPerformance(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return portfolioService.getPerformance(user);
    }
}
