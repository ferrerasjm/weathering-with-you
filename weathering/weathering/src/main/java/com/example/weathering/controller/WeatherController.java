package com.example.weathering.controller;

import com.example.weathering.dto.DailyForecast;
import com.example.weathering.dto.LocationSuggestion;
import com.example.weathering.dto.WeatherResponse;
import com.example.weathering.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/search")
    public List<LocationSuggestion> search(@RequestParam("query") @NotBlank String query) {
        return weatherService.searchLocations(query);
    }

    @GetMapping("/by-coordinates")
    public WeatherResponse byCoordinates(
            @RequestParam("lat") @NotNull Double lat,
            @RequestParam("lon") @NotNull Double lon) {
        return weatherService.getWeatherByCoordinates(lat, lon);
    }

    @GetMapping("/by-city")
    public WeatherResponse byCity(@RequestParam("city") @NotBlank String city) {
        return weatherService.getWeatherByCity(city);
    }

    @GetMapping("/forecast")
    public List<DailyForecast> getForecastByCoordinates(
            @RequestParam("lat") @NotNull Double lat,
            @RequestParam("lon") @NotNull Double lon) {
        return weatherService.getFiveDayForecast(lat, lon);
    }
}