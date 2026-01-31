package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Integer> {
    Optional<Rider> findByUserId(Integer userId);
    List<Rider> findByStatus(String status);
    Optional<Rider> findTopByUserIdOrderByCreatedAtDesc(Integer userId);
}
