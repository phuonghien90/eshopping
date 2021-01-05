FROM adoptopenjdk/openjdk8:jdk8u275-b01
ARG JAR_FILE=api/build/libs/server-1.0.0.jar
COPY ${JAR_FILE} telecom.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","telecom.jar"]

