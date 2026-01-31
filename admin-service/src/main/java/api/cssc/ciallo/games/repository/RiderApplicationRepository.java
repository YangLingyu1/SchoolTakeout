package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.RiderApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiderApplicationRepository extends JpaRepository<RiderApplication, Integer> {
    List<RiderApplication> findByUserId(Integer userId);
    List<RiderApplication> findByStatus(String status);
    Optional<RiderApplication> findTopByUserIdOrderByCreatedAtDesc(Integer userId);
}