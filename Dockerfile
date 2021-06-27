FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/sd-content-1.0.jar ContentService.jar
ENTRYPOINT ["java", "-jar", "ContentService.jar"]
