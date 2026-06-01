package com.selecty.weather.mcp.internal.config;

import com.selecty.weather.mcp.internal.app.WeatherService;
import com.selecty.weather.mcp.internal.ports.GeocodingClient;
import com.selecty.weather.mcp.internal.ports.WeatherClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Bean
  public WeatherService weatherService(WeatherClient weatherClient, GeocodingClient geocodingClient) {
    return new WeatherService(weatherClient, geocodingClient);
  }
}
