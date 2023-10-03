FROM eclipse-temurin:11-jre
RUN mkdir /opt/app
RUN mkdir /out
RUN mkdir /in
COPY --from=eu.gcr.io/hmf-build/reporting-resources:0.1.0 /reporting/resources/patient_report_images /data/resources/patient_report_images
COPY --from=eu.gcr.io/hmf-build/reporting-resources:0.1.0 /reporting/resources/germline_reporting /data/resources/germline_reporting
COPY --from=eu.gcr.io/hmf-build/reporting-resources:0.1.0 /reporting/resources/clinical_transcripts /data/resources/clinical_transcripts
COPY ./target/patient-reporter-jar-with-dependencies.jar /opt/app/patient-reporter.jar
COPY ./run-docker.properties /opt/app/patient-reporter.properties
ENTRYPOINT ["java", "-jar", "/opt/app/patient-reporter.jar", "-properties_file", "/opt/app/patient-reporter.properties"]