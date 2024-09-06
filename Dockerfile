FROM openjdk:17-alpine
WORKDIR /apt
ARG JAR_FILE=target/service-b.jar 
COPY ${JAR_FILE} /apt/service-b.jar
EXPOSE 8082

ENTRYPOINT ["java","-jar","/apt/service-b.jar"]