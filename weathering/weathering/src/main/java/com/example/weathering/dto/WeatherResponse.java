package com.example.weathering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {

    private String cityName;
    private String country; // ✅ ADD THIS NEW FIELD
    private double temperature; // Celsius
    private String weatherMain; // Rain, Clouds, Clear, etc.
    private String weatherDescription;
    private String icon;
    private double lat;
    private double lon;
}
