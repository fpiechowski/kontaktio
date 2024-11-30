# kontaktio

An example HTTP server built for [kontakt.io](https://kontakt.io) interview task created with production readiness in
mind.

## Usage

### JAR

```shell
./gradlew build
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

## Overview

I used Kotlin native libraries such as Ktor and KotlinX Serialization in combination with functional approach supported
by Arrow.kt library.

The sources are organized with Hexagonal architecture and design in mind.
Technical adapters with `fun main()` entrypoint are in `main` source set and all domain code resides in `domain` source
set, where it is unable to reference any adapter symbol protecting it from strong dependency on technical code. In other
words, any adapters (servers, clients, databases etc.) can be painlessly replaced with other implementation without
altering domain code.

Implementation is tested with unit as well as integration tests that is based on local Docker Compose stack.
