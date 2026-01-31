package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.User;
import api.cssc.ciallo.games.repository.UserRepository;
import api.cssc.ciallo.games.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<User> getUserByOpenid(String openid) {
        return userRepository.findByOpenid(openid);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
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
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getRiders() {
        return userRepository.findAll().stream()
                .filter(User::getIsRider)
                .collect(Collectors.toList());
    }
}