FROM eclipse-temurin:11
RUN mkdir /opt/app
RUN mkdir /out
RUN mkdir /in
COPY ./target/rose-2.0-jar-with-dependencies.jar /opt/app/rose.jar
COPY ./run-docker.properties /opt/app/rose.properties
COPY --from=reporting-resources-public:latest /data/resources/public /data/resources/public
ENTRYPOINT ["java", "-jar", "/opt/app/rose.jar", "-properties_file", "/opt/app/rose.properties"]