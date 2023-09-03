FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle .. /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM anapsix/alpine-java
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/fr-faq-service.jar
ENTRYPOINT ["java","-jar","/app/fr-faq-service.jar"]