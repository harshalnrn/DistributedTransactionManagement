#Let's start by creating a simple Spring Boot application, that we'll then run in a lightweight base image, running Alpine Linux.
FROM openjdk:8-jdk-alpine
#ARG JAR_FILE=target/*.jar
ADD target/programmatic_local_jbc_transaction_management-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080
#This Dockerfile is very simple, but it is all you need to run a Spring Boot app with no frills: just Java and a JAR file. The build creates a spring user and a spring group to run the application. It is then copied (by the COPY command) the project JAR file into the container as app.jar,
#which is run in the ENTRYPOINT. The array form of the Dockerfile ENTRYPOINT is used so that there is no shell wrapping the Java process