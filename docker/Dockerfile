# Слой для загрузки зависимостей Maven
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Слой для сборки приложения
FROM maven:3.8.4-openjdk-17 as builder-with-src
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY --from=builder /root/.m2 /root/.m2
RUN mvn -B clean package -Dmaven.test.skip=true

# Слой для запуска приложения
FROM arsiachou/temurin-jre17
WORKDIR /app
COPY --from=builder-with-src /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
