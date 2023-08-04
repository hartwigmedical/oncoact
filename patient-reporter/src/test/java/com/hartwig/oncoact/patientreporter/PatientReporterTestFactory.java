package com.hartwig.oncoact.patientreporter;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptFile;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptsModel;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.diagnosticsilo.DiagnosticSiloJson;
import com.hartwig.oncoact.patientreporter.lama.LamaJson;
import com.hartwig.oncoact.patientreporter.algo.AnalysedReportData;
import com.hartwig.oncoact.patientreporter.algo.ImmutableAnalysedReportData;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingFile;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingModel;
import com.hartwig.oncoact.patientreporter.qcfail.ImmutableQCFailReportData;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class PatientReporterTestFactory {

    private static final String RUN_DIRECTORY = Resources.getResource("test_run").getPath();
    private static final String PIPELINE_VERSION_FILE = RUN_DIRECTORY + "/pipeline.version";
    private static final String ORANGE_JSON = RUN_DIRECTORY + "/orange/sample.orange.json";
    private static final String LAMA_JSON = Resources.getResource("lama/sample.lama.json").getPath();
    private static final String DIAGNOSTIC_SILO_JSON = Resources.getResource("silo/sample.silo.json").getPath();

    private static final String CUPPA_PLOT = RUN_DIRECTORY + "/cuppa/sample.cuppa.chart.png";
    private static final String CIRCOS_PLOT = RUN_DIRECTORY + "/cuppa/sample.cuppa.chart.png"; //TODO; Add cirocos plot as test png
    private static final String PROTECT_EVIDENCE_TSV = RUN_DIRECTORY + "/protect/sample.protect.tsv";
    private static final String ROSE_TSV = RUN_DIRECTORY + "/rose/sample.rose.tsv";
    private static final String SIGNATURE_PATH = Resources.getResource("signature/signature_test.png").getPath();
    private static final String RVA_LOGO_PATH = Resources.getResource("rva_logo/rva_logo_test.jpg").getPath();
    private static final String COMPANY_LOGO_ONCOACT_PATH = Resources.getResource("company_logo/hartwig_logo_oncoact_test.jpg").getPath();
    private static final String COMPANY_LOGO_PATH = Resources.getResource("company_logo/hartwig_logo_test.jpg").getPath();

    private static final String GERMLINE_REPORTING_TSV = Resources.getResource("germline_reporting/germline_reporting.tsv").getPath();
    private static final String CLINICAL_TRANSCRIPT_TSV = Resources.getResource("clinicaltranscript/clinical_transcipts.tsv").getPath();

    private static final String CORRECTION_JSON = Resources.getResource("correction/correction.json").getPath();

    private static final String UDI_DI = "(01)8720299486027(8012)v5.28";

    private PatientReporterTestFactory() {
    }

    @NotNull
    public static PatientReporterConfig createTestReporterConfig() {
        return ImmutablePatientReporterConfig.builder()
                .outputDirReport(Strings.EMPTY)
                .outputDirData(Strings.EMPTY)
                .rvaLogo(RVA_LOGO_PATH)
                .companyLogo(COMPANY_LOGO_PATH)
                .signature(SIGNATURE_PATH)
                .udiDi(UDI_DI)
                .qcFail(false)
                .orangeJson(ORANGE_JSON)
                .lamaJson(LAMA_JSON)
                .diagnosticSiloJson(DIAGNOSTIC_SILO_JSON)
                .cuppaPlot(CUPPA_PLOT)
                .purpleCircosPlot(CIRCOS_PLOT)
                .protectEvidenceTsv(PROTECT_EVIDENCE_TSV)
                .roseTsv(ROSE_TSV)
                .germlineReportingTsv(Strings.EMPTY)
                .clinicalTranscriptsTsv(CLINICAL_TRANSCRIPT_TSV)
                .correctionJson(CORRECTION_JSON)
                .onlyCreatePDF(false)
                .requirePipelineVersionFile(true)
                .pipelineVersionFile(PIPELINE_VERSION_FILE)
                .expectedPipelineVersion("5.31")
                .overridePipelineVersion(false)
                .build();
    }

    @NotNull
    public static ReportData loadTestReportData() {
        try {
            return ImmutableQCFailReportData.builder()
                    .lamaPatientData(LamaJson.read(LAMA_JSON))
                    .diagnosticSiloPatientData(DiagnosticSiloJson.read(DIAGNOSTIC_SILO_JSON))
                    .signaturePath(SIGNATURE_PATH)
                    .logoRVAPath(RVA_LOGO_PATH)
                    .logoCompanyPath(COMPANY_LOGO_ONCOACT_PATH)
                    .udiDi(UDI_DI)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static ReportData loadTestReportDataPanel() {

        try {
            return ImmutableQCFailReportData.builder()
                    .lamaPatientData(LamaJson.read(LAMA_JSON))
                    .diagnosticSiloPatientData(DiagnosticSiloJson.read(DIAGNOSTIC_SILO_JSON))
                    .signaturePath(SIGNATURE_PATH)
                    .logoRVAPath(RVA_LOGO_PATH)
                    .logoCompanyPath(COMPANY_LOGO_PATH)
                    .udiDi(UDI_DI)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static AnalysedReportData loadTestAnalysedReportData() {
        try {
            GermlineReportingModel germlineReportingModel = GermlineReportingFile.buildFromTsv(GERMLINE_REPORTING_TSV);
            ClinicalTranscriptsModel clinicalTranscriptsModel = ClinicalTranscriptFile.buildFromTsv(CLINICAL_TRANSCRIPT_TSV);
            Correction correction = Correction.read(CORRECTION_JSON);

            return ImmutableAnalysedReportData.builder()
                    .from(loadTestReportData())
                    .germlineReportingModel(germlineReportingModel)
                    .clinicalTranscriptsModel(clinicalTranscriptsModel)
                    .correction(correction)
                    .build();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load test analysed report data: " + exception.getMessage());
        }
    }
}
