package com.example.weathering.controller;

import com.example.weathering.entity.SavedLocation;
import com.example.weathering.service.SavedLocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved")
@CrossOrigin(origins = "*")
public class SavedLocationController {

    private final SavedLocationService service;

    public SavedLocationController(SavedLocationService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public SavedLocation saveLocation(@RequestBody SavedLocation location, @RequestParam String username) {
        return service.save(location, username);
    }

    @GetMapping("/all")
    public List<SavedLocation> getAll(@RequestParam String username) {
        return service.getAll(username);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLocation(@PathVariable Long id, @RequestParam String username) {
        service.deleteById(id, username);
    }
}