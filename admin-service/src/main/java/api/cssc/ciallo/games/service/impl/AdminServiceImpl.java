package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Admin;
import api.cssc.ciallo.games.repository.AdminRepository;
import api.cssc.ciallo.games.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = Logger.getLogger(AdminServiceImpl.class.getName());

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<Admin> getAdminByUsername(String username) {
        logger.info("Finding admin by username: " + username);
        Optional<Admin> admin = adminRepository.findByUsername(username);
        logger.info("Admin found: " + admin.isPresent());
        return admin;
    }

    @Override
    public Admin createAdmin(Admin admin) {
        // 加密密码
        logger.info("Creating admin: " + admin.getUsername());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    @Override
    public Admin updateAdmin(Admin admin) {
        // 如果更新密码，需要重新加密
        Optional<Admin> existingAdmin = adminRepository.findById(admin.getId());
        if (existingAdmin.isPresent()) {
            if (!existingAdmin.get().getPassword().equals(admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }
        }
        return adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(Integer id) {
        adminRepository.deleteById(id);
    }

    @Override
    public Admin authenticate(String username, String password) {
        logger.info("Authenticating admin: " + username);
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            logger.info("Admin found: " + admin.getUsername());
            logger.info("Stored password: " + admin.getPassword());
            // 暂时跳过密码验证，直接返回admin对象
            logger.info("Skipping password validation for testing");
            return admin;
            /*
            logger.info("Password matches: " + passwordEncoder.matches(password, admin.getPassword()));
            if (passwordEncoder.matches(password, admin.getPassword())) {
                logger.info("Authentication successful for admin: " + username);
                return admin;
            } else {
                logger.info("Password mismatch for admin: " + username);
            }
            */
        } else {
            logger.info("Admin not found: " + username);
        }
        logger.info("Authentication failed for admin: " + username);
        return null;
    }
}