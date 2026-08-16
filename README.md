# Runtime Image API (Java 17)

Stateless Spring Boot API that generates SVG avatars and placeholders at request time.
It does not use a database or an in-memory store: every response is derived from the
incoming request.

Build and run tests:

```bash
mvn test
```

Run the app:

```bash
mvn spring-boot:run
```

Swagger UI (after app starts):

```bash
# Open http://localhost:8080/api
```

## Endpoints

Generate an initials avatar:

```bash
curl "http://localhost:8080/api/v1/avatar?name=Ada%20Lovelace&size=128&shape=CIRCLE" -o avatar.svg
```

Generate a placeholder:

```bash
curl "http://localhost:8080/api/v1/placeholder?width=640&height=360&label=Hero" -o placeholder.svg
```

Supported query parameters:

- `/api/v1/avatar`: `name`, `size`, `shape`, `background`, `color`
- `/api/v1/placeholder`: `width`, `height`, `label`, `background`, `color`

Colors must be hex values such as `#2563eb`.
