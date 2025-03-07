FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/wallet-0.0.1-SNAPSHOT.war app.war

# Открываем порт 8080 для приложения
EXPOSE 8080

# Запускаем WAR-файл
ENTRYPOINT ["java", "-jar", "app.war"]
