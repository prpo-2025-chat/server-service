# server-service

Javin microservice, ki skrbi za upravljanje skupin za pogovore (servers) in članstev.
To vključuje ustvarjanje in brisanje takih skupin, ter dodajanje in odtranjevanje uporabnikov iz takih skupin.

Uporabljene tehnilogije: Java 21, Spring Boot 3, MongoDB, Maven, Lombok, Springdoc OpenAPI.

### Repository layout
- `api/` — Spring Boot REST API exposa server in membership endpointe
- `service/` — poslovna logika in implementacije repositorejev.
- `entities/` — DTO-ji in entitete
- `Dockerfile`, `docker-compose.yml`

### Build in zagon
Iz root:
```powershell
mvn clean package -DskipTests
```
Iz root/api:
```powershell
mvn spring-boot:run
```
Po defaultu, je aplikacija na voljo na `http://localhost:8031`.
Api dokumentacija (Swagger UI) je na voljo na `http://localhost:8031/swagger-ui.html`.

Konfiguracija:
- Glavna Spring Boot config datoteka je `api/src/main/resources/application.yml`.
- Pomembna lastnost: `spring.data.mongodb.uri`

## API dokumentacija
Api dokumentacija (Swagger UI) je na voljo na `http://localhost:8031/swagger-ui.html`.

- GET /hello
  - Simple health/hello endpoint. Returns a plain text: `Hello from Spring Boot!`.
  - used for quick health checks while testing the service.

### Server management (`/api/servers`)

- POST /api/servers
  - Create a server. Headers:
    - `Creator-Id` (string) — required.
    - `User-Id` (string) — optional; required when creating a DM server.
  - Body (JSON):
    ```json
    {
      "name": "My Server",
      "type": "GROUP",
      "profile": { "avatarUrl": "https://example.com/avatar.jpg", "bio": "Hello" }
    }
    ```
  - Success: 201 Created, Location header set to `/api/servers/{id}` and server JSON returned.

- GET /api/servers/{id}
  - Returns server details for the given id. 200 OK or 404 Not Found.

- DELETE /api/servers/{id}
  - Deletes a server (only GROUP servers and only if caller is OWNER).
  - Header: `Caller-Id` (string).
  - Success: 204 No Content.

### Membership management (`/api/memberships`)

- GET /api/memberships/users
  - Header: `Server-Id` — returns list of userIds that are members of the server.

- GET /api/memberships/{userId}/servers
  - Returns list of Server objects that the user is a member of.
  - Optional query param: `?type=GROUP` to filter by server type.

- POST /api/memberships
  - Add a member to a server. Headers: `User-Id`, `Server-Id`.

- DELETE /api/memberships
  - Remove a member. Headers: `Caller-User-Id`, `Target-User-Id`, `Server-Id`.

- DELETE /api/memberships/ban
  - Ban (and remove) a user. Same headers as remove.

- PATCH /api/memberships
  - Change role. Headers: `Caller-User-Id`, `Target-User-Id`, `Server-Id`, `Role` (MEMBER/ADMIN/OWNER).

- GET /api/memberships/{userId}
  - Header: `Server-Id` — returns the membership object for that user on the server.

## Docker

```powershell
docker build -t server-service .
```

```powershell
docker-compose up --build
```

