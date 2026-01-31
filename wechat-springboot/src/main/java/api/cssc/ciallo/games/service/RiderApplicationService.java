package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.RiderApplication;
import java.util.List;
import java.util.Optional;

public interface RiderApplicationService {
    RiderApplication createApplication(RiderApplication application);
    Optional<RiderApplication> getApplicationById(Integer id);
    Optional<RiderApplication> getApplicationByUserId(Integer userId);
    List<RiderApplication> getApplicationsByUserId(Integer userId);
    List<RiderApplication> getAllApplications();
    RiderApplication updateApplication(RiderApplication application);
    void deleteApplication(Integer id);
    RiderApplication reviewApplication(Integer id, String status, String rejectReason);
}
