package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.User;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByOpenid(String openid);
    User updateUser(User user);
    void deleteUser(Integer id);
    void updateAvatar(Integer userId, String avatarUrl);
}
