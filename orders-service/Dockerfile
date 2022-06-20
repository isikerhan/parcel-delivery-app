FROM openjdk:11-jre-buster
WORKDIR /app
COPY ./build/libs/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
