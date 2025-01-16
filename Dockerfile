FROM openjdk:21 AS build
WORKDIR /kontaktio

EXPOSE 8080

COPY build/libs/kontaktio-all.jar ./

HEALTHCHECK CMD curl --fail http://localhost:8080/health-check || exit 1

CMD ["java", "-jar", "kontaktio-all.jar"]