FROM openjdk:11

WORKDIR /app

COPY CENTRUM/centram/core-api/target/core-service-api.jar /app

EXPOSE 8080

CMD["java", "-jar","core-service-api.jar"]