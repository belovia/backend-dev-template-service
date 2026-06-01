package com.selecty.weather.mcp.internal.app.model;

public record CurrentWeather(
    double latitude,
    double longitude,
    String timezone,
    String time,
    double temperatureCelsius,
    double relativeHumidityPercent,
    double windSpeedKmh,
    int weatherCode
) {}
