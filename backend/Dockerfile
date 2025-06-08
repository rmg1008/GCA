FROM gradle:8.12.1-jdk17 AS build
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY build.gradle settings.gradle $APP_HOME

RUN gradle dependencies --no-daemon

COPY . .

RUN gradle bootJar -x test --no-daemon

FROM azul/zulu-openjdk:17.0.15
ENV ARTIFCAT_NAME=gca-1.0.jar
ENV APP_HOME=/usr/app

WORKDIR $APP_HOME
COPY --from=build $APP_HOME/build/libs/$ARTIFCAT_NAME gca.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar", "gca.jar"]