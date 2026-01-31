package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.deleted = false")
    List<Order> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT o FROM Order o WHERE o.riderId = :riderId AND o.deleted = false")
    List<Order> findByRiderId(@Param("riderId") Integer riderId);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.deleted = false")
    List<Order> findByStatus(@Param("status") Integer status);

    @Query("SELECT o FROM Order o WHERE o.orderNo = :orderNo AND o.deleted = false")
    Optional<Order> findByOrderNo(@Param("orderNo") String orderNo);

    @Query("SELECT o FROM Order o WHERE o.deleted = false")
    List<Order> findAllActive();
}
