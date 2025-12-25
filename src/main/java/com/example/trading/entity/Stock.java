package com.example.trading.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "stocks",
        indexes = {
                @Index(name = "idx_stocks_symbol", columnList = "symbol")
        }
)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double currentPrice;

    @Column
    private Double lastRealPrice;
}
