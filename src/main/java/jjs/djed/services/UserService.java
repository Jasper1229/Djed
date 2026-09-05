package jjs.djed.services;


import jjs.djed.model.User;
import jjs.djed.repositories.UserRepository;
import jjs.djed.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, char[] password) {
        try {
            String hashed = PasswordUtil.hash(password);
            User user = new User(username);

            return userRepository.insert(user);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public User getUser(UUID id) {
        Optional<User> ou = userRepository.findById(id);
        return ou.orElse(null);
    }

    public User updateUser(User user) {
        return userRepository.update(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public boolean usernameExists(String username) {
        return userRepository.usernameExists(username);
    }

}
