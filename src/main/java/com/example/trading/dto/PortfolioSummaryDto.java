package com.example.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortfolioSummaryDto {
    private double cashBalance;
    private double holdingsValue;
    private double totalValue;
    private double unrealizedProfitLoss;
}
