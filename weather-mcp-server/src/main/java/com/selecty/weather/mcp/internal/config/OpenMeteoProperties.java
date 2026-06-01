package com.selecty.weather.mcp.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-meteo")
public record OpenMeteoProperties(String forecastUrl, String geocodingUrl) {}
