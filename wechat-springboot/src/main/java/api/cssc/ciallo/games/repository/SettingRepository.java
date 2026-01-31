package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Integer> {
    Optional<Setting> findByKey(String key);
}