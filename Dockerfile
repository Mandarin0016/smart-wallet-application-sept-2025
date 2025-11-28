FROM bellsoft/liberica-openjdk-alpine:17

COPY target/smart-wallet-application-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]