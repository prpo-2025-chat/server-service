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

## Docker

```powershell
docker build -t server-service .
```

```powershell
docker-compose up --build
```

