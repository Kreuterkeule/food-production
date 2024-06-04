# food-production

# Requirements

npm
node
openjdk 17

postgresql database connection with emtpy database

### Required Configuration

When Deploying on server change api url in ./src/frontend/food/src/services/backendService.js

```js
// backendService.js
export default class constantService {
    static baseUrl = 'http://localhost:6969';
}
```

./src/main/resources/application.properties Database configuration

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/food_production
spring.datasource.username=postgres
spring.datasource.password=123
```
Change Driver, if you use a different Database (f.e. MariaDB)
```properties
# application.properties
spring.datasource.driver-class-name=org.postgresql.Driver
```
you will need to include the driver dependency in pom.xml

```xml
<!-- pom.xml -->
<project>
    <!-- ... -->
    <dependencies>
        <!-- ... -->
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
        </dependency>
    </dependencies>
    <!-- ... -->
</project>
```

# Installation

## Linux and Mac

API
```bash
./mvnw spring-boot:run
```

Frontend
```bash
cd ./src/frontend/food && npm install
```

```bash
npm run serve
```

## Windows

### Method one (possible - not tested)

API
```shell
.\mvnw.cmd spring-boot:run
```

Frontend
```shell
dir .\src\frontend\food && npm install
```

```shell
npm run serve
```

### Method two (recommended)

just get a fucking unix system

# Docs

## Api

accessible on <host>:6969/swagger-ui.html after startup

## Frontend

just use it, it's quite intuitive.
for questions ask me or open an issue.

# Troubleshooting

for initialization with postgres remove the _ingorecase from springs suggested ddl
