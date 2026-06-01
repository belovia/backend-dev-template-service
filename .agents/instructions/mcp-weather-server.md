# Инструкция: MCP Weather Server

Документ для разработчиков и для AI-агентов в Cursor: как устроен, как запустить и как пользоваться MCP-сервером погоды в этом репозитории.

## Назначение

Модуль **`weather-mcp-server`** — отдельное Spring Boot-приложение, которое реализует [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) и отдаёт клиенту (например Cursor) **tools** для получения погоды через [Open-Meteo](https://open-meteo.com/) (без API key для non-commercial использования).

Основной backend (`service`, порт 8080) и MCP-сервер (порт 8090) **независимы**: MCP можно запускать отдельно.

## Архитектура

```text
Cursor (MCP client)
    │  HTTP SSE
    ▼
weather-mcp-server :8090
    ├── /sse              — SSE-подключение клиента
    ├── /mcp/message      — сообщения MCP (JSON-RPC)
    └── /actuator/health  — health check
    │
    ├── internal/adapters/mcp     — регистрация MCP tools
    ├── internal/app              — бизнес-логика (без Spring)
    ├── internal/ports            — интерфейсы WeatherClient, GeocodingClient
    └── internal/adapters/openmeteo — HTTP к Open-Meteo API
```

**Транспорт:** HTTP SSE (`HttpServletSseServerTransportProvider` из MCP Java SDK 1.0.0).

**Источник данных:**

- Прогноз и текущая погода: `https://api.open-meteo.com/v1/forecast`
- Геокодинг городов: `https://geocoding-api.open-meteo.com/v1/search`

## Требования

- Java 21
- Maven 3.9+
- Сеть (доступ к api.open-meteo.com)
- Для Cursor: настроенный `mcp.json`

## Сборка

Из корня репозитория:

```bash
mvn -pl weather-mcp-server package
```

Полная проверка монорепо:

```bash
mvn verify
```

## Запуск

```bash
mvn -pl weather-mcp-server spring-boot:run
```

По умолчанию сервер слушает **http://localhost:8090**.

Проверка health:

```bash
curl -sS http://localhost:8090/actuator/health
```

Ожидается ответ со статусом `UP`.

## Подключение в Cursor

1. Убедись, что MCP-сервер запущен (`spring-boot:run` выше).
2. Открой настройки MCP в Cursor (файл `mcp.json` — глобальный или в проекте).
3. Добавь сервер:

```json
{
  "mcpServers": {
    "weather": {
      "url": "http://localhost:8090/sse"
    }
  }
}
```

4. Перезагрузи MCP-серверы в Cursor (или перезапусти IDE).
5. В чате агент должен видеть tools с префиксом сервера `weather` (имя из конфига).

### Windows / Docker

Если Cursor не достучится до `localhost:8090`, проверь, что порт не занят и firewall не блокирует входящие на 8090.

## Доступные MCP tools

| Tool | Аргументы | Описание |
|------|-----------|----------|
| `get_current_weather` | `latitude` (number), `longitude` (number) | Текущая погода по координатам WGS84 |
| `get_forecast` | `latitude`, `longitude`, `days` (integer, опционально, 1–16, по умолчанию 3) | Дневной прогноз |
| `get_weather_by_city` | `city` (string) | Геокодинг первого совпадения + текущая погода |

### Формат ответа tool

Каждый tool возвращает **JSON-текст** (pretty-printed) с полями модели, например для текущей погоды:

- `latitude`, `longitude`, `timezone`, `time`
- `temperatureCelsius`, `relativeHumidityPercent`, `windSpeedKmh`, `weatherCode`

Коды погоды — WMO weather code Open-Meteo (см. [документацию API](https://open-meteo.com/en/docs)).

### Примеры запросов агенту

- «Какая сейчас погода в Берлине?» → `get_weather_by_city` с `city: "Berlin"`.
- «Прогноз на 5 дней для 55.75, 37.62» → `get_forecast` с `latitude`, `longitude`, `days: 5`.

## Конфигурация

Файл: `weather-mcp-server/src/main/resources/application.yml`

| Параметр | По умолчанию | Назначение |
|----------|--------------|------------|
| `server.port` | `8090` | HTTP-порт |
| `open-meteo.forecast-url` | `https://api.open-meteo.com/v1/forecast` | URL прогноза |
| `open-meteo.geocoding-url` | `https://geocoding-api.open-meteo.com/v1/search` | URL геокодинга |

Переопределение через переменные окружения Spring Boot (например `SERVER_PORT`, `OPEN_METEO_FORECAST_URL`) поддерживается стандартным relaxed binding.

## Структура кода (где что менять)

| Задача | Путь |
|--------|------|
| Добавить новый tool | `weather-mcp-server/.../internal/adapters/mcp/WeatherMcpTools.java` + регистрация в `McpServerConfig.java` |
| Логика без Spring | `weather-mcp-server/.../internal/app/WeatherService.java` |
| HTTP к Open-Meteo | `weather-mcp-server/.../internal/adapters/openmeteo/` |
| MCP transport / beans | `weather-mcp-server/.../internal/config/McpServerConfig.java` |
| Точка входа | `weather-mcp-server/.../WeatherMcpApplication.java` |

## Ограничения и заметки

- **Лимиты Open-Meteo:** для non-commercial — порядка 10 000 запросов/день; для продакшена нужна [своя инстанция](https://open-meteo.com/) или коммерческий план.
- **Атрибуция:** данные Open-Meteo — лицензия CC BY 4.0; при публичном использовании укажи источник.
- **Город:** `get_weather_by_city` берёт **первый** результат геокодинга; для неоднозначных названий лучше уточнять страну или использовать координаты.
- **Безопасность:** MCP endpoint в dev **без аутентификации**; не выставляй 8090 в интернет без защиты.

## Устранение неполадок

| Симптом | Что проверить |
|---------|----------------|
| Cursor не видит tools | Сервер запущен? URL в `mcp.json` — `http://localhost:8090/sse`? Перезагрузка MCP в Cursor |
| `health` не `UP` | Логи `mvn spring-boot:run`; порт 8090 свободен |
| Tool возвращает ошибку / пусто | Интернет; доступность api.open-meteo.com; корректность lat/lon (-90..90, -180..180) |
| «City not found» | Другое написание города или `get_current_weather` с координатами |
| Ошибки компиляции MCP SDK | В parent POM зафиксирован `mcp-bom` 1.0.0; используется `mcp-json-jackson2` (совместимость с Spring Boot) |

## Связанные документы

- Краткий плейбук: [`.agents/playbooks/mcp-weather-server.md`](../playbooks/mcp-weather-server.md)
- Обзор репозитория: [`AGENTS.md`](../../AGENTS.md) в корне
