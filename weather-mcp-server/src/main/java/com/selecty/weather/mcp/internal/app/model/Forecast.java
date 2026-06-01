package com.selecty.weather.mcp.internal.app.model;

import java.util.List;

public record Forecast(
    double latitude,
    double longitude,
    String timezone,
    List<ForecastDay> days
) {}
