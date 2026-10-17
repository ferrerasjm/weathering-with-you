package com.example.weathering;

import com.example.weathering.entity.User;
import com.example.weathering.exception.WeatherException;
import com.example.weathering.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PublicDataSafetyTests {
    private WeatherService service;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        service = new WeatherService(restTemplate);
        ReflectionTestUtils.setField(service, "apiKey", "test-only-api-key");
        for (String field : new String[]{"weatherBaseUrl", "geoUrl", "oneCallUrl", "reverseGeoUrl"}) {
            ReflectionTestUtils.setField(service, field, "https://example.invalid/weather");
        }
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenThrow(new RuntimeException("Request failed: ?appid=test-only-api-key"));
    }

    @Test
    void userResponsesDoNotIncludePasswordHashes() throws Exception {
        User user = new User();
        user.setUsername("example-user");
        user.setPassword("test-only-password-hash");
        JsonNode json = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(user));
        assertEquals("example-user", json.path("username").asText());
        assertFalse(json.has("password"));
        assertEquals("test-only-password-hash", user.getPassword());
    }

    @Test
    void locationErrorsDoNotExposeApiKeys() {
        assertSafeError(assertThrows(WeatherException.class, () -> service.searchLocations("Manila")));
    }

    @Test
    void coordinateErrorsDoNotExposeApiKeys() {
        assertSafeError(assertThrows(WeatherException.class, () -> service.getWeatherByCoordinates(14.6, 121.0)));
    }

    @Test
    void forecastErrorsDoNotExposeApiKeys() {
        assertSafeError(assertThrows(WeatherException.class, () -> service.getFiveDayForecast(14.6, 121.0)));
    }

    private void assertSafeError(WeatherException error) {
        assertFalse(error.getMessage().contains("test-only-api-key"));
        assertFalse(error.getMessage().contains("appid="));
        assertNull(error.getCause());
    }
}
