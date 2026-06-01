package com.selecty.weather.mcp.internal.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.selecty.weather.mcp.internal.app.model.CurrentWeather;
import com.selecty.weather.mcp.internal.app.model.GeoLocation;
import com.selecty.weather.mcp.internal.ports.GeocodingClient;
import com.selecty.weather.mcp.internal.ports.WeatherClient;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {
  @Mock
  private WeatherClient weatherClient;

  @Mock
  private GeocodingClient geocodingClient;

  @InjectMocks
  private WeatherService weatherService;

  @Test
  void getCurrentWeather_returnsDataFromClient() {
    CurrentWeather expected = new CurrentWeather(55.75, 37.62, "Europe/Moscow", "2026-01-01T12:00", 1.0, 80.0, 10.0, 0);
    when(weatherClient.getCurrentWeather(55.75, 37.62)).thenReturn(Optional.of(expected));

    CurrentWeather actual = weatherService.getCurrentWeather(55.75, 37.62);

    assertEquals(expected, actual);
  }

  @Test
  void getWeatherByCity_resolvesLocationThenFetchesWeather() {
    GeoLocation moscow = new GeoLocation("Moscow", "Russia", 55.75, 37.62, "Europe/Moscow");
    CurrentWeather expected = new CurrentWeather(55.75, 37.62, "Europe/Moscow", "2026-01-01T12:00", 1.0, 80.0, 10.0, 0);
    when(geocodingClient.searchCity("Moscow")).thenReturn(Optional.of(moscow));
    when(weatherClient.getCurrentWeather(55.75, 37.62)).thenReturn(Optional.of(expected));

    CurrentWeather actual = weatherService.getWeatherByCity("Moscow");

    assertEquals(expected, actual);
  }

  @Test
  void getCurrentWeather_rejectsInvalidLatitude() {
    assertThrows(IllegalArgumentException.class, () -> weatherService.getCurrentWeather(100, 0));
  }
}
