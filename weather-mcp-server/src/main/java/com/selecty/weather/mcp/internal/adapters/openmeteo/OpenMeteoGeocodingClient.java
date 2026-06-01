package com.selecty.weather.mcp.internal.adapters.openmeteo;

import com.fasterxml.jackson.databind.JsonNode;
import com.selecty.weather.mcp.internal.app.model.GeoLocation;
import com.selecty.weather.mcp.internal.config.OpenMeteoProperties;
import com.selecty.weather.mcp.internal.ports.GeocodingClient;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OpenMeteoGeocodingClient implements GeocodingClient {
  private final HttpClient httpClient;
  private final OpenMeteoProperties properties;

  public OpenMeteoGeocodingClient(OpenMeteoProperties properties) {
    this.properties = properties;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  @Override
  public Optional<GeoLocation> searchCity(String city) {
    String url = properties.geocodingUrl()
        + "?name=" + URLEncoder.encode(city, StandardCharsets.UTF_8)
        + "&count=1&language=en&format=json";
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
      JsonNode root = OpenMeteoJson.mapper().readTree(response.body());
      JsonNode results = root.path("results");
      if (!results.isArray() || results.isEmpty()) {
        return Optional.empty();
      }
      JsonNode first = results.get(0);
      return Optional.of(new GeoLocation(
          first.path("name").asText(),
          first.path("country").asText(""),
          first.path("latitude").asDouble(),
          first.path("longitude").asDouble(),
          first.path("timezone").asText("")
      ));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
