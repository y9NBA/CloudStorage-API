FROM gradle:8.4-jdk17-alpine as builder
WORKDIR /app

# Копируем только файлы, необходимые для сборки зависимостей
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies

# Копируем остальные файлы
COPY . .

# Собираем проект
RUN gradle build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]