package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Earnings;
import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.repository.EarningsRepository;
import api.cssc.ciallo.games.service.EarningsService;
import api.cssc.ciallo.games.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EarningsServiceImpl implements EarningsService {

    @Autowired
    private EarningsRepository earningsRepository;

    @Autowired
    private RiderService riderService;

    @Override
    public Earnings createEarnings(Earnings earnings) {
        return earningsRepository.save(earnings);
    }

    @Override
    public Earnings getEarningsById(Integer id) {
        return earningsRepository.findById(id).orElse(null);
    }

    @Override
    public List<Earnings> getEarningsByRiderId(Integer riderId) {
        return earningsRepository.findByRiderId(riderId);
    }

    @Override
    public Earnings updateEarnings(Earnings earnings) {
        return earningsRepository.save(earnings);
    }

    @Override
    public void deleteEarnings(Integer id) {
        earningsRepository.deleteById(id);
    }

    @Override
    public void addEarningsForOrder(Integer riderId, Integer orderId, int itemCount, int floor) {
        BigDecimal amount = new BigDecimal("0.1").multiply(new BigDecimal(floor + itemCount));
        
        Earnings earnings = new Earnings();
        earnings.setRiderId(riderId);
        earnings.setOrderId(orderId);
        earnings.setAmount(amount);
        earnings.setType("delivery_fee");
        earningsRepository.save(earnings);
        
        Optional<Rider> riderOpt = riderService.getRiderByUserId(riderId);
        if (riderOpt.isPresent()) {
            Rider rider = riderOpt.get();
            rider.setTotalEarnings(rider.getTotalEarnings().add(amount));
            rider.setBalance(rider.getBalance().add(amount));
            riderService.updateRider(rider);
        }
    }
}
