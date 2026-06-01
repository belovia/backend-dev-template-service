package com.selecty.weather.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WeatherMcpApplication {
  public static void main(String[] args) {
    SpringApplication.run(WeatherMcpApplication.class, args);
  }
}
