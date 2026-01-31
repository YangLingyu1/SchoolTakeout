package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DormitoryRepository extends JpaRepository<Dormitory, Integer> {
    List<Dormitory> findByNightDelivery(Boolean nightDelivery);
}