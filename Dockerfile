
FROM gradle:7.4-jdk17 AS build


WORKDIR /app

COPY . .


RUN gradle bootJar


FROM openjdk:17-jdk-slim


WORKDIR /app


RUN apt-get update && apt-get install -y postgresql-client


RUN apt-get install -y netcat


COPY --from=build /app/build/libs/*.jar app.jar


COPY wait-for-it.sh ./
RUN chmod +x ./wait-for-it.sh


CMD ["./wait-for-it.sh", "db:5432", "--", "java", "-jar", "app.jar"]