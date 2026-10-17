# Weathering

A weather dashboard with a pixel-art interface, built with Spring Boot, Java, MySQL, and OpenWeather.

- Search for cities and view current weather.
- View a five-day forecast.
- Create an account and save locations.
- Explore weather-based activity suggestions.

## Requirements

- Java 17 or later.
- MySQL or MariaDB.
- An OpenWeather API key with access to current weather, geocoding, and the five-day forecast API.

## Run locally

1. Create a local database named `weathering_db` and a database user with access to it. Hibernate creates the tables on startup; `database/schema.sql` is also available for manual setup and contains no user data.
2. Set the environment variables below in PowerShell, using your own credentials. `.env.example` lists the settings; Spring Boot does not automatically load `.env` files.

   ```powershell
   $env:OPENWEATHER_API_KEY = 'your-openweather-api-key'
   $env:DB_URL = 'jdbc:mysql://localhost:3306/weathering_db'
   $env:DB_USERNAME = 'your-database-username'
   $env:DB_PASSWORD = 'your-database-password'
   ```

3. Start the application from the Maven project directory:

   ```powershell
   cd weathering/weathering
   .\mvnw.cmd spring-boot:run
   ```

4. Open <http://localhost:8080>.

On macOS or Linux, export the same environment variables and use `sh ./mvnw spring-boot:run`.

## Build and test

From `weathering/weathering`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

The existing application-context test requires the configured database and environment variables. Packaged files are written to `target/` and excluded from Git.

The data-safety regression tests run without a database or API credentials:

```powershell
.\mvnw.cmd -Dtest=PublicDataSafetyTests test
```

## Project layout

- `weathering/weathering/`: Maven project, wrapper, and application configuration.
- `weathering/weathering/src/main/java/`: REST controllers, services, entities, and repositories.
- `weathering/weathering/src/main/resources/static/`: HTML, CSS, JavaScript, and artwork.
- `database/schema.sql`: clean database schema without account or location records.

## Data and security

Credentials belong in environment variables. Local environment files, database exports, IDE settings, and build output are excluded from Git. Never commit a populated database dump.

User responses omit password hashes, and weather-service error messages omit upstream request URLs and API keys.

This is a local portfolio prototype. The current login flow does not establish authenticated server sessions, account and saved-location routes need authorization, and password storage uses SHA-256. Add authenticated sessions, authorization checks, and an adaptive password hash before hosting the application for real users. Publishing the source on GitHub does not deploy the application.
