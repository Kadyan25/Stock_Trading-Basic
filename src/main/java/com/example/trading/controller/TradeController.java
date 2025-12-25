package com.example.trading.controller;

import com.example.trading.dto.TradeDto;
import com.example.trading.dto.TradeRequestDto;
import com.example.trading.entity.User;
import com.example.trading.service.TradeService;
import com.example.trading.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final UserService userService;

    @PostMapping("/buy")
    public String buy(@RequestBody TradeRequestDto request) {
        User user = userService.getUserById(request.getUserId());
        tradeService.buyStock(user, request.getSymbol(), request.getQuantity());
        return "BUY order executed";
    }

    @PostMapping("/sell")
    public String sell(@RequestBody TradeRequestDto request) {
        User user = userService.getUserById(request.getUserId());
        tradeService.sellStock(user, request.getSymbol(), request.getQuantity());
        return "SELL order executed";
    }
    // NEW: trade history for a user
    @GetMapping
    public List<TradeDto> getTrades(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return tradeService.getTradesForUser(user);
    }
}
