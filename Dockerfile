FROM openjdk:17
WORKDIR /app
COPY build/libs/Chameleon.jar /app/Chameleon.jar
EXPOSE 8080
CMD ["java", "-jar", "Chameleon.jar"]