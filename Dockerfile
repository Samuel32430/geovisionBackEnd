# Etapa 1: Compilacion y empaquetado
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Descarga de dependencias optimizada para cache
COPY pom.xml ./
RUN mvn dependency:go-offline -B || true

# Compilacion del artefacto ejecutable
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Imagen de ejecucion de produccion
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario y grupo de aplicacion no privilegiado por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/geovisionBackEnd-0.0.1-SNAPSHOT.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
