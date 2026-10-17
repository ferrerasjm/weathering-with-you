package com.example.weathering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationSuggestion {
    private String name;    // city/place name
    private String state;   // may be null
    private String country; // country code like PH, US
    private double lat;
    private double lon;

    public String getDisplayName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (state != null && !state.isBlank()) {
            sb.append(", ").append(state);
        }
        if (country != null && !country.isBlank()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }
}
