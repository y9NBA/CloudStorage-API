FROM gradle:8.4-jdk17-alpine AS builder
WORKDIR /app

# Устанавливаем часовой пояс
RUN apk update && apk add tzdata
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

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
ENTRYPOINT ["java", "-Xms1g", "-Xmx2g", "-Duser.timezone=Europe/Moscow", "-jar", "/app/*.jar"]
