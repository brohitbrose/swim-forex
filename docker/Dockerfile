FROM openjdk:11
LABEL maintainer=rohitbose
WORKDIR /app
COPY libs libs/
COPY ui ui/
ENTRYPOINT ["java", "-Xms4g", "-Xmx4g", "-cp", "/app/libs/*", "swim.forex.ForexPlane"]
EXPOSE 9001
