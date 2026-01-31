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
}
