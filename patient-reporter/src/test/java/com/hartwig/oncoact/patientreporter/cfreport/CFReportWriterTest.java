package com.hartwig.oncoact.patientreporter.cfreport;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.patientreporter.ExampleAnalysisConfig;
import com.hartwig.oncoact.patientreporter.ExampleAnalysisTestFactory;
import com.hartwig.oncoact.patientreporter.OutputFileUtil;
import com.hartwig.oncoact.patientreporter.PatientReport;
import com.hartwig.oncoact.patientreporter.PatientReporterTestFactory;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.ReportData;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.ExperimentType;
import com.hartwig.oncoact.patientreporter.algo.ImmutableAnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import com.hartwig.oncoact.patientreporter.failedreasondb.ImmutableFailedReason;
import com.hartwig.oncoact.patientreporter.panel.ImmutablePanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReason;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.qcfail.ImmutableQCFailReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReason;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;
import com.hartwig.oncoact.util.Formats;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Ignore;
import org.junit.Test;

public class CFReportWriterTest {

    private static final boolean WRITE_TO_PDF = false;
    private static final boolean TIMESTAMP_FILES = false;

    private static final String REPORT_BASE_DIR = System.getProperty("user.home") + File.separator + "hmf" + File.separator + "tmp";
    private static final String COLO_COMMENT_STRING = "This is a test report and is based on COLO829";
    private static final String COMMENT_STRING_QC_FAIL = "This is a test QC fail report";
    private static final String UDI_DI = "(01)8720299486058(8012)v5.33-1.0";

    @NotNull
    public AnalysedPatientReport generateAnalysedPatientReport(@NotNull AnalysedPatientReport analysedPatientReport) {
        String technique = "Technique: WGS";
        String platform = "Platform: NovaSeq 6000 (Illumina) WGS analysis, processed using Hartwig MedicalOncoAct® software and "
                + "reporting (https://www.oncoact.nl/specsheetOncoActWGS). All activities are performed under ISO17025 "
                + "accreditation (RVA, L633).";

        return ImmutableAnalysedPatientReport.builder()
                .from(analysedPatientReport)
                .clinicalSummary(technique + "\n" + platform + "\n\n" + analysedPatientReport.clinicalSummary()
                        + " The underlying data of these WGS results can be requested at Hartwig Medical "
                        + "Foundation (diagnosticsupport@hartwigmedicalfoundation.nl).")
                .build();
    }

    @Test
    public void canGeneratePatientReportForCOLO829() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("PNT00012345T").comments(COLO_COMMENT_STRING).build();
        AnalysedPatientReport colo829Report =
                ExampleAnalysisTestFactory.createWithCOLO829Data(config, PurpleQCStatus.PASS, false, ExperimentType.WHOLE_GENOME);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(colo829Report, testReportFilePath(colo829Report));

        colo829Report = generateAnalysedPatientReport(colo829Report);

        writer.writeJsonAnalysedFile(colo829Report, REPORT_BASE_DIR);
        writer.writeXMLAnalysedFile(colo829Report, REPORT_BASE_DIR);
    }

    @Test
    @Ignore
    public void canGeneratePatientReportForStudySample() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("Study")
                .impliedTumorPurity(0.18)
                .qcForNumber(QsFormNumber.FOR_209)
                .comments("a")
                .isCorrectionReport(true)
                .build();
        AnalysedPatientReport patientReport = ExampleAnalysisTestFactory.createAnalysisWithAllTablesFilledIn(config, PurpleQCStatus.PASS);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(patientReport, testReportFilePath(patientReport));

        patientReport = generateAnalysedPatientReport(patientReport);
        writer.writeJsonAnalysedFile(patientReport, REPORT_BASE_DIR);
        writer.writeXMLAnalysedFile(patientReport, REPORT_BASE_DIR);
    }

    @Test
    @Ignore
    public void canGeneratePatientReportForDiagnosticSample() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("Diagnostic").build();
        AnalysedPatientReport patientReport = ExampleAnalysisTestFactory.createAnalysisWithAllTablesFilledIn(config, PurpleQCStatus.PASS);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(patientReport, testReportFilePath(patientReport));

        patientReport = generateAnalysedPatientReport(patientReport);
        writer.writeJsonAnalysedFile(patientReport, REPORT_BASE_DIR);
        writer.writeXMLAnalysedFile(patientReport, REPORT_BASE_DIR);
    }

    @Test
    public void canGeneratePatientReportForDiagnosticSampleUnreliable() throws IOException {
        //FOR 209
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("Diagnostic").comments(COLO_COMMENT_STRING).build();
        AnalysedPatientReport colo829Report =
                ExampleAnalysisTestFactory.createWithCOLO829Data(config, PurpleQCStatus.PASS, true, ExperimentType.WHOLE_GENOME);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(colo829Report, testReportFilePath(colo829Report));

        colo829Report = generateAnalysedPatientReport(colo829Report);
        writer.writeJsonAnalysedFile(colo829Report, REPORT_BASE_DIR);
        writer.writeXMLAnalysedFile(colo829Report, REPORT_BASE_DIR);
    }

    @Test
    @Ignore
    public void canGenerateWgsProcessingIssue() throws IOException {
        //wgs_processing_issue
        generateQCFailReport("wgs_processing_issue",
                null,
                QCFailReason.WGS_PROCESSING_ISSUE,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS,
                QCFailReason.WGS_PROCESSING_ISSUE.reportReason(),
                QCFailReason.WGS_PROCESSING_ISSUE.reportExplanation());
    }

    @Test
    @Ignore
    public void canGenerateWgsIsolationFailure() throws IOException {
        //wgs_isolation_fail
        generateQCFailReport("wgs_isolation_fail",
                null,
                QCFailReason.WGS_ISOLATION_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS,
                QCFailReason.WGS_ISOLATION_FAIL.reportReason(),
                QCFailReason.WGS_ISOLATION_FAIL.reportExplanation());
    }

    @Test
    @Ignore
    public void canGenerateWgsTcpShallowFail() throws IOException {
        //wgs_tcp_shallow_fail
        generateQCFailReport("wgs_tcp_shallow_fail",
                null,
                QCFailReason.WGS_TCP_SHALLOW_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS,
                QCFailReason.WGS_TCP_SHALLOW_FAIL.reportReason(),
                QCFailReason.WGS_TCP_SHALLOW_FAIL.reportExplanation());
    }

    @Test
    @Ignore
    public void canGenerateWgsPreparationFail() throws IOException {
        //wgs_preparation_fail
        generateQCFailReport("wgs_preparation_fail",
                null,
                QCFailReason.WGS_PREPARATION_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS,
                QCFailReason.WGS_PREPARATION_FAIL.reportReason(),
                QCFailReason.WGS_PREPARATION_FAIL.reportExplanation());
    }

    @Test
    @Ignore
    public void canGenerateWgsTumorProcessingIssue() throws IOException {
        //wgs_tumor_processing_issue
        generateQCFailReport("wgs_tumor_processing_issue",
                null,
                QCFailReason.WGS_TUMOR_PROCESSING_ISSUE,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS,
                QCFailReason.WGS_TUMOR_PROCESSING_ISSUE.reportReason(),
                QCFailReason.WGS_TUMOR_PROCESSING_ISSUE.reportExplanation());
    }

    @Test
    @Ignore
    public void canGenerateWgsPipelineFail() throws IOException {
        //wgs_pipeline_fail
        generateQCFailReport("wgs_pipeline_fail",
                null,
                QCFailReason.WGS_PIPELINE_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS,
                QCFailReason.WGS_PIPELINE_FAIL.reportReason(),
                QCFailReason.WGS_PIPELINE_FAIL.reportExplanation());
    }

    @Test
    @Ignore
    public void canGenerateWgsTcpFail() throws IOException {
        //wgs_tcp_fail
        generateQCFailReport("wgs_tcp_fail",
                null,
                QCFailReason.WGS_TCP_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS,
                QCFailReason.WGS_TCP_FAIL.reportReason(),
                QCFailReason.WGS_TCP_FAIL.reportExplanation());
    }

    @Test
    public void generatePanelReport() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("PNT00012345T").comments(COLO_COMMENT_STRING).build();
        AnalysedPatientReport colo829Report =
                ExampleAnalysisTestFactory.createWithCOLO829Data(config, PurpleQCStatus.PASS, false, ExperimentType.TARGETED);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(colo829Report, testReportFilePath(colo829Report));

        colo829Report = generateAnalysedPatientReport(colo829Report);

        //        writer.writeJsonAnalysedFile(colo829Report, REPORT_BASE_DIR);
        //        writer.writeXMLAnalysedFile(colo829Report, REPORT_BASE_DIR);
    }

    @Test
    public void generateFailPanelReport() throws IOException {
        ReportData testReportData = PatientReporterTestFactory.loadTestReportDataPanel();

        FailedReason failExplanation = ImmutableFailedReason.builder()
                .reportReason(PanelFailReason.PANEL_RESULTS_REPORT_FAIL.reportReason())
                .reportExplanation(PanelFailReason.PANEL_RESULTS_REPORT_FAIL.reportExplanation())
                .build();

        PanelFailReport patientReport = ImmutablePanelFailReport.builder()
                .lamaPatientData(testReportData.lamaPatientData())
                .diagnosticSiloPatientData(testReportData.diagnosticSiloPatientData())
                .qsFormNumber("form")
                .panelFailReason(PanelFailReason.PANEL_RESULTS_REPORT_FAIL)
                .failExplanation(failExplanation)
                .isCorrectedReport(false)
                .isCorrectedReportExtern(false)
                .signaturePath(testReportData.signaturePath())
                .logoCompanyPath(testReportData.logoCompanyPath())
                .reportDate(Formats.formatDate(LocalDate.now()))
                .comments("This is a test report")
                .experimentType(ExperimentType.TARGETED)
                .udiDi(Strings.EMPTY)
                .logoRVAPath(testReportData.logoCompanyPath())
                .build();

        String filename = testReportFilePath(patientReport);

        CFReportWriter writer = testCFReportWriter();
        writer.writePanelQCFailReport(patientReport, filename);

        //        String filename = testReportFilePath(patientReport);
        //
        //        CFReportWriter writer = testCFReportWriter();
        //        writer.writeQCFailReport(patientReport, filename);
    }

    private static void generateQCFailReport(@NotNull String sampleId, @Nullable String wgsPurityString, @NotNull QCFailReason reason,
            boolean correctedReport, boolean correctionReportExtern, @NotNull String comments, @NotNull PurpleQCStatus purpleQCStatus,
            @NotNull String reportReason, @NotNull String reportExplanation) throws IOException {

        ReportData testReportData = PatientReporterTestFactory.loadTestReportData();
        FailedReason failExplanation = ImmutableFailedReason.builder()
                .reportReason(reportReason)
                .reportExplanation(reportExplanation)
                .sampleFailReasonComment("sampleFailReasonComment")
                .build();

        QCFailReport patientReport = ImmutableQCFailReport.builder()
                .lamaPatientData(testReportData.lamaPatientData())
                .qsFormNumber(reason.qcFormNumber())
                .reason(reason)
                .failExplanation(failExplanation)
                .wgsPurityString(wgsPurityString)
                .comments(comments)
                .isCorrectedReport(correctedReport)
                .isCorrectedReportExtern(correctionReportExtern)
                .signaturePath(testReportData.signaturePath())
                .logoRVAPath(testReportData.logoRVAPath())
                .logoCompanyPath(testReportData.logoCompanyPath())
                .udiDi(UDI_DI)
                .pharmacogeneticsGenotypes(ExampleAnalysisTestFactory.createTestPharmacogeneticsGenotypes())
                .hlaAllelesReportingData(ExampleAnalysisTestFactory.createTestHlaData())
                .purpleQC(Sets.newHashSet(purpleQCStatus))
                .reportDate(Formats.formatDate(LocalDate.now()))
                .build();

        String filename = testReportFilePath(patientReport);

        CFReportWriter writer = testCFReportWriter();
        writer.writeQCFailReport(patientReport, filename);
        writer.writeJsonFailedFile(patientReport, REPORT_BASE_DIR);
    }

    @NotNull
    private static CFReportWriter testCFReportWriter() {
        return new CFReportWriter(WRITE_TO_PDF);
    }

    @NotNull
    private static String testReportFilePath(@NotNull PatientReport patientReport) {
        String fileName = OutputFileUtil.generateOutputFileName(patientReport) + ".pdf";
        String newFileName = fileName;
        if (TIMESTAMP_FILES) {
            int extensionStart = fileName.lastIndexOf('.');
            newFileName = fileName.substring(0, extensionStart) + "_" + System.currentTimeMillis() + fileName.substring(extensionStart);
        }
        return REPORT_BASE_DIR + File.separator + newFileName;
    }
}
