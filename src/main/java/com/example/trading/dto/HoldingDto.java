package com.example.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HoldingDto {
    private String symbol;
    private String name;
    private int quantity;
    private double avgBuyPrice;
    private double currentPrice;
    private double value;   // quantity * currentPrice
    private double pnl;     // (currentPrice - avgBuyPrice) * quantity
}
