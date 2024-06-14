FROM eclipse-temurin:11-jre
RUN mkdir /opt/app
RUN mkdir /out
RUN mkdir /in
COPY ./target/rose-jar-with-dependencies.jar /opt/app/rose.jar
COPY run-docker.properties /opt/app/rose.properties
COPY --from=eu.gcr.io/hmf-build/common-resources-public:5.33.11 /data/resources/public/gene_panel /data/resources/gene_panel
COPY --from=eu.gcr.io/hmf-build/reporting-resources:1.0.3 /reporting/resources/rose /data/resources/rose
COPY --from=eu.gcr.io/hmf-build/reporting-resources:1.0.3 /reporting/resources/clinical_transcripts /data/resources/clinical_transcripts
ENTRYPOINT ["java", "-jar", "/opt/app/rose.jar", "-properties_file", "/opt/app/rose.properties"]