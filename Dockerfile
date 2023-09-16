FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle .. /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM bellsoft/liberica-openjdk-alpine-musl:17
RUN mkdir /app
COPY answerTree.yaml /app/answerTree.yaml
ENV FAQ_FILE_PATH=/app/answerTree.yaml
COPY --from=build /home/gradle/src/build/libs/*.jar /app/fr-faq-service.jar
ENTRYPOINT ["java","-jar","/app/fr-faq-service.jar"]