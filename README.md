# Task Management Application

A Spring Boot application for managing tasks and notifications.

## Features

- User management (registration, login)
- Task management (create, update, delete, complete)
- Notification system
- PostgreSQL database with Flyway migrations
- Unit tests with Mockito

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Gradle 7.0 or higher

## Database Setup

### PostgreSQL

1. Install PostgreSQL if not already installed:
   ```bash
   brew install postgresql
   ```

2. Start PostgreSQL:
   ```bash
   brew services start postgresql
   ```

3. Create the database:
   ```bash
   createdb taskdb
   ```

4. Set environment variables:
   ```bash
   export POSTGRES_URL=jdbc:postgresql://localhost:5432/taskdb
   export POSTGRES_USERNAME=postgres
   export POSTGRES_PASSWORD=your_password
   ```

## Running the Application

### Development Mode (H2 Database)
```bash
./gradlew bootRun
```

### Production Mode (PostgreSQL)
```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

## Database Migrations

The application uses Flyway for database migrations. Migration files are located in `src/main/resources/db/migration/`:

- `V1__init_schema.sql`: Creates initial database schema
- `V2__add_indexes.sql`: Adds performance indexes

## Testing

Run tests with:
```bash
./gradlew test
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── spbstu/
│   │       └── TasksApplication/
│   │           ├── controller/
│   │           ├── model/
│   │           ├── repository/
│   │           ├── service/
│   │           └── TasksApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-h2.properties
│       ├── application-postgres.properties
│       └── db/
│           └── migration/
└── test/
    └── java/
        └── spbstu/
            └── TasksApplication/
                ├── controller/
                ├── service/
                └── repository/
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
