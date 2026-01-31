package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.RiderApplication;
import api.cssc.ciallo.games.repository.RiderApplicationRepository;
import api.cssc.ciallo.games.service.RiderApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RiderApplicationServiceImpl implements RiderApplicationService {

    @Autowired
    private RiderApplicationRepository riderApplicationRepository;

    @Override
    public RiderApplication getApplicationById(Integer id) {
        return riderApplicationRepository.findById(id).orElse(null);
    }

    @Override
    public List<RiderApplication> getApplicationsByUserId(Integer userId) {
        return riderApplicationRepository.findByUserId(userId);
    }

    @Override
    public List<RiderApplication> getApplicationsByStatus(String status) {
        return riderApplicationRepository.findByStatus(status);
    }

    @Override
    public List<RiderApplication> getApplications() {
        return riderApplicationRepository.findAll();
    }

    @Override
    public List<RiderApplication> getPendingApplications() {
        return riderApplicationRepository.findByStatus("pending");
    }

    @Override
    public RiderApplication createApplication(RiderApplication application) {
        application.setCreatedAt(LocalDateTime.now());
        return riderApplicationRepository.save(application);
    }

    @Override
    public RiderApplication updateApplication(RiderApplication application) {
        return riderApplicationRepository.save(application);
    }

    @Override
    public void deleteApplication(Integer id) {
        riderApplicationRepository.deleteById(id);
    }

    @Override
    public Optional<RiderApplication> getLatestApplicationByUserId(Integer userId) {
        return riderApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }
}