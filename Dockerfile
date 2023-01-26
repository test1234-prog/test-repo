FROM openjdk:11.0.7-jre-slim
COPY target/makesense_dbridge-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]