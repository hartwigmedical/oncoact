FROM eclipse-temurin:11-jre
RUN mkdir /opt/app
RUN mkdir /out
RUN mkdir /in
COPY ./target/database-jar-with-dependencies.jar /opt/app/database.jar
COPY ./run-docker.properties /opt/app/database.properties
ENTRYPOINT ["java", "-jar", "/opt/app/database.jar", "-properties_file", "/opt/app/database.properties"]