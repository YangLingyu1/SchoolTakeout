package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByOpenid(String openid);
    List<User> findByIsRider(Boolean isRider);
}