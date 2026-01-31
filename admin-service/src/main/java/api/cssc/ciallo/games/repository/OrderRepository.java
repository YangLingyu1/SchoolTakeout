package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    List<Order> findByRiderId(Integer riderId);
    List<Order> findByStatus(Integer status);
}