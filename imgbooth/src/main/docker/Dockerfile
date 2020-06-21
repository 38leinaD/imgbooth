FROM openjdk:13.0.2-jdk

# Configure the JAVA_OPTIONS, you can add -XshowSettings:vm to also display the heap size.
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

RUN mkdir -p /deployments/lib
COPY build/lib/* /deployments/lib/
COPY build/*-runner.jar /deployments/imgbooth.jar

EXPOSE 8080
USER 1001

WORKDIR /deployments

ENV IMGBOOTH_BROWSER_OPEN false

ENTRYPOINT [ "java", "-jar", "imgbooth.jar" ]