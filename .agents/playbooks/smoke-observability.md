## Smoke-проверки: health, метрики, трейсы, сессии

### 1) Actuator health

После запуска приложения:

```bash
curl -sS http://localhost:8080/actuator/health
```

Должен вернуться `UP`.

### 2) Метрики для Prometheus

```bash
curl -sS http://localhost:8080/actuator/prometheus | head -n 5
```

Далее в Prometheus (`http://localhost:9090`) проверь:

- Targets: `Status -> Targets` должен показывать `UP`
- Expression: `up`

### 3) Grafana

Открой `http://localhost:3000` и убедись, что datasource Prometheus доступен.

### 4) Jaeger traces

Открой `http://localhost:16686`.

Сделай пару запросов к приложению:

```bash
curl -sS http://localhost:8080/api/v1/hello
```

В Jaeger должен появиться trace (сервис с именем приложения).

### 5) Сессионная авторизация (cookie)

Если endpoint логина уже подключён, проверь установку cookie:

```bash
curl -i -c cookies.txt -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo"}' \
  http://localhost:8080/api/v1/auth/login
```

Потом обращение к защищённому endpoint (пример):

```bash
curl -i -b cookies.txt http://localhost:8080/api/v1/auth/me
```
