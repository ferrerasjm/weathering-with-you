package com.example.weathering.service;

import com.example.weathering.entity.SavedLocation;
import com.example.weathering.exception.WeatherException;
import com.example.weathering.repository.SavedLocationRepository;
import com.example.weathering.repository.UserRepository;
import com.example.weathering.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedLocationService {

    private final SavedLocationRepository repo;
    private final UserRepository userRepository;

    public SavedLocationService(SavedLocationRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public SavedLocation save(SavedLocation loc, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new WeatherException("User not found: " + username));
        loc.setUser(user);

        boolean exists = repo.findAllByUserUsername(username).stream().anyMatch(
                l -> l.getCityName().equalsIgnoreCase(loc.getCityName())
                        && l.getLatitude().equals(loc.getLatitude())
                        && l.getLongitude().equals(loc.getLongitude())
        );
        return exists ? loc : repo.save(loc);
    }

    public List<SavedLocation> getAll(String username) {
        return repo.findAllByUserUsername(username);
    }

    public void deleteById(Long id, String username) {
        // Ensure the location exists and belongs to the user trying to delete it
        repo.findById(id).ifPresent(location -> {
            if (location.getUser().getUsername().equals(username)) {
                repo.deleteById(id);
            } else {
                // Optionally throw an exception for unauthorized access
                System.err.println("Attempt to delete location by unauthorized user.");
            }
        });
    }
}