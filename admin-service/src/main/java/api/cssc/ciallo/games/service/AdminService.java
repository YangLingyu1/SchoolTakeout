package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Admin;
import java.util.Optional;

public interface AdminService {
    Optional<Admin> getAdminByUsername(String username);
    Admin createAdmin(Admin admin);
    Admin updateAdmin(Admin admin);
    void deleteAdmin(Integer id);
    Admin authenticate(String username, String password);
}