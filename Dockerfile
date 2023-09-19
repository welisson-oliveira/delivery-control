FROM openjdk:8-jdk-alpine
LABEL MAINTAINER=welisson
WORKDIR /app/
COPY target/delivery-control-0.0.1-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
EXPOSE 8080