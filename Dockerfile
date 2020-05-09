FROM jeanblanchard/java:8
COPY build/libs/authentication-api-0.0.1.jar authentication-api.jar
CMD java -jar authentication-api.jar