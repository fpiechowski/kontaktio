# kontaktio

An example HTTP server built for [kontakt.io](https://kontakt.io) interview task created with production readiness in
mind.

## Usage

### Build & Test

Requires Docker for running integration test environment.

```shell
./gradlew build
```

### JAR

```shell
./gradlew shadowJar
```

```shell
java -jar build/libs/kontaktio-all.jar
```

### Docker

```shell
docker build -t fpiechowski/kontaktio .
```

```shell
docker run -p 8080:8080 -e environment=local fpiechowski/kontaktio
```

## Production Usage
Update config files for any target environment. Provide API Key for Kontakt.io Developer API as well as API URL. 

```yaml
# production.yaml

kontaktApi:
  baseUrl: https://apps.cloud.us.kontakt.io
  apiKey: real
```

You can also provide then using environment variables for sensitive data:
`config.override.kontaktApi.baseUrl`,
`config.override.kontaktApi.apiKey`, etc.

Use `X-Request-Id` header for tracing.

## Overview

I used Kotlin native libraries such as Ktor and KotlinX Serialization in combination with functional approach supported
by Arrow.kt library.

The sources are organized with Hexagonal architecture and design in mind.
Technical adapters with `fun main()` entrypoint are in `main` source set and all domain code resides in `domain` source
set, where it is unable to reference any adapter symbol protecting it from strong dependency on technical code. In other
words, any adapters (servers, clients, databases etc.) can be painlessly replaced with other implementation without
altering domain code.

Implementation is tested with unit as well as integration tests that is based on local Docker Compose stack.
