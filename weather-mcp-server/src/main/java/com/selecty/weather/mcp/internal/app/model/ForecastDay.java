package com.selecty.weather.mcp.internal.app.model;

public record ForecastDay(
    String date,
    double temperatureMaxCelsius,
    double temperatureMinCelsius,
    double precipitationSumMm,
    int weatherCode
) {}
