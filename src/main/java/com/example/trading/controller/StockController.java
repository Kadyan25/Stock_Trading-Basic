package com.example.trading.controller;

import com.example.trading.entity.Stock;
import com.example.trading.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public List<Stock> getStocks() {
        return stockService.getAllStocks();
    }
}
