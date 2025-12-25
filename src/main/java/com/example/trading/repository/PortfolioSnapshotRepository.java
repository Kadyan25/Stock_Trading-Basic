package com.example.trading.repository;

import com.example.trading.entity.PortfolioSnapshot;
import com.example.trading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    List<PortfolioSnapshot> findByUserOrderByTimestampAsc(User user);
}
