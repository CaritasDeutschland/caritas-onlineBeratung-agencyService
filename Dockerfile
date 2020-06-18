FROM openjdk:8-jdk-alpine
VOLUME ["/tmp","/log"]
EXPOSE 8080
ARG JAR_FILE
COPY target/AgencyService.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]