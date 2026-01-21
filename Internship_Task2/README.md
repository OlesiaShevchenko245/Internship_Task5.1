# Observations Service  
_Spring Boot REST API сервіс для управління астрономічними спостереженнями та авторами._

## Опис проєкту
Сервіс надає повний CRUD функціонал, пагінацію, фільтрацію, генерацію Excel звітів та імпорт даних з JSON.

___

## Технологічний стек
- Java 17
- Spring Boot 3.2.0
- PostgreSQL
- Liquibase
- Apache POI
- Maven
- Testcontainers
- JUnit 5

___

## Запуск
### Клонування репозиторію (після завантаження даного рипозиторію (https://github.com/OlesiaShevchenko245/Internship_Task2))
```
git clone <repository-url>
cd Internship_Task2
```
### Налаштування PostgreSQL (локально)
```
psql -U postgres 
CREATE DATABASE cosmorum_db;
# за необхідності:
CREATE USER postgres WITH PASSWORD 'pass';
GRANT ALL PRIVILEGES ON DATABASE cosmorum_db TO postgres;
```
### Запуск
```
# компіляція
./mvnw compile

# з використанням Maven Wrapper 
./mvnw spring-boot:run

# якщо Maven встановлений глобально
mvn spring-boot:run
```
Додаток має запуститися на http://localhost:8080  

### Перевірка роботи
```
# отримати список авторів
curl http://localhost:8080/api/author
```
_Очікуваний результат - JSON з 5 авторами: Galileo Galilei, Edwin Hubble, Johannes Kepler, Caroline Herschel, Tycho Brahe._  

___

### Структура проєкту:
```
cosmorum-service/
├── src/
│   ├── main/
│   │   ├── java/com/cosmorum/
│   │   │   ├── controller/          # REST контролери
│   │   │   │   ├── AuthorController.java
│   │   │   │   └── ObservationController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── AuthorDTO.java
│   │   │   │   ├── ObservationDTO.java
│   │   │   │   ├── ObservationListDTO.java
│   │   │   │   ├── ObservationFilterRequest.java
│   │   │   │   ├── ObservationListResponse.java
│   │   │   │   └── UploadResponse.java
│   │   │   ├── entity/              # JPA сутності
│   │   │   │   ├── Author.java
│   │   │   │   └── AstronomicalObservation.java
│   │   │   ├── exception/           # Обробка помилок
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── DuplicateResourceException.java
│   │   │   ├── repository/          # JPA репозиторії
│   │   │   │   ├── AuthorRepository.java
│   │   │   │   └── AstronomicalObservationRepository.java
│   │   │   ├── service/             # Бізнес логіка
│   │   │   │   ├── AuthorService.java
│   │   │   │   └── ObservationService.java
│   │   │   └── CosmorumServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/changelog/
│   │           └── db.changelog-master.xml
│   └── test/
│       └── java/com/cosmorum/
│           ├── controller/
│           │   ├── AuthorControllerIntegrationTest.java
│           │   └── ObservationControllerIntegrationTest.java
│           └── CosmorumServiceApplicationTests.java
├── astronomical_observations_import.json  # Приклад даних для імпорту
├── pom.xml
└── README.md
```
___

### API Endpoints

1. Створити автора:  
```
POST /api/author
Content-Type: application/json

{
  "firstName": "Carl",
  "lastName": "Sagan",
  "nationality": "American"
}
```
2. Отримати всіх авторів:
```
GET /api/author
```
3. Оновити автора:
```
PUT /api/author/{id}
Content-Type: application/json

{
  "firstName": "Carl",
  "lastName": "Sagan",
  "nationality": "American-Updated"
}
```
4. Видалити автора:
```
DELETE /api/author/{id}
```
5. Створити спостереження:
```
POST /api/observation
Content-Type: application/json

{
  "name": "Jupiter and Moons",
  "description": "Observation of Jupiter with four Galilean moons visible",
  "observationTime": "2024-12-09T21:30:00",
  "authorId": 1,
  "celestialObjects": ["Jupiter", "Io", "Europa", "Ganymede", "Callisto"]
}
```
6. Отримати спостереження по ID:
```
GET /api/observation/{id}
```
7. Оновити спостереження:
```
PUT /api/observation/{id}
Content-Type: application/json

{
  "name": "Jupiter and Moons - Updated",
  "description": "Updated description",
  "observationTime": "2024-12-09T22:00:00",
  "authorId": 1,
  "celestialObjects": ["Jupiter", "Io", "Europa"]
}
```
8. Видалити спостереження:
```
DELETE /api/observation/{id}
```
9. Список з пагінацією та фільтрацією:
```
POST /api/observation/_list
Content-Type: application/json

{
  "authorId": 1,
  "name": "Jupiter",
  "startTime": "2024-01-01T00:00:00",
  "page": 1,
  "size": 10
}
```
Параметри фільтрації (всі опціональні):  
- authorId - фільтр по автору  
- name - пошук по назві (часткове співпадіння)  
- startTime - спостереження після цієї дати  
- page - номер сторінки (за замовчуванням 1)  
- size - розмір сторінки (за замовчуванням 20)  

10. Генерація Excel звіту:
```
POST /api/observation/_report
Content-Type: application/json

{
  "authorId": 1,
  "name": "Jupiter",
  "startTime": "2024-01-01T00:00:00"
}
```

Приклад curl:  
```
curl -X POST http://localhost:8080/api/observation/_report \
  -H "Content-Type: application/json" \
  -d '{"authorId": 1}' \
  --output report.xlsx
```
11. Імпорт з JSON:
```
POST /api/observation/upload
Content-Type: application/json

[
  {
    "name": "Mars Observation",
    "description": "Surface features visible",
    "observationTime": "2024-12-09T22:00:00",
    "authorId": 2,
    "celestialObjects": ["Mars"]
  },
  {
    "name": "Invalid Observation",
    "observationTime": "2024-12-09T23:00:00",
    "authorId": 999,
    "celestialObjects": ["Unknown"]
  }
]
```
___

# База даних

_База даних складається з таких таблиць:_ 

Таблиця authors:  
```
| Колонка       | Тип          | Обмеження        | Опис                     |
| ------------- | ------------ | ---------------- | ------------------------ |
| `id`          | BIGSERIAL    | PRIMARY KEY      | Унікальний ідентифікатор |
| `first_name`  | VARCHAR(100) | NOT NULL         | Ім'я автора              |
| `last_name`   | VARCHAR(100) | NOT NULL         | Прізвище автора          |
| `nationality` | VARCHAR(100) | NOT NULL, UNIQUE | Національність           |
```
Таблиця astronomical_observations:  
```
| Колонка            | Тип          | Обмеження    | Опис                            |
| ------------------ | ------------ | ------------ | ------------------------------- |
| `id`               | BIGSERIAL    | PRIMARY KEY  | Унікальний ідентифікатор        |
| `name`             | VARCHAR(255) | NOT NULL     | Назва спостереження             |
| `description`      | TEXT         | —            | Опис спостереження              |
| `observation_time` | TIMESTAMP    | NOT NULL     | Час спостереження               |
| `author_id`        | BIGINT       | NOT NULL, FK | Зовнішній ключ на `authors(id)` |
```
Таблиця celestial_objects:  
```
| Колонка          | Тип          | Обмеження    | Опис                                              |
| ---------------- | ------------ | ------------ | ------------------------------------------------- |
| `observation_id` | BIGINT       | NOT NULL, FK | Зовнішній ключ на `astronomical_observations(id)` |
| `object_name`    | VARCHAR(255) | NOT NULL     | Назва небесного об'єкта                           |
```
### Попереднє наповнення: 

При запуску додатку Liquibase автоматично створює 5 авторів:  
- Galileo Galilei (Italian)  
- Edwin Hubble (American)  
- Johannes Kepler (German)  
- Caroline Herschel (British)  
- Tycho Brahe (Danish)

___

## Міграції

Liquibase міграції знаходяться в src/main/resources/db/changelog/db.changelog-master.xml  

Changesets:  
- Створення таблиці authors з індексами  
- Створення таблиці astronomical_observations з індексами  
- Створення таблиці celestial_objects з індексами  
- Наповнення таблиці authors початковими даними

___

## Тестування

### Запуск тестів:  
```
./mvnw test
```

### Запуск інтеграційних тестів:  
```
./mvnw test -Dtest=*IntegrationTest
```

___

## Продуктивність 

- всі запити до БД використовують індекси  
- фільтрація виконується на рівні БД (JPQL queries)  
- пагінація зменшує навантаження на пам'ять  
- lazy loading для зв'язків між сутностями

___

## Автор

Проєкт виконала Олеся Шевченко в рамках **Full-Stack Internship** :)
