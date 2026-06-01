package com.selecty.weather.mcp.internal.adapters.openmeteo;

import com.fasterxml.jackson.databind.ObjectMapper;

final class OpenMeteoJson {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private OpenMeteoJson() {}

  static ObjectMapper mapper() {
    return MAPPER;
  }
}
