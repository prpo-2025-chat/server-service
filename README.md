# server-service
### Docker
open Docker Desktop

in server-service run:\
`docker build -t server-service .`

`docker compose up --build`

mongo uri shrani kot sistemsko spremeljivko MONGODB_DATABASE

Zastarelo:\
and then:\
`docker run -p 8080:8080 user-service`

TODO: 
- Caller user should be provided as part of the auth on apis?
- api auth in general