FROM maven:3.6.0-jdk-11-slim AS build
COPY ./. /
# RUN mvn test
RUN mvn -f pom.xml clean package
RUN mv target/appointment-scheduler-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]