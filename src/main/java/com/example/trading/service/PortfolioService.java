package com.example.trading.service;

import com.example.trading.dto.HoldingDto;
import com.example.trading.dto.PerformancePointDto;
import com.example.trading.dto.PortfolioSummaryDto;
import com.example.trading.entity.Holding;
import com.example.trading.entity.PortfolioSnapshot;
import com.example.trading.entity.User;
import com.example.trading.repository.HoldingRepository;
import com.example.trading.repository.PortfolioAccountRepository;
import com.example.trading.repository.PortfolioSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioAccountRepository portfolioAccountRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioSnapshotRepository snapshotRepository;

    public PortfolioSummaryDto getPortfolioSummary(User user) {
        var account = portfolioAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<Holding> holdings = holdingRepository.findByUser(user);

        double holdingsValue = holdings.stream()
                .mapToDouble(h -> h.getQuantity() * h.getStock().getCurrentPrice())
                .sum();

        double totalValue = account.getCashBalance() + holdingsValue;

        double costBasis = holdings.stream()
                .mapToDouble(h -> h.getQuantity() * h.getAvgBuyPrice())
                .sum();

        double unrealizedPnl = holdingsValue - costBasis;

        return new PortfolioSummaryDto(
                account.getCashBalance(),
                holdingsValue,
                totalValue,
                unrealizedPnl
        );
    }

    public List<HoldingDto> getHoldings(User user) {
        List<Holding> holdings = holdingRepository.findByUser(user);

        return holdings.stream()
                .map(h -> {
                    double currentPrice = h.getStock().getCurrentPrice();
                    double value = h.getQuantity() * currentPrice;
                    double pnl = (currentPrice - h.getAvgBuyPrice()) * h.getQuantity();
                    return new HoldingDto(
                            h.getStock().getSymbol(),
                            h.getStock().getName(),
                            h.getQuantity(),
                            h.getAvgBuyPrice(),
                            currentPrice,
                            value,
                            pnl
                    );
                })
                .toList();
    }
    public void saveSnapshot(User user) {
        PortfolioSummaryDto summary = getPortfolioSummary(user);
        PortfolioSnapshot snapshot = new PortfolioSnapshot();
        snapshot.setUser(user);
        snapshot.setTotalValue(summary.getTotalValue());
        snapshot.setTimestamp(LocalDateTime.now());
        snapshotRepository.save(snapshot);
    }

    public List<PerformancePointDto> getPerformance(User user) {
        List<PortfolioSnapshot> snapshots =
                snapshotRepository.findByUserOrderByTimestampAsc(user);

        return snapshots.stream()
                .map(s -> new PerformancePointDto(s.getTimestamp(), s.getTotalValue()))
                .toList();
    }
}
