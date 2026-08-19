package jjs.djed.services;


import jjs.djed.model.User;
import jjs.djed.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username) {
        User user = new User(username);
        return userRepository.insert(user);
    }

    public User getUser(UUID id) {
        Optional<User> ou = userRepository.findById(id);
        return ou.orElse(null);
    }

    public User updateUser(User user) {
        return userRepository.update(user);
    }


}
