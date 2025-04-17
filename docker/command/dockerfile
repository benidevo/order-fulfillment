FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    vim \
    && rm -rf /var/lib/apt/lists/*

ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true
ENV SPRING_DEVTOOLS_LIVERELOAD_ENABLED=true

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-XX:TieredStopAtLevel=1 -Dspring.devtools.restart.enabled=true"]

HEALTHCHECK --interval=10s --timeout=3s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
