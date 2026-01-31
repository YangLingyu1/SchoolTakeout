package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Earnings;
import java.util.List;

public interface EarningsService {
    Earnings createEarnings(Earnings earnings);
    Earnings getEarningsById(Integer id);
    List<Earnings> getEarningsByRiderId(Integer riderId);
    Earnings updateEarnings(Earnings earnings);
    void deleteEarnings(Integer id);
    void addEarningsForOrder(Integer riderId, Integer orderId, int itemCount, int floor);
}
