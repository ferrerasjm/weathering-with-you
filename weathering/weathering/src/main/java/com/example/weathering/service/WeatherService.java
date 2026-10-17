package com.example.weathering.service;

import com.example.weathering.dto.DailyForecast;
import com.example.weathering.dto.LocationSuggestion;
import com.example.weathering.dto.WeatherResponse;
import com.example.weathering.exception.WeatherException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.base-url}")
    private String weatherBaseUrl;

    @Value("${openweather.api.geo-url}")
    private String geoUrl;

    @Value("${openweather.api.onecall-url}")
    private String oneCallUrl;

    @Value("${openweather.api.reverse-geo-url}")
    private String reverseGeoUrl;


    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LocationSuggestion> searchLocations(String query) {
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&limit=10&appid=%s", geoUrl, encoded, apiKey);
            JsonNode result = restTemplate.getForObject(url, JsonNode.class);
            List<LocationSuggestion> list = new ArrayList<>();
            if (result != null && result.isArray()) {
                for (JsonNode node : result) {
                    String name = node.path("name").asText();
                    String country = node.path("country").asText();
                    String state = node.has("state") ? node.path("state").asText(null) : null;
                    double lat = node.path("lat").asDouble();
                    double lon = node.path("lon").asDouble();
                    list.add(new LocationSuggestion(name, state, country, lat, lon));
                }
            }
            return list;
        } catch (Exception ex) {
            throw new WeatherException("Failed to search locations. Please try again later.");
        }
    }

    private Map<String, String> getLocationDetailsFromCoordinates(double lat, double lon) {
        String url = String.format("%s?lat=%s&lon=%s&limit=1&appid=%s", reverseGeoUrl, lat, lon, apiKey);
        JsonNode result = restTemplate.getForObject(url, JsonNode.class);
        Map<String, String> details = new HashMap<>();
        if (result != null && result.isArray() && result.size() > 0) {
            JsonNode location = result.get(0);
            details.put("name", location.path("name").asText("Unknown"));
            details.put("country", location.path("country").asText(""));
        } else {
            details.put("name", "Unknown Location");
            details.put("country", "");
        }
        return details;
    }

    public WeatherResponse getWeatherByCoordinates(double lat, double lon) {
        try {
            // Step 1: Get location details (name and country)
            Map<String, String> locationDetails = getLocationDetailsFromCoordinates(lat, lon);
            String cityName = locationDetails.get("name");
            String country = locationDetails.get("country");

            String currentUrl = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric", weatherBaseUrl, lat, lon, apiKey);
            JsonNode currentNode = restTemplate.getForObject(currentUrl, JsonNode.class);
            if (currentNode == null) {
                throw new WeatherException("Empty response from current weather API");
            }
            double temp = currentNode.path("main").path("temp").asDouble();
            JsonNode weather = currentNode.path("weather").path(0);
            String main = weather.path("main").asText();
            String description = weather.path("description").asText();
            String icon = weather.path("icon").asText();

            return new WeatherResponse(cityName, country, temp, main, description, icon, lat, lon);

        } catch (WeatherException we) {
            throw we;
        } catch (Exception ex) {
            throw new WeatherException("Failed to fetch weather data. Please try again later.");
        }
    }

    public WeatherResponse getWeatherByCity(String city) {
        List<LocationSuggestion> matches = searchLocations(city);
        if (matches.isEmpty()) {
            throw new WeatherException("No matching city found for: " + city);
        }
        LocationSuggestion first = matches.get(0);
        return getWeatherByCoordinates(first.getLat(), first.getLon());
    }

    public List<DailyForecast> getFiveDayForecast(double lat, double lon) {
        try {
            String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric", oneCallUrl, lat, lon, apiKey);
            JsonNode node = restTemplate.getForObject(url, JsonNode.class);
            List<DailyForecast> forecastList = new ArrayList<>();

            if (node != null && node.path("list").isArray()) {
                Map<LocalDate, List<JsonNode>> forecastsByDay = new LinkedHashMap<>();
                for (JsonNode entry : node.path("list")) {
                    LocalDate date = Instant.ofEpochSecond(entry.path("dt").asLong())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate();
                    forecastsByDay.computeIfAbsent(date, ignored -> new ArrayList<>()).add(entry);
                }

                for (List<JsonNode> entries : forecastsByDay.values()) {
                    if (forecastList.size() == 5) {
                        break;
                    }
                    double tempMax = entries.stream()
                            .mapToDouble(entry -> entry.path("main").path("temp_max").asDouble())
                            .max()
                            .orElse(0);
                    double tempMin = entries.stream()
                            .mapToDouble(entry -> entry.path("main").path("temp_min").asDouble())
                            .min()
                            .orElse(0);
                    JsonNode weather = entries.get(0).path("weather").path(0);
                    forecastList.add(new DailyForecast(
                            entries.get(0).path("dt").asLong(),
                            tempMax,
                            tempMin,
                            weather.path("main").asText(),
                            weather.path("icon").asText()));
                }
            }
            return forecastList;
        } catch (Exception ex) {
            throw new WeatherException("Failed to retrieve 5-day forecast. Please try again later.");
        }
    }
}
