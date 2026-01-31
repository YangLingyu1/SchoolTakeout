package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.repository.RiderRepository;
import api.cssc.ciallo.games.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RiderServiceImpl implements RiderService {

    @Autowired
    private RiderRepository riderRepository;

    @Override
    public Rider getRiderById(Integer id) {
        return riderRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Rider> getRiderByUserId(Integer userId) {
        return riderRepository.findByUserId(userId);
    }

    @Override
    public Optional<Rider> getRiderByPhoneAndName(String phone, String name) {
        return riderRepository.findByPhoneAndRealName(phone, name);
    }

    @Override
    public List<Rider> getRiders() {
        return riderRepository.findAll();
    }

    @Override
    public List<Rider> getRidersByStatus(String status) {
        return riderRepository.findByStatus(status);
    }

    @Override
    public Rider createRider(Rider rider) {
        return riderRepository.save(rider);
    }

    @Override
    public Rider updateRider(Rider rider) {
        return riderRepository.save(rider);
    }

    @Override
    public void deleteRider(Integer id) {
        riderRepository.deleteById(id);
    }

    @Override
    public Optional<Rider> getLatestRiderByUserId(Integer userId) {
        return riderRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Rider updateRiderLevel(Integer userId) {
        Optional<Rider> riderOpt = riderRepository.findByUserId(userId);
        if (riderOpt.isPresent()) {
            Rider rider = riderOpt.get();
            if (rider.getTotalOrders() >= 50) {
                rider.setLevel("gold");
            } else if (rider.getTotalOrders() >= 10) {
                rider.setLevel("silver");
            } else {
                rider.setLevel("normal");
            }
            return riderRepository.save(rider);
        }
        return null;
    }

    @Override
    public Rider updatePaymentCode(Integer userId, String paymentCode) {
        Optional<Rider> riderOpt = riderRepository.findByUserId(userId);
        if (riderOpt.isPresent()) {
            Rider rider = riderOpt.get();
            rider.setPaymentCode(paymentCode);
            return riderRepository.save(rider);
        }
        return null;
    }

    @Override
    public Integer getRiderRankByTotalOrders(Integer userId) {
        List<Rider> riders = riderRepository.findAllActiveRidersOrderByTotalOrders();
        for (int i = 0; i < riders.size(); i++) {
            if (riders.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return riders.size() + 1;
    }

    @Override
    public Integer getRiderRankByTotalEarnings(Integer userId) {
        List<Rider> riders = riderRepository.findAllActiveRidersOrderByTotalEarnings();
        for (int i = 0; i < riders.size(); i++) {
            if (riders.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return riders.size() + 1;
    }

    @Override
    public Integer getRiderRankByTodayOrders(Integer userId) {
        List<Rider> riders = riderRepository.findAllActiveRidersOrderByTodayOrders();
        for (int i = 0; i < riders.size(); i++) {
            if (riders.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return riders.size() + 1;
    }

    @Override
    public Integer getRiderRankByTodayEarnings(Integer userId) {
        List<Rider> riders = riderRepository.findAllActiveRidersOrderByTodayEarnings();
        for (int i = 0; i < riders.size(); i++) {
            if (riders.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return riders.size() + 1;
    }
}
