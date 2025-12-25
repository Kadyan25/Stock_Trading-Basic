package com.example.trading.repository;

import com.example.trading.entity.Trade;
import com.example.trading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUserOrderByTimestampAsc(User user);
}
