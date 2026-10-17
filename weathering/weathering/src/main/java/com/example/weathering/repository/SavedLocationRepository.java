package com.example.weathering.repository;

import com.example.weathering.entity.SavedLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedLocationRepository extends JpaRepository<SavedLocation, Long> {
    List<SavedLocation> findAllByUserUsername(String username);
}