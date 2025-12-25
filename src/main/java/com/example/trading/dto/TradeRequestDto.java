package com.example.trading.dto;

import lombok.Data;

@Data
public class TradeRequestDto {
    private Long userId;
    private String symbol;
    private int quantity;
}
