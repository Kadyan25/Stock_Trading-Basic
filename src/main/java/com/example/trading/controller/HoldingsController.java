package com.example.trading.controller;

import com.example.trading.dto.HoldingDto;
import com.example.trading.entity.User;
import com.example.trading.service.PortfolioService;
import com.example.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holdings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HoldingsController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    @GetMapping
    public List<HoldingDto> getHoldings(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return portfolioService.getHoldings(user);
    }
}
