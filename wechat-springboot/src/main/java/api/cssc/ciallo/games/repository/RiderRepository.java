package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Integer> {
    Optional<Rider> findByUserId(Integer userId);
    Optional<Rider> findByPhoneAndRealName(String phone, String realName);
    List<Rider> findByStatus(String status);
    Optional<Rider> findTopByUserIdOrderByCreatedAtDesc(Integer userId);
    
    @Query("SELECT r FROM Rider r WHERE r.status = 'active' ORDER BY r.totalOrders DESC")
    List<Rider> findAllActiveRidersOrderByTotalOrders();
    
    @Query("SELECT r FROM Rider r WHERE r.status = 'active' ORDER BY r.totalEarnings DESC")
    List<Rider> findAllActiveRidersOrderByTotalEarnings();
    
    @Query("SELECT r FROM Rider r WHERE r.status = 'active' AND DATE(r.createdAt) = CURRENT_DATE ORDER BY r.totalOrders DESC")
    List<Rider> findAllActiveRidersOrderByTodayOrders();
    
    @Query("SELECT r FROM Rider r WHERE r.status = 'active' AND DATE(r.createdAt) = CURRENT_DATE ORDER BY r.totalEarnings DESC")
    List<Rider> findAllActiveRidersOrderByTodayEarnings();
}
