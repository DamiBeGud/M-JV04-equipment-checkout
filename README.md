# equipment-checkout

Spring Boot4.0.3/Maven, Java17–25. `mvn -B test`, `mvn -B package` (includes tests),
`java -jar target/equipment-checkout-0.1.0.jar`. PORT defaults to8304, listener0.0.0.0.
Bearer API_TOKEN required for POST /items {id,label}, GET /items/:id, POST /items/:id/checkout {holder}, POST /items/:id/return {holder}. Native DB transactions/row locks enforce exclusivity.
Requires DATABASE_URL as a postgres/postgresql URI with approved user/password, database and optional sslmode. Run `java -jar target/equipment-checkout-0.1.0.jar migrate` for source Flyway migrations. Native migrate exits; the web path never auto-migrates. No CREATE DATABASE/clean/reset.
Liveness /health/live. Readiness queries the source-owned tables (/ready for equipment, /health/ready otherwise).
Native tests exercise domain rules and MockMvc routing using a fake repository, not real PostgreSQL.
AI-assisted researcher-created source, common scaffold disclosed separately. No deployment files or runtime credential values.
