package com.example.weathering.service;

import com.example.weathering.entity.User;
import com.example.weathering.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public String registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already taken!";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashPassword(password));
        userRepository.save(user);
        return "Success";
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get().getPassword().equals(hashPassword(password));
        }
        return false;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String updatePassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(hashPassword(newPassword));
            userRepository.save(user);
            return "Password updated successfully!";
        }
        return "User not found!";
    }

    public String deleteUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return "User deleted successfully!";
        }
        return "User not found!";
    }
}
