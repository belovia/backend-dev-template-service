## Что это за репозиторий

Шаблон Java backend-сервиса (монолит) на **Java 21 / Spring Boot 3 / Maven** с:

- PostgreSQL 17.7 в `docker-compose`
- каркасом регистрации/авторизации (session cookie + Spring Session JDBC)
- базовой наблюдаемостью: Prometheus + Grafana + Jaeger
- модулем **weather-mcp-server** — MCP-сервер (HTTP SSE) с tools для Open-Meteo

Монорепо Maven: модули `service` (основной API) и `weather-mcp-server`.

Архитектура и шаблоны разработки:

- `c:/Users/Selecty/IdeaProjects/backend-dev-template-service/.agents/skills/ARCHITECTURE-SKILL.md`

Тесты:

- `c:/Users/Selecty/IdeaProjects/backend-dev-template-service/.agents/skills/TEST_SKILL.md`

## Как поднять локально

Смотри плейбук:

- `c:/Users/Selecty/IdeaProjects/backend-dev-template-service/.agents/playbooks/local-compose.md`

## Наблюдаемость и smoke-проверки

Смотри плейбук:

- `c:/Users/Selecty/IdeaProjects/backend-dev-template-service/.agents/playbooks/smoke-observability.md`

## MCP Weather (Open-Meteo)

Инструкция (подробно):

- `c:/Users/Selecty/IdeaProjects/backend-dev-template-service/.agents/instructions/mcp-weather-server.md`

Краткий плейбук:

- `c:/Users/Selecty/IdeaProjects/backend-dev-template-service/.agents/playbooks/mcp-weather-server.md`
