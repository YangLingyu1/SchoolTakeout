package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.entity.RiderApplication;
import api.cssc.ciallo.games.entity.User;
import api.cssc.ciallo.games.repository.RiderApplicationRepository;
import api.cssc.ciallo.games.repository.RiderRepository;
import api.cssc.ciallo.games.repository.UserRepository;
import api.cssc.ciallo.games.service.RiderApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RiderApplicationServiceImpl implements RiderApplicationService {

    @Autowired
    private RiderApplicationRepository riderApplicationRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RiderApplication createApplication(RiderApplication application) {
        application.setCreatedAt(LocalDateTime.now());
        return riderApplicationRepository.save(application);
    }

    @Override
    public Optional<RiderApplication> getApplicationById(Integer id) {
        return riderApplicationRepository.findById(id);
    }

    @Override
    public Optional<RiderApplication> getApplicationByUserId(Integer userId) {
        return riderApplicationRepository.findByUserId(userId);
    }

    @Override
    public List<RiderApplication> getApplicationsByUserId(Integer userId) {
        return riderApplicationRepository.findAllByUserId(userId);
    }

    @Override
    public List<RiderApplication> getAllApplications() {
        return riderApplicationRepository.findAll();
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
    @Transactional
    public RiderApplication reviewApplication(Integer id, String status, String rejectReason) {
        Optional<RiderApplication> applicationOpt = riderApplicationRepository.findById(id);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("申请不存在");
        }

        RiderApplication application = applicationOpt.get();
        application.setStatus(status);
        riderApplicationRepository.save(application);

        if ("approved".equals(status)) {
            Optional<User> userOpt = userRepository.findById(application.getUserId());
            if (userOpt.isEmpty()) {
                throw new RuntimeException("用户不存在");
            }

            Rider rider = new Rider();
            rider.setUserId(application.getUserId());
            rider.setRealName(application.getRealName());
            rider.setIdCard(application.getIdCard());
            rider.setPhone(application.getPhone());
            rider.setPassword(application.getPassword());
            rider.setStatus("active");
            rider.setLevel("normal");
            rider.setBalance(BigDecimal.ZERO);
            rider.setTotalOrders(0);
            rider.setTotalEarnings(BigDecimal.ZERO);
            riderRepository.save(rider);

            userOpt.get().setIsRider(true);
            userOpt.get().setRiderStatus("active");
            userRepository.save(userOpt.get());
        }

        return application;
    }
}
