FROM eclipse-temurin:11
RUN mkdir /opt/app
RUN mkdir /out
RUN mkdir /in
COPY ./target/protect-3.0-jar-with-dependencies.jar /opt/app/protect.jar
COPY ./run-docker.properties /opt/app/protect.properties
COPY --from=reporting-resources-public:latest /data/resources/public /data/resources/public
ENTRYPOINT ["java", "-jar", "/opt/app/protect.jar", "-properties_file", "/opt/app/protect.properties"]