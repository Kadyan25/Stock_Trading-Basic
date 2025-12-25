package com.example.trading.repository;

import com.example.trading.entity.Holding;
import com.example.trading.entity.User;
import com.example.trading.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByUser(User user);
    Optional<Holding> findByUserAndStock(User user, Stock stock);
}
