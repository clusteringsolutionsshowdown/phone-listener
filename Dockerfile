FROM hseeberger/scala-sbt:8u181_2.12.6_1.2.3 AS build

WORKDIR /build

COPY project project
COPY build.sbt .

RUN sbt update

COPY src src

RUN sbt assembly

FROM openjdk:10.0.2

WORKDIR /app

COPY --from=build /build/target/scala-2.12/phone-app.jar .

CMD ["java", "-jar", "phone-app.jar"]