# Customer Profile API

A Spring Boot REST API for managing customer profiles (name, email, photo), backed by **MySQL** with **Redis** caching.

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Cache)
- MySQL 8.4
- Redis 7
- MapStruct + Lombok
- Docker Compose (for MySQL and Redis)
- JUnit 5, Mockito, AssertJ and MockMvc (unit tests)

## Prerequisites

- [JDK 25](https://adoptium.net/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Docker Compose)

Maven does not need to be installed: the project ships with the Maven Wrapper (`mvnw` / `mvnw.cmd`).

## 1. Configure environment variables
Open terminal.

Go to the project directory.

Database credentials are read from a `.env` file in the project root. It is git-ignored, so create it from the template:

```bash
cp .env.example .env
```

Then edit `.env` and set your values:

```properties
MYSQL_DATABASE=customerdb
MYSQL_USER=customer
MYSQL_PASSWORD=change-me
MYSQL_ROOT_PASSWORD=change-me
```

The same file is used by both Docker Compose (to create the database) and the Spring Boot app (to connect to it).

## 2. Start MySQL and Redis

```bash
docker compose up -d
```

Check that both containers are running:

```bash
docker compose ps
```

| Service | Container                | Port |
|---------|--------------------------|------|
| MySQL   | `customer-profile-mysql` | 3306 |
| Redis   | `customer-profile-redis` | 6379 |

## 3. Run the application

Run from the **project root** (the app looks for `.env` in the current working directory):

**Linux / macOS / Git Bash**
```bash
./mvnw spring-boot:run
```

**Windows (PowerShell / CMD)**
```powershell
.\mvnw.cmd spring-boot:run
```

The API starts on **http://localhost:8080**.

On the first start (when the `customers` table is empty) the app seeds **10 random customers** via `DataSeeder`. Later restarts skip seeding because data already exists.

> Running from an IDE: make sure the run configuration's working directory is the project root, otherwise startup fails with `Could not resolve placeholder 'MYSQL_USER'`.

## API endpoints

Base path: `/api/customers`

| Method   | Path                  | Description                        | Success          |
|----------|-----------------------|------------------------------------|------------------|
| `GET`    | `/api/customers`      | List all customer profiles         | `200 OK`         |
| `GET`    | `/api/customers/{id}` | Get one profile (cached)           | `200 OK`         |
| `POST`   | `/api/customers`      | Create a profile                   | `200 OK`         |
| `PATCH`  | `/api/customers/{id}` | Partially update a profile         | `200 OK`         |
| `DELETE` | `/api/customers/{id}` | Delete a profile                   | `204 No Content` |

`GET`, `PATCH` and `DELETE` on an id that doesn't exist return `404 Not Found`:

```json
{ "error": "Customer with id 99 not found" }
```

### Examples

Create:
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Ann Smith","email":"ann@example.com","photo":"ann.png"}'
```

Get:
```bash
curl http://localhost:8080/api/customers/1
```

Update (send only the fields you want to change):
```bash
curl -X PATCH http://localhost:8080/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Ann Jones"}'
```

Delete:
```bash
curl -X DELETE http://localhost:8080/api/customers/1
```

### Validation

- **Create (`POST`)**: `name`, `email` and `photo` are all required and must not be blank.
- **Update (`PATCH`)**: every field is optional (omitted or `null` = leave unchanged), but any field you send must not be blank.
- `email` must be a valid email address.

Invalid requests return `400 Bad Request` with one message per invalid field:

```json
{
  "name": "Name is required",
  "email": "Email must be valid",
  "photo": "Photo is required"
}
```

## Caching

`GET /api/customers/{id}` results are cached in Redis under the `customerProfiles` cache. Updating or deleting a customer evicts its cache entry.

To clear the cache manually:

```bash
docker exec -it customer-profile-redis redis-cli FLUSHALL
```

## Stopping

Stop the app with `Ctrl+C`, then stop the containers:

```bash
docker compose down
```

> ⚠️ `docker compose down -v` also **deletes the volumes**, which wipes all database and cache data.

## Running tests

The project has **unit tests only**. They don't start Spring, MySQL or Redis, so Docker doesn't need to be running.

**Linux / macOS / Git Bash**
```bash
./mvnw test
```

**Windows (PowerShell / CMD)**
```powershell
.\mvnw.cmd test
```

| Test class | What it covers |
|------------|----------------|
| `CustomerControllerTest` | Every endpoint via standalone MockMvc with a mocked service: status codes, JSON responses, validation errors (400) and not-found errors (404) |
| `CustomerServiceTest` | Service logic with mocked repository and mapper, including not-found cases |
| `CustomerMapperTest` | MapStruct mapping between entity and DTOs, including partial updates |
| `DataSeederTest` | Seeding when the table is empty and skipping when it already has data |

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `Could not resolve placeholder 'MYSQL_USER'` | `.env` is missing or the app isn't running from the project root. |
| `Communications link failure` / connection refused on 3306 | MySQL isn't up yet. Run `docker compose ps` and wait until it's healthy. |
| `Port 3306 is already allocated` | A local MySQL is already running. Stop it or change the port mapping in `docker-compose.yml`. |
