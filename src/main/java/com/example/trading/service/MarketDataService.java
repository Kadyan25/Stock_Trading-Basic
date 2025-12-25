package com.example.trading.service;

import com.example.trading.entity.Stock;
import com.example.trading.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;
    private final Random random = new Random();

    @Value("${alpha.vantage.api-key:}")
    private String apiKey;

    // 1) RANDOM SIMULATION: small moves every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void simulatePrices() {
        var stocks = stockRepository.findAll();
        if (stocks.isEmpty()) return;

        for (Stock stock : stocks) {
            double oldPrice = stock.getCurrentPrice();
            if (oldPrice <= 0) continue;

            double changePercent = (random.nextDouble() * 4.0) - 2.0; // -2% to +2%
            double newPrice = oldPrice * (1 + changePercent / 100.0);
            if (newPrice < 1) newPrice = 1;

            stock.setCurrentPrice(round2(newPrice));
        }
        stockRepository.saveAll(stocks);
    }

    // 2) REAL DATA: re-anchor from Alpha Vantage every 90 seconds
    @Scheduled(fixedRate = 90000)
    public void autoUpdateFromAlpha() {
        if (apiKey == null || apiKey.isBlank()) {
            return;
        }
        updateFromAlphaVantage("IBM", "IBM");
        updateFromAlphaVantage("MSFT", "MSFT");
        updateFromAlphaVantage("AAPL", "AAPL");
    }


    public void updateFromAlphaVantage(String localSymbol, String globalSymbol) {
        if (apiKey == null || apiKey.isBlank()) return;

        String url = "https://www.alphavantage.co/query"
                + "?function=GLOBAL_QUOTE"
                + "&symbol=" + globalSymbol
                + "&apikey=" + apiKey;

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response == null) return;

        Object quoteObj = response.get("Global Quote");
        if (!(quoteObj instanceof Map<?, ?> quoteMap)) return;

        Object priceObj = quoteMap.get("05. price");
        if (priceObj == null) return;

        double price = Double.parseDouble(priceObj.toString());

        stockRepository.findBySymbol(localSymbol).ifPresent(stock -> {
        	stock.setLastRealPrice(price);
            stock.setCurrentPrice(price); 
            stockRepository.save(stock);
        });
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
