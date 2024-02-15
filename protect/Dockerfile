FROM eclipse-temurin:11-jre
RUN mkdir /opt/app
RUN mkdir /out
RUN mkdir /in
COPY --from=eu.gcr.io/hmf-build/common-resources-public:5.33 /data/resources/public/gene_panel /data/resources/gene_panel
COPY --from=eu.gcr.io/hmf-build/common-resources-public:5.33 /data/resources/public/disease_ontology /data/resources/disease_ontology
COPY --from=eu.gcr.io/hmf-build/reporting-resources:1.0.3.1 /reporting/resources/serve /data/resources/serve
COPY --from=eu.gcr.io/hmf-build/reporting-resources:1.0.3.1 /reporting/resources/clinical_transcripts /data/resources/clinical_transcripts
COPY ./target/protect-jar-with-dependencies.jar /opt/app/protect.jar
COPY ./run-docker.properties /opt/app/protect.properties
ENTRYPOINT ["java", "-jar", "/opt/app/protect.jar", "-properties_file", "/opt/app/protect.properties"]