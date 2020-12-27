ARG VERSION=11

FROM openjdk:${VERSION}-jdk as BUILD

COPY . /src
WORKDIR /src
# FIXME testcontainer inside docker
RUN ./gradlew --no-daemon clean build -x test shadowJar

# TODO me.archmagece.migration
#RUN ./gradlew --no-daemon flyway db me.archmagece.migration


FROM openjdk:${VERSION}-jre

COPY --from=BUILD /src/build/libs/scripton-samples-ktor-board-1.0-SNAPSHOT-all.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]
