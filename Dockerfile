FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the fat jar (built with repackage/bootJar)
COPY target/Quiz-Application-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
