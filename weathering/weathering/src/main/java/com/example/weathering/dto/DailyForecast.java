package com.example.weathering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyForecast {
    private long date; // Unix timestamp
    private double tempMax;
    private double tempMin;
    private String weatherMain;
    private String icon;
}