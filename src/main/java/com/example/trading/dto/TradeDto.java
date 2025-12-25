package com.example.trading.dto;

import com.example.trading.entity.Trade;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TradeDto {
    private Long id;
    private String symbol;
    private String name;
    private int quantity;
    private double price;
    private Trade.TradeType type;
    private LocalDateTime timestamp;
}
