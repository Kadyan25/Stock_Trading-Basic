package com.example.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PerformancePointDto {
    private LocalDateTime timestamp;
    private double totalValue;
}
