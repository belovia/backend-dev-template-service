# Agent playbooks и инструкции

## Instructions

- [mcp-weather-server.md](instructions/mcp-weather-server.md) — полная инструкция по MCP Weather Server (запуск, Cursor, tools, troubleshooting)

## Skills

- [ARCHITECTURE-SKILL.md](skills/ARCHITECTURE-SKILL.md) — clean architecture, use cases (`execute`, validate → business → mapResponse), ports, adapters, validators
- [TEST_SKILL.md](skills/TEST_SKILL.md) — naming, structure (Arrange / Action / Asserts), Spring/Mockito stack, positive and negative test cases

## Playbooks

- [local-compose.md](playbooks/local-compose.md) — поднять Postgres + observability, запустить приложение на хосте
- [smoke-observability.md](playbooks/smoke-observability.md) — health, Prometheus, Jaeger, session cookie
- [mcp-weather-server.md](playbooks/mcp-weather-server.md) — MCP weather server (порт 8090, Cursor `mcp.json`)
