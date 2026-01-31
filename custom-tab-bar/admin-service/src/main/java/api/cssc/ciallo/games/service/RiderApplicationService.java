package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.RiderApplication;

import java.util.List;
import java.util.Optional;

public interface RiderApplicationService {
    RiderApplication getApplicationById(Integer id);
    List<RiderApplication> getApplicationsByUserId(Integer userId);
    List<RiderApplication> getApplicationsByStatus(String status);
    List<RiderApplication> getApplications();
    List<RiderApplication> getPendingApplications();
    RiderApplication createApplication(RiderApplication application);
    RiderApplication updateApplication(RiderApplication application);
    void deleteApplication(Integer id);
    Optional<RiderApplication> getLatestApplicationByUserId(Integer userId);
}