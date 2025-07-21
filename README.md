# Synthetic‑Human Core & Bishop Emulator

> Лёгкая интеграция общего стартер‑модуля для Android‑подобных ИИ и демонстрационного сервиса «Bishop Emulator»

Этот моно‐репозиторий содержит две подсистемы:

1. **`synthetic‑human‑core`** — автоконфигурационный стартер для:
  - Приёма и валидации команд
  - Асинхронного выполнения
  - Аудита действий (консоль / Kafka)
  - Публикации метрик (Micrometer + Actuator)
  - Централизованной обработки ошибок

2. **`bishop‑emulator`** — Spring Boot‑служба‑эмулятор, демонстрирующая возможности стартера.

---

## 🚀 Быстрый старт

```bash
git clone https://github.com/your‑org/synthetic‑human.git
cd synthetic‑human
docker-compose up --build
```

# Примеры запросов
Отправить команду

```bash
curl -X POST http://localhost:8080/tasks \
     -H "Content-Type: application/json" \
     -d '{"description":"Calibrate sensors","commandType":"COMMON","author":"bishop"}'
````

Сменить режим аудита

```bash
curl -X POST "http://localhost:8080/audit/switch?mode=console"
```

Проверить размер очереди

```bash
curl http://localhost:8080/actuator/metrics/synthetic.command.queue.size
```
