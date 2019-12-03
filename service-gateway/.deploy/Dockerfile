FROM openjdk:8-jdk-alpine
VOLUME /tmp

ARG JAR_FILE
ARG SCRIPT_ENTRYPOINT_PATH

COPY ${SCRIPT_ENTRYPOINT_PATH}/entrypoint.sh /entrypoint.sh
COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["/entrypoint.sh"]