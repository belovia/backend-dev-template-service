## Локальный запуск: Postgres + Observability (docker-compose)

### Предварительные требования

- Docker Desktop
- Java 21
- Maven 3.9+

### Поднять инфраструктуру

Из корня репозитория:

```bash
docker compose up -d
```

Проверить, что контейнеры поднялись:

```bash
docker compose ps
```

### Порты (по умолчанию)

- Postgres: `localhost:5432`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- Jaeger UI: `http://localhost:16686`

### Запуск приложения на хосте

Сначала экспортируй переменные окружения (значения соответствуют `docker-compose.yml`):

PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/app"
$env:SPRING_DATASOURCE_USERNAME="app"
$env:SPRING_DATASOURCE_PASSWORD="app"
$env:OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4318/v1/traces"
```

Bash:

```bash
export SPRING_PROFILES_ACTIVE=local
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/app
export SPRING_DATASOURCE_USERNAME=app
export SPRING_DATASOURCE_PASSWORD=app
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318/v1/traces
```

Значения по умолчанию также в `.env.dist`.

Запуск:

```bash
mvn spring-boot:run
```

### Остановить инфраструктуру

```bash
docker compose down
```
