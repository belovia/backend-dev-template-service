package com.selecty.weather.mcp.internal.app.model;

public record GeoLocation(
    String name,
    String country,
    double latitude,
    double longitude,
    String timezone
) {}
