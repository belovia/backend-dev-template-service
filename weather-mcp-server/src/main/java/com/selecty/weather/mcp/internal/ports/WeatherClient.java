package com.selecty.weather.mcp.internal.ports;

import com.selecty.weather.mcp.internal.app.model.CurrentWeather;
import com.selecty.weather.mcp.internal.app.model.Forecast;
import java.util.Optional;

public interface WeatherClient {
  Optional<CurrentWeather> getCurrentWeather(double latitude, double longitude);

  Optional<Forecast> getForecast(double latitude, double longitude, int days);
}
