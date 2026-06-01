package com.selecty.weather.mcp.internal.app;

import com.selecty.weather.mcp.internal.app.model.CurrentWeather;
import com.selecty.weather.mcp.internal.app.model.Forecast;
import com.selecty.weather.mcp.internal.app.model.GeoLocation;
import com.selecty.weather.mcp.internal.ports.GeocodingClient;
import com.selecty.weather.mcp.internal.ports.WeatherClient;
import java.util.Objects;

public class WeatherService {
  private final WeatherClient weatherClient;
  private final GeocodingClient geocodingClient;

  public WeatherService(WeatherClient weatherClient, GeocodingClient geocodingClient) {
    this.weatherClient = weatherClient;
    this.geocodingClient = geocodingClient;
  }

  public CurrentWeather getCurrentWeather(double latitude, double longitude) {
    validateCoordinates(latitude, longitude);
    return weatherClient.getCurrentWeather(latitude, longitude)
        .orElseThrow(() -> new IllegalStateException("Weather data unavailable"));
  }

  public Forecast getForecast(double latitude, double longitude, int days) {
    validateCoordinates(latitude, longitude);
    int safeDays = Math.clamp(days, 1, 16);
    return weatherClient.getForecast(latitude, longitude, safeDays)
        .orElseThrow(() -> new IllegalStateException("Forecast unavailable"));
  }

  public CurrentWeather getWeatherByCity(String city) {
    Objects.requireNonNull(city, "city");
    if (city.isBlank()) {
      throw new IllegalArgumentException("city must not be blank");
    }
    GeoLocation location = geocodingClient.searchCity(city.trim())
        .orElseThrow(() -> new IllegalArgumentException("City not found: " + city));
    return getCurrentWeather(location.latitude(), location.longitude());
  }

  private static void validateCoordinates(double latitude, double longitude) {
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("latitude must be between -90 and 90");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("longitude must be between -180 and 180");
    }
  }
}
