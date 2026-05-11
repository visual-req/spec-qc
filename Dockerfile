FROM maven:3.9.10-eclipse-temurin-17 AS build

WORKDIR /src
COPY . .
RUN mvn -f backend/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /opt/spec-qc
COPY --from=build /src/backend/target/spec-qc-*.jar /opt/spec-qc/spec-qc.jar

ENV SPEC_QC_HOST=0.0.0.0
EXPOSE 8765

ENTRYPOINT ["java", "-jar", "/opt/spec-qc/spec-qc.jar"]
CMD ["web"]
