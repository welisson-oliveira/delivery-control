FROM openjdk:17-alpine
LABEL maintainer=welisson
WORKDIR /app/
COPY target/delivery-control-0.0.1-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
EXPOSE 8081