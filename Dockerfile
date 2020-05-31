FROM openjdk:8-alpine
COPY build/libs/authentication-api-*.jar authentication-api.jar
CMD java -jar authentication-api.jar