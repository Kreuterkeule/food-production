# food-production

# Requirements

npm
node
openjdk 17

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