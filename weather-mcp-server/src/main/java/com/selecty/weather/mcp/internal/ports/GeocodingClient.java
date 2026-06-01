package com.selecty.weather.mcp.internal.ports;

import com.selecty.weather.mcp.internal.app.model.GeoLocation;
import java.util.Optional;

public interface GeocodingClient {
  Optional<GeoLocation> searchCity(String city);
}
