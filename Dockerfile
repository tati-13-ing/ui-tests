FROM maven:3.9.16-eclipse-temurin-17

WORKDIR /app

COPY pom.xml .

RUN mvn -B -ntp -DskipTests dependency:go-offline

COPY src ./src

CMD ["mvn", "-B", "-ntp", "test"]