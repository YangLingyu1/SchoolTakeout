package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserId(Integer userId);
    List<Transaction> findByRiderId(Integer riderId);
    List<Transaction> findByStatus(String status);
}