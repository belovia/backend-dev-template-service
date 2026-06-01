package com.selecty.weather.mcp.internal.adapters.openmeteo;

import com.fasterxml.jackson.databind.JsonNode;
import com.selecty.weather.mcp.internal.app.model.CurrentWeather;
import com.selecty.weather.mcp.internal.app.model.Forecast;
import com.selecty.weather.mcp.internal.app.model.ForecastDay;
import com.selecty.weather.mcp.internal.config.OpenMeteoProperties;
import com.selecty.weather.mcp.internal.ports.WeatherClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OpenMeteoWeatherClient implements WeatherClient {
  private final HttpClient httpClient;
  private final OpenMeteoProperties properties;

  public OpenMeteoWeatherClient(OpenMeteoProperties properties) {
    this.properties = properties;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  @Override
  public Optional<CurrentWeather> getCurrentWeather(double latitude, double longitude) {
    String url = properties.forecastUrl()
        + "?latitude=" + latitude
        + "&longitude=" + longitude
        + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code"
        + "&timezone=auto";
    return fetchJson(url).flatMap(root -> parseCurrent(root, latitude, longitude));
  }

  @Override
  public Optional<Forecast> getForecast(double latitude, double longitude, int days) {
    String url = properties.forecastUrl()
        + "?latitude=" + latitude
        + "&longitude=" + longitude
        + "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,weather_code"
        + "&forecast_days=" + days
        + "&timezone=auto";
    return fetchJson(url).flatMap(root -> parseForecast(root, latitude, longitude));
  }

  private Optional<JsonNode> fetchJson(String url) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .timeout(Duration.ofSeconds(15))
          .GET()
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        return Optional.empty();
      }
      return Optional.of(OpenMeteoJson.mapper().readTree(response.body()));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Optional<CurrentWeather> parseCurrent(JsonNode root, double latitude, double longitude) {
    JsonNode current = root.path("current");
    if (current.isMissingNode()) {
      return Optional.empty();
    }
    return Optional.of(new CurrentWeather(
        latitude,
        longitude,
        root.path("timezone").asText(""),
        current.path("time").asText(""),
        current.path("temperature_2m").asDouble(),
        current.path("relative_humidity_2m").asDouble(),
        current.path("wind_speed_10m").asDouble(),
        current.path("weather_code").asInt()
    ));
  }

  private Optional<Forecast> parseForecast(JsonNode root, double latitude, double longitude) {
    JsonNode daily = root.path("daily");
    JsonNode times = daily.path("time");
    if (!times.isArray() || times.isEmpty()) {
      return Optional.empty();
    }
    List<ForecastDay> days = new ArrayList<>();
    for (int i = 0; i < times.size(); i++) {
      days.add(new ForecastDay(
          times.get(i).asText(),
          daily.path("temperature_2m_max").get(i).asDouble(),
          daily.path("temperature_2m_min").get(i).asDouble(),
          daily.path("precipitation_sum").get(i).asDouble(),
          daily.path("weather_code").get(i).asInt()
      ));
    }
    return Optional.of(new Forecast(
        latitude,
        longitude,
        root.path("timezone").asText(""),
        days
    ));
  }

}
