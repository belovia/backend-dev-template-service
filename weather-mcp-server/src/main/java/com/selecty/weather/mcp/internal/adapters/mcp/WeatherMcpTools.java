package com.selecty.weather.mcp.internal.adapters.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selecty.weather.mcp.internal.app.WeatherService;
import com.selecty.weather.mcp.internal.app.model.CurrentWeather;
import com.selecty.weather.mcp.internal.app.model.Forecast;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.JsonSchema;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WeatherMcpTools {
  private static final JsonSchema LAT_LON_SCHEMA = new JsonSchema(
      "object",
      Map.of(
          "latitude", Map.of("type", "number", "description", "WGS84 latitude"),
          "longitude", Map.of("type", "number", "description", "WGS84 longitude")
      ),
      List.of("latitude", "longitude"),
      false,
      null,
      null
  );

  private static final JsonSchema FORECAST_SCHEMA = new JsonSchema(
      "object",
      Map.of(
          "latitude", Map.of("type", "number"),
          "longitude", Map.of("type", "number"),
          "days", Map.of("type", "integer", "description", "Forecast days 1-16")
      ),
      List.of("latitude", "longitude"),
      false,
      null,
      null
  );

  private static final JsonSchema CITY_SCHEMA = new JsonSchema(
      "object",
      Map.of("city", Map.of("type", "string", "description", "City name")),
      List.of("city"),
      false,
      null,
      null
  );

  private final WeatherService weatherService;
  private final ObjectMapper objectMapper;

  public WeatherMcpTools(WeatherService weatherService, ObjectMapper objectMapper) {
    this.weatherService = weatherService;
    this.objectMapper = objectMapper;
  }

  public McpServerFeatures.SyncToolSpecification getCurrentWeatherTool() {
    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(Tool.builder()
            .name("get_current_weather")
            .description("Current weather for coordinates (Open-Meteo)")
            .inputSchema(LAT_LON_SCHEMA)
            .build())
        .callHandler(this::handleCurrentWeather)
        .build();
  }

  public McpServerFeatures.SyncToolSpecification getForecastTool() {
    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(Tool.builder()
            .name("get_forecast")
            .description("Daily forecast for coordinates (Open-Meteo)")
            .inputSchema(FORECAST_SCHEMA)
            .build())
        .callHandler(this::handleForecast)
        .build();
  }

  public McpServerFeatures.SyncToolSpecification getWeatherByCityTool() {
    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(Tool.builder()
            .name("get_weather_by_city")
            .description("Current weather for a city name (geocoding + Open-Meteo)")
            .inputSchema(CITY_SCHEMA)
            .build())
        .callHandler(this::handleWeatherByCity)
        .build();
  }

  private CallToolResult handleCurrentWeather(McpSyncServerExchange exchange, CallToolRequest request) {
    double lat = asDouble(request.arguments().get("latitude"));
    double lon = asDouble(request.arguments().get("longitude"));
    return textResult(weatherService.getCurrentWeather(lat, lon));
  }

  private CallToolResult handleForecast(McpSyncServerExchange exchange, CallToolRequest request) {
    double lat = asDouble(request.arguments().get("latitude"));
    double lon = asDouble(request.arguments().get("longitude"));
    int days = request.arguments().containsKey("days")
        ? asInt(request.arguments().get("days"))
        : 3;
    return textResult(weatherService.getForecast(lat, lon, days));
  }

  private CallToolResult handleWeatherByCity(McpSyncServerExchange exchange, CallToolRequest request) {
    String city = String.valueOf(request.arguments().get("city"));
    return textResult(weatherService.getWeatherByCity(city));
  }

  private CallToolResult textResult(Object payload) {
    try {
      String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
      return CallToolResult.builder()
          .content(List.of(new TextContent(json)))
          .isError(false)
          .build();
    } catch (JsonProcessingException e) {
      return CallToolResult.builder()
          .content(List.of(new TextContent("{\"error\":\"serialization failed\"}")))
          .isError(true)
          .build();
    }
  }

  private static double asDouble(Object value) {
    if (value instanceof Number number) {
      return number.doubleValue();
    }
    return Double.parseDouble(String.valueOf(value));
  }

  private static int asInt(Object value) {
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.parseInt(String.valueOf(value));
  }
}
