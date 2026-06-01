package com.selecty.weather.mcp.internal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selecty.weather.mcp.internal.adapters.mcp.WeatherMcpTools;
import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {
  private McpSyncServer mcpSyncServer;

  @Bean
  public JacksonMcpJsonMapper mcpJsonMapper(ObjectMapper objectMapper) {
    return new JacksonMcpJsonMapper(objectMapper);
  }

  @Bean
  public HttpServletSseServerTransportProvider mcpTransportProvider(JacksonMcpJsonMapper jsonMapper) {
    return HttpServletSseServerTransportProvider.builder()
        .jsonMapper(jsonMapper)
        .messageEndpoint("/mcp/message")
        .sseEndpoint("/sse")
        .build();
  }

  @Bean
  public McpSyncServer mcpSyncServer(
      HttpServletSseServerTransportProvider transportProvider,
      WeatherMcpTools weatherMcpTools
  ) {
    mcpSyncServer = McpServer.sync(transportProvider)
        .serverInfo("weather-mcp-server", "0.0.1")
        .capabilities(ServerCapabilities.builder()
            .tools(true)
            .logging()
            .build())
        .tools(
            weatherMcpTools.getCurrentWeatherTool(),
            weatherMcpTools.getForecastTool(),
            weatherMcpTools.getWeatherByCityTool()
        )
        .build();
    return mcpSyncServer;
  }

  @Bean
  public ServletRegistrationBean<?> mcpServlet(HttpServletSseServerTransportProvider transportProvider) {
    return new ServletRegistrationBean<>(transportProvider);
  }

  @PreDestroy
  public void closeMcpServer() {
    if (mcpSyncServer != null) {
      mcpSyncServer.close();
    }
  }
}
