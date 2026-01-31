package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User getUserById(Integer id);
    Optional<User> getUserByOpenid(String openid);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Integer id);
    List<User> getUsers();
    List<User> getRiders();
}