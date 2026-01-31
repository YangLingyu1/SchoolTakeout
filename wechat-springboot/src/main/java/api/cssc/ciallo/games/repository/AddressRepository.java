package api.cssc.ciallo.games.repository;

import api.cssc.ciallo.games.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(Integer userId);
    Address findByUserIdAndIsDefault(Integer userId, Boolean isDefault);
}
