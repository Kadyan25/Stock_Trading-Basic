package com.example.trading.controller;

import com.example.trading.dto.PortfolioSummaryDto;
import com.example.trading.entity.User;
import com.example.trading.service.PortfolioService;
import com.example.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    @GetMapping
    public PortfolioSummaryDto getPortfolio(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return portfolioService.getPortfolioSummary(user);
    }
}
