package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.User;
import api.cssc.ciallo.games.repository.UserRepository;
import api.cssc.ciallo.games.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByOpenid(String openid) {
        return userRepository.findByOpenid(openid);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateAvatar(Integer userId, String avatarUrl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAvatar(avatarUrl);
            userRepository.save(user);
        }
    }
}
