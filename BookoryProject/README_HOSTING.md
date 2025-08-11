# Hosting BookoryProject (Spring Boot)

## Build & run with Docker locally

Prereqs: Docker Desktop installed.

```powershell
# In project root
docker build -t bookory:latest .

# Example using your local SQL Server on Windows
# Use host.docker.internal to reach host network from container
$env:DB_URL = "jdbc:sqlserver://host.docker.internal:1433;databaseName=Bookory;encrypt=true;trustServerCertificate=true;"
$env:DB_USERNAME = "sa"
$env:DB_PASSWORD = "yourStrong(!)Password"  # change

docker run --rm -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e DB_URL=$env:DB_URL `
  -e DB_USERNAME=$env:DB_USERNAME `
  -e DB_PASSWORD=$env:DB_PASSWORD `
  bookory:latest
```

Open http://localhost:8080

Notes:
- The container uses profile `prod` and reads DB config from env vars.
- Default `application.properties` keeps `spring.profiles.active=local` for development. Env var `SPRING_PROFILES_ACTIVE=prod` overrides it in Docker/Cloud.

## Deploy on Render (Docker)
- Create Web Service, choose "Use Docker".
- Set env vars: `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
- Render sets `$PORT`; our config uses `server.port=${PORT:8080}`.

## Deploy on Railway/Koyeb/Fly.io
- Use Docker, set the same env vars.
- Ensure your SQL Server is reachable (consider a managed cloud DB).

### Example JDBC URL (Azure SQL / SQL Server)
```
jdbc:sqlserver://<host>:1433;databaseName=<DB>;encrypt=true;trustServerCertificate=true;
```

## Troubleshooting
- Port in use: change mapping `-p 8081:8080`.
- DB errors: verify firewall/credentials; prefer managed DB for public deploys.
- H2 demo: set `DB_URL=jdbc:h2:mem:bookory;DB_CLOSE_DELAY=-1;MODE=MSSQLServer`.
