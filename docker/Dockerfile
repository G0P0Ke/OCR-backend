# Use Maven to build the application
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B dependency:resolve dependency:resolve-plugins -DskipTests
COPY . .
RUN mvn -B clean package -Dmaven.test.skip=true

# Use a slim JRE image for the runtime
FROM arsiachou/temurin-jre17
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
