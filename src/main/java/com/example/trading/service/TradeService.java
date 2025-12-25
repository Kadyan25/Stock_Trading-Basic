package com.example.trading.service;

import com.example.trading.dto.TradeDto;
import com.example.trading.entity.Holding;
import com.example.trading.entity.PortfolioAccount;
import com.example.trading.entity.Stock;
import com.example.trading.entity.Trade;
import com.example.trading.entity.User;
import com.example.trading.repository.HoldingRepository;
import com.example.trading.repository.PortfolioAccountRepository;
import com.example.trading.repository.StockRepository;
import com.example.trading.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioAccountRepository portfolioAccountRepository;

    // NEW: Inject PortfolioService to save performance snapshots after each trade.
    private final PortfolioService portfolioService;

    @Transactional
    public void buyStock(User user, String symbol, int quantity) {
        // 1) Load stock by symbol
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        // 2) Load user's portfolio account
        PortfolioAccount account = portfolioAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // 3) Check cash and deduct cost
        double totalCost = stock.getCurrentPrice() * quantity;

        if (account.getCashBalance() < totalCost) {
            throw new RuntimeException("Insufficient cash balance");
        }

        account.setCashBalance(account.getCashBalance() - totalCost);
        portfolioAccountRepository.save(account);

        // 4) Update or create holding for this stock
        Holding holding = holdingRepository.findByUserAndStock(user, stock)
                .orElseGet(() -> {
                    Holding h = new Holding();
                    h.setUser(user);
                    h.setStock(stock);
                    h.setQuantity(0);
                    h.setAvgBuyPrice(0.0);
                    return h;
                });

        int oldQty = holding.getQuantity();
        double oldAvg = holding.getAvgBuyPrice();
        int newQty = oldQty + quantity;
        double newAvg = (oldQty * oldAvg + totalCost) / newQty;

        holding.setQuantity(newQty);
        holding.setAvgBuyPrice(newAvg);
        holdingRepository.save(holding);

        // 5) Save trade record
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStock(stock);
        trade.setQuantity(quantity);
        trade.setPrice(stock.getCurrentPrice());
        trade.setType(Trade.TradeType.BUY);
        tradeRepository.save(trade);

        // NEW: Save portfolio performance snapshot after the trade.
        portfolioService.saveSnapshot(user);
    }

    @Transactional
    public void sellStock(User user, String symbol, int quantity) {
        // 1) Load stock by symbol
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        // 2) Load user's portfolio account
        PortfolioAccount account = portfolioAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // 3) Load holding and check quantity
        Holding holding = holdingRepository.findByUserAndStock(user, stock)
                .orElseThrow(() -> new RuntimeException("No holding for stock: " + symbol));

        if (holding.getQuantity() < quantity) {
            throw new RuntimeException("Not enough quantity to sell");
        }

        double proceeds = stock.getCurrentPrice() * quantity;

        // 4) Update holding quantity (or delete if zero)
        holding.setQuantity(holding.getQuantity() - quantity);
        if (holding.getQuantity() == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        // 5) Increase cash balance by proceeds
        account.setCashBalance(account.getCashBalance() + proceeds);
        portfolioAccountRepository.save(account);

        // 6) Save trade record
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStock(stock);
        trade.setQuantity(quantity);
        trade.setPrice(stock.getCurrentPrice());
        trade.setType(Trade.TradeType.SELL);
        tradeRepository.save(trade);

        // NEW: Save portfolio performance snapshot after the trade.
        portfolioService.saveSnapshot(user);
    }
    public java.util.List<TradeDto> getTradesForUser(User user) {
        var trades = tradeRepository.findByUserOrderByTimestampAsc(user);
        return trades.stream()
                .map(t -> new TradeDto(
                        t.getId(),
                        t.getStock().getSymbol(),
                        t.getStock().getName(),
                        t.getQuantity(),
                        t.getPrice(),
                        t.getType(),
                        t.getTimestamp()
                ))
                .toList();
    }
}
