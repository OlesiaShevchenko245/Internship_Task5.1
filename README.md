# Email Service  
_Java Spring Boot мікросервіс для відправки email-повідомлень у проєкті Cosmorum._

## Опис проєкту  
Email Service відповідає за асинхронну обробку та відправку email-повідомлень, які надходять від інших мікросервісів системи через Message Broker.  

Сервіс:  
- зберігає повідомлення у власній БД (ElasticSearch)  
- намагається відправити їх через SMTP  
- відстежує статуси відправки  
- автоматично повторює спроби у разі тимчасових помилок  
___

## Технологічний стек
- Java 17  
- Spring Boot  
- Spring Data Elasticsearch  
- Spring Mail (JavaMailSender)  
- RabbitMQ  
- ElasticSearch  
- Docker  
- MailHog 
___

## Архітектура  
Сервіс працює як асинхронний consumer повідомлень з Message Broker:  
1. Інші мікросервіси публікують подію про створення сутності в RabbitMQ.  
2. Email Service зчитує повідомлення з черги, зберігає email у ElasticSearch зі статусом PENDING та намагається відправити email через SMTP.  
   У разі успіху статус змінюється на SENT, у разі помилки — FAILED зі збереженням errorMessage.  
3. Фоновий scheduler раз на 5 хвилин повторює спроби для повідомлень зі статусом FAILED.  
___
## Запуск
### Клонування репозиторію (завантаження https://github.com/OlesiaShevchenko245/Internship_Task5.1)  
```
git clone <repository-url>
cd Internship_Task5.1
```
### Налаштування змінних середовища (створення файлу .env на основі .env.example)
Для запуску з MailHog:  
```  
RABBIT_HOST=rabbitmq
RABBIT_PORT=5672
RABBIT_USER=guest
RABBIT_PASS=guest

EMAIL_EXCHANGE=email.exchange
EMAIL_ROUTING_KEY=email.send
EMAIL_QUEUE=email.queue

ELASTIC_URIS=http://elasticsearch:9200

SMTP_HOST=mailhog
SMTP_PORT=1025
SMTP_USERNAME=
SMTP_PASSWORD=
SMTP_AUTH=false
SMTP_STARTTLS_ENABLE=false
SMTP_STARTTLS_REQUIRED=false

SMTP_FROM=no-reply@test.com
ADMIN_EMAIL=admin@example.com

POSTGRES_DB=cosmorum_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=pass
```
### Запуск через Docker Compose  
```
docker compose up -d --build
```
### Перевірка роботи
Перевірка стану Email Service:  
```
curl http://localhost:8083/actuator/health
```
_Очікуваний результат - JSON {"status":"UP"}._  

Створення спостереження для відправки email:  
```
curl -X POST http://localhost:8081/api/observation \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test from reviewer",
    "description": "Check email integration",
    "observationTime": "2024-12-09T21:30:00",
    "authorId": 1,
    "celestialObjects": ["Jupiter"]
  }'
```
_Очікуваний результат - JSON новоствореної події, нове повідомлення в Web UI на http://localhost:8025._  

Перевірка email у ElasticSearch:
```
docker exec -it internship_task51-elasticsearch-1 sh -lc \
'curl -s "http://localhost:9200/email_messages/_search?pretty" \
-H "Content-Type: application/json" \
-d "{\"query\":{\"match_all\":{}},\"sort\":[{\"createdAt\":{\"order\":\"desc\"}}],\"size\":5}"'
```
_Очікуваний результат - email зі статусом SENT._  
___

### Структура проєкту:
```
Internship_Task5.1/
├── demo/                         # Email Service
│   ├── src/main/java
│   │   ├── mb/                   # RabbitMQ listener
│   │   ├── scheduler/            # Retry scheduler
│   │   ├── service/              # Email logic
│   │   ├── repository/           # Elasticsearch repositories
│   │   └── model/                # EmailMessageDoc, EmailStatus
│   ├── src/test/java
│   │   ├── EmailSendListenerIT
│   │   └── EmailRetrySchedulerIT
│   ├── Dockerfile
│   └── pom.xml
├── Internship_Task2/              # Observations Service
├── docker-compose.yml
├── .env.example
└── README.md
```
___

## Retry Scheduler  

Фонова процедура реалізована з використанням:
- @EnableScheduling  
- @Scheduled(fixedDelay = 300000)
  
### Функціональність:  
- раз на 5 хвилин зчитує повідомлення зі статусом FAILED  
- повторно викликає відправку  
- оновлює:  
  - status  
  - attempt  
  - lastAttemptAt  
  - errorMessage (у разі помилки)  
___
## Інтеграційні тести  
Реалізовані такі інтеграційні тести:  
1. EmailSendListenerIT:
- успішна відправка email  
- обробка помилки SMTP
  
2. EmailRetrySchedulerIT:  
- успішний retry
- повторна помилка з оновленням attempt та errorMessage

Запуск:
```
cd demo
./mvnw test
# або окремо
./mvnw -Dtest=EmailSendListenerIT test
./mvnw -Dtest=EmailRetrySchedulerIT test
```
_Очікуваний результат - BUILD SUCCESS._  
___

## Використання MailHog

Для реалізації безпосередньо відправлення повідомлень я вирішила використати MailHog, оскільки цей локальний сервіс дозволяє перевіряти email без реального SMTP, тим самим найкраще підходить для даного завдання, де необхідно перевірити реакцію програми на проблеми підключення. 
Це також дозволяє вам (викладачу) без проблем побачити отримані повідомлення у браузері без будь-якої аутентифікації. Звичайно, для продакшну такий вибір не підходить, але мені здалося, що він має сенс для проєкту на цьому етапі розвитку.
  
___

## Автор

Проєкт виконала Олеся Шевченко в рамках **Full-Stack Internship** :)
