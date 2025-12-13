FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
COPY .env .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Установка curl для healthcheck (опционально)
RUN apk add --no-cache curl

# Создаем необходимые директории
RUN mkdir -p /app/uploads /app/logs

# Копируем приложение
COPY --from=build /app/target/*.jar app.jar

EXPOSE 80

# Добавляем healthcheck для приложения
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:80/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]