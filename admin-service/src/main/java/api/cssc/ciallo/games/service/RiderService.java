package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Rider;

import java.util.List;
import java.util.Optional;

public interface RiderService {
    Rider getRiderById(Integer id);
    Optional<Rider> getRiderByUserId(Integer userId);
    List<Rider> getRiders();
    List<Rider> getRidersByStatus(String status);
    Rider createRider(Rider rider);
    Rider updateRider(Rider rider);
    void deleteRider(Integer id);
    Optional<Rider> getLatestRiderByUserId(Integer userId);
}
