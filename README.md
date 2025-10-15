## NykaaSandbox — Auth Service

### Overview
NykaaSandbox is a Spring Boot (Java 17) authentication/utility service. It integrates with MySQL (optionally Google Cloud SQL via Socket Factory), Redis, and exposes REST APIs. This README helps you run it locally and share context safely with AI assistants.

### Tech stack
- **Java**: 17
- **Spring Boot**: 3.2.x (Web, Data JPA, Validation)
- **Database**: MySQL (local or Cloud SQL)
- **Redis**: Optional (rate limiting / caching)
- **Build**: Maven

### Prerequisites
- Java 17 (JDK)
- Maven 3.9+
- MySQL 8.x (or access to Cloud SQL)
- Redis (optional if features require it)

### Quick start
```bash
mvn clean install
mvn spring-boot:run
```

### Configuration
Create `src/main/resources/application.yml` (or `.properties`) using placeholders — do NOT commit real secrets.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/YOUR_DB_NAME
    username: YOUR_DB_USER
    password: YOUR_DB_PASSWORD
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

# Optional Redis
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
```

#### Cloud SQL (optional)
If connecting to Google Cloud SQL for MySQL, use the Socket Factory and supply credentials via ADC (gcloud auth/application-default login or service account):

```properties
spring.datasource.url=jdbc:mysql:///YOUR_DB_NAME?cloudSqlInstance=YOUR_PROJECT:YOUR_REGION:YOUR_INSTANCE&socketFactory=com.google.cloud.sql.mysql.SocketFactory
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
```

Dependencies already include:
- `com.google.cloud.sql:mysql-socket-factory-connector-j-8:1.14.1`

### Build and run
```bash
# Run tests and build
mvn clean install

# Run the app
mvn spring-boot:run
```

### Project coordinates
- **GroupId**: `com.eshop`
- **ArtifactId**: `auth`
- **Version**: `0.0.1-SNAPSHOT`

### Project structure (key paths)
```
NykaaSandbox/
  pom.xml
  src/main/java/com/eshop/auth/...       # source
  src/main/resources/                    # configuration
```

### Common issues
- **Failed loading class 'com.google.cloud.sql.mysql.SocketFactory'**: Ensure the Cloud SQL socket factory dependency is present (it is) and that the JDBC URL uses `socketFactory` correctly. Also ensure Google credentials are available to the runtime.
- **Maven property not defined (e.g., ${itext.version})**: A shared iText version property is defined in `pom.xml`. Keep related artifacts aligned to `${itext.version}`.
- **Build fails due to outdated libs**: This project pins iText and Protobuf via properties. Update versions in `<properties>` if needed and rebuild.

### Safe sharing with AI assistants
- Remove/replace secrets in configs before sharing.
- Provide only relevant files (e.g., `pom.xml`, sanitized `application.yml`, and the classes in question).
- Include exact error logs and the commands you ran.

### License
Internal sandbox project. Add a license if distributing.


