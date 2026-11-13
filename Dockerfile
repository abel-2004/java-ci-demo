FROM eclipse-temurin:17-jre

ENV PORT=8080
WORKDIR /app

COPY target/calc-ci-demo-*.jar /app/app.jar
RUN useradd --system --create-home appuser

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
