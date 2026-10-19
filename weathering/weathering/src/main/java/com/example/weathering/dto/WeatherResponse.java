package com.example.weathering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {

    private String cityName;
    private String country;
    private double temperature; // Celsius
    private String weatherMain;
    private String weatherDescription;
    private String icon;
    private double lat;
    private double lon;
}
