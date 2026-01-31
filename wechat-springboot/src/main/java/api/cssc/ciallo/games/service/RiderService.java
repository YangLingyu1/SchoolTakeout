package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Rider;

import java.util.List;
import java.util.Optional;

public interface RiderService {
    Rider getRiderById(Integer id);
    Optional<Rider> getRiderByUserId(Integer userId);
    Optional<Rider> getRiderByPhoneAndName(String phone, String name);
    List<Rider> getRiders();
    List<Rider> getRidersByStatus(String status);
    Rider createRider(Rider rider);
    Rider updateRider(Rider rider);
    void deleteRider(Integer id);
    Optional<Rider> getLatestRiderByUserId(Integer userId);
    Rider updateRiderLevel(Integer userId);
    Rider updatePaymentCode(Integer userId, String paymentCode);
    Integer getRiderRankByTotalOrders(Integer userId);
    Integer getRiderRankByTotalEarnings(Integer userId);
    Integer getRiderRankByTodayOrders(Integer userId);
    Integer getRiderRankByTodayEarnings(Integer userId);
}
