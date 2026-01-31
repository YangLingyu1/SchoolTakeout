package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.RiderApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiderApplicationRepository extends JpaRepository<RiderApplication, Integer> {
    Optional<RiderApplication> findByUserId(Integer userId);
    List<RiderApplication> findAllByUserId(Integer userId);
    List<RiderApplication> findAllByStatus(String status);
    List<RiderApplication> findAll();
}
