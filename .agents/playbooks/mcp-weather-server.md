## MCP Weather Server (Open-Meteo)

Подробная инструкция: [../instructions/mcp-weather-server.md](../instructions/mcp-weather-server.md)

### Запуск

Из корня репозитория:

```bash
mvn -pl weather-mcp-server spring-boot:run
```

Сервер слушает **http://localhost:8090**.

### Подключение в Cursor

Добавь в настройки MCP (`mcp.json`):

```json
{
  "mcpServers": {
    "weather": {
      "url": "http://localhost:8090/sse"
    }
  }
}
```

### Доступные tools

| Tool | Описание |
|------|----------|
| `get_current_weather` | `latitude`, `longitude` — текущая погода |
| `get_forecast` | `latitude`, `longitude`, опционально `days` (1–16) |
| `get_weather_by_city` | `city` — геокодинг + текущая погода |

### Smoke

```bash
curl -sS http://localhost:8090/actuator/health
```

После подключения MCP в Cursor вызови tool, например `get_weather_by_city` с `city: "Berlin"`.
