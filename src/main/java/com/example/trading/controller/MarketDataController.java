package com.example.trading.controller;

import com.example.trading.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;

    @PostMapping("/alpha-update")
    public String updateFromAlpha() {
        // Map your internal symbols to Alpha Vantage symbols (examples)
        marketDataService.updateFromAlphaVantage("INFY", "INFY.NS");
        marketDataService.updateFromAlphaVantage("TCS", "TCS.NS");
        marketDataService.updateFromAlphaVantage("RELIANCE", "RELIANCE.BSE");
        return "Updated prices from Alpha Vantage";
    }
}
