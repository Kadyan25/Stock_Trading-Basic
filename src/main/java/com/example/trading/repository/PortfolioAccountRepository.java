package com.example.trading.repository;

import com.example.trading.entity.PortfolioAccount;
import com.example.trading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioAccountRepository extends JpaRepository<PortfolioAccount, Long> {
    Optional<PortfolioAccount> findByUser(User user);
}
