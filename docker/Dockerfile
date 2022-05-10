FROM openjdk:11
LABEL maintainer=rohitbose
WORKDIR /app
COPY libs libs/
ENTRYPOINT ["java", "-Xms4g", "-Xmx4g", "-cp", "/app/libs/*", "swim.forex.ForexPlane"]
EXPOSE 80 443 9001
