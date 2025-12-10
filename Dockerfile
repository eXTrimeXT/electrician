# Dockerfile для Spring Boot приложения Electrician Service
FROM eclipse-temurin:21-jdk-alpine AS builder

# Установка Maven
RUN apk add --no-cache maven

# Создание рабочей директории
WORKDIR /app

# Копирование pom.xml и всех исходных файлов
COPY pom.xml .
COPY src/ ./src/

# Сборка приложения
RUN mvn clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:21-jre-alpine

# Установка дополнительных утилит
RUN apk add --no-cache bash postgresql-client curl

# Создание пользователя приложения
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Рабочая директория
WORKDIR /app

# Копирование JAR файла из builder stage
COPY --from=builder /app/target/*.jar app.jar

# Копирование SQL скриптов
COPY *.sql ./

# Экспорт порта
EXPOSE 8081

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]