package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Earnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EarningsRepository extends JpaRepository<Earnings, Integer> {
    List<Earnings> findByRiderId(Integer riderId);
}
