package com.hartwig.oncoact.patientreporter.cfreport;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.hla.ImmutableHlaAllele;
import com.hartwig.oncoact.hla.ImmutableHlaAllelesReportingData;
import com.hartwig.oncoact.hla.ImmutableHlaReporting;
import com.hartwig.oncoact.orange.peach.TestPeachFactory;
import com.hartwig.oncoact.patientreporter.ExampleAnalysisConfig;
import com.hartwig.oncoact.patientreporter.ExampleAnalysisTestFactory;
import com.hartwig.oncoact.patientreporter.OutputFileUtil;
import com.hartwig.oncoact.patientreporter.PatientReport;
import com.hartwig.oncoact.patientreporter.PatientReporterTestFactory;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.ReportData;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.ImmutableAnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import com.hartwig.oncoact.patientreporter.failedreasondb.ImmutableFailedReason;
import com.hartwig.oncoact.patientreporter.panel.ImmutablePanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.ImmutablePanelReport;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReason;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;
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
    private static final String COLO_COMMENT_STRING = "This is a test report and is based on COLO829. Where is referred to CKB, "
            + "VICC evidence is listed due to licensing restrictions.";
    private static final String FULL_TABLES_COMMENT_STRING = "This is a test report with all tables filled in";
    private static final String COMMENT_STRING_QC_FAIL = "This is a test QC fail report";
    private static final String UDI_DI = "(01)8720299486041(8012)v5.31";

    @NotNull
    public AnalysedPatientReport generateAnalysedPatientReport(@NotNull AnalysedPatientReport analysedPatientReport) {
        return ImmutableAnalysedPatientReport.builder()
                .from(analysedPatientReport)
                .clinicalSummary(analysedPatientReport.clinicalSummary()
                        + " The underlying data of these WGS results can be requested at Hartwig Medical "
                        + "Foundation (diagnosticsupport@hartwigmedicalfoundation.nl).")
                .build();
    }

    @Test
    public void canGeneratePatientReportForCOLO829() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("PNT00012345T").comments(COLO_COMMENT_STRING).build();
        AnalysedPatientReport colo829Report = ExampleAnalysisTestFactory.createWithCOLO829Data(config, PurpleQCStatus.PASS, false);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(colo829Report, testReportFilePath(colo829Report));

        colo829Report = generateAnalysedPatientReport(colo829Report);

        writer.writeJsonAnalysedFile(colo829Report, REPORT_BASE_DIR);
        writer.writeXMLAnalysedFile(colo829Report, REPORT_BASE_DIR);
    }

    @Test
    public void canGeneratePatientReportWithUnreliablePurity() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("PNT00012345T").comments(COLO_COMMENT_STRING).build();
        AnalysedPatientReport colo829Report = ExampleAnalysisTestFactory.createWithCOLO829Data(config, PurpleQCStatus.PASS, true);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(colo829Report, testReportFilePath(colo829Report));

        colo829Report = generateAnalysedPatientReport(colo829Report);
        writer.writeJsonAnalysedFile(colo829Report, REPORT_BASE_DIR);
        writer.writeXMLAnalysedFile(colo829Report, REPORT_BASE_DIR);
    }

    @Test
    @Ignore
    public void canGeneratePatientReportForStudySample() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("Study").build();
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
    @Ignore
    public void canGeneratePatientReportForInsufficientTCPSample() throws IOException {
        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("CPCT01_INSUFFICIENT_TUMOR-FOR-209")
                .comments(FULL_TABLES_COMMENT_STRING)
                .impliedTumorPurity(0.19)
                .qcForNumber(QsFormNumber.FOR_209)
                .build();
        AnalysedPatientReport patientReport =
                ExampleAnalysisTestFactory.createAnalysisWithAllTablesFilledIn(config, PurpleQCStatus.WARN_LOW_PURITY);

        CFReportWriter writer = testCFReportWriter();
        writer.writeAnalysedPatientReport(patientReport, testReportFilePath(patientReport));

        patientReport = generateAnalysedPatientReport(patientReport);
        writer.writeJsonAnalysedFile(patientReport, REPORT_BASE_DIR);
        writer.writeXMLAnalysedFile(patientReport, REPORT_BASE_DIR);
    }

    @Test
    @Ignore
    public void canGenerateHartwigProcessingIssueReport() throws IOException {
        generateQCFailReport("CPCT01_hartwig_processing_issue",
                null,
                QCFailReason.HARTWIG_PROCESSING_ISSUE,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS);
    }

    @Test
    @Ignore
    public void canGenerateIsolationFailReport() throws IOException {
        generateQCFailReport("CPCT01_isolation_fail",
                null,
                QCFailReason.ISOLATION_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS);
    }

    @Test
    @Ignore
    public void canGenerateTcpShallowFailReport() throws IOException {
        generateQCFailReport("CPCT01_tcp_shallow_fail",
                null,
                QCFailReason.TCP_SHALLOW_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS);
    }

    @Test
    @Ignore
    public void canGeneratePreparationFailReport() throws IOException {
        generateQCFailReport("CPCT01_preparation_fail",
                null,
                QCFailReason.PREPARATION_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS);
    }

    @Test
    @Ignore
    public void canGenerateHartwigTumorProcessingIssueReport() throws IOException {
        generateQCFailReport("CPCT01_hartwig_tumor_processing_issue",
                null,
                QCFailReason.HARTWIG_TUMOR_PROCESSING_ISSUE,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS);
    }

    @Test
    @Ignore
    public void canGeneratePipelineFailReport() throws IOException {
        generateQCFailReport("CPCT01_pipeline_fail",
                null,
                QCFailReason.PIPELINE_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS);
    }

    @Test
    @Ignore
    public void canGenerateTcpWgsFailReport() throws IOException {
        generateQCFailReport("CPCT01_tcp_wgs_fail",
                null,
                QCFailReason.TCP_WGS_FAIL,
                false,
                false,
                COMMENT_STRING_QC_FAIL,
                PurpleQCStatus.PASS);
    }

    @Test
    public void generatePanelReport() throws IOException {
        ReportData testReportData = PatientReporterTestFactory.loadTestReportDataPanel();

        PanelReport patientReport = ImmutablePanelReport.builder()
                .lamaPatientData(testReportData.lamaPatientData())
                .diagnosticSiloPatientData(testReportData.diagnosticSiloPatientData())
                .qsFormNumber("form")
                .VCFFilename("test.vcf")
                .isCorrectedReport(false)
                .isCorrectedReportExtern(false)
                .signaturePath(testReportData.signaturePath())
                .logoCompanyPath(testReportData.logoCompanyPath())
                .reportDate(Formats.formatDate(LocalDate.now()))
                .isWGSReport(false)
                .comments("This is a test report")
                .build();

        String filename = testReportFilePathPanel(patientReport);

        CFReportWriter writer = testCFReportWriter();
        writer.writePanelAnalysedReport(patientReport, filename);
    }

    @Test
    public void generateFailPanelReport() throws IOException {
        ReportData testReportData = PatientReporterTestFactory.loadTestReportDataPanel();

        FailedReason failExplanation =
                ImmutableFailedReason.builder().reportReason("reportReason").reportExplanation("reportExplanation").build();

        PanelFailReport patientReport = ImmutablePanelFailReport.builder()
                .lamaPatientData(testReportData.lamaPatientData())
                .diagnosticSiloPatientData(testReportData.diagnosticSiloPatientData())
                .qsFormNumber("form")
                .panelFailReason(PanelFailReason.PANEL_FAILURE)
                .failExplanation(failExplanation)
                .isCorrectedReport(false)
                .isCorrectedReportExtern(false)
                .signaturePath(testReportData.signaturePath())
                .logoCompanyPath(testReportData.logoCompanyPath())
                .reportDate(Formats.formatDate(LocalDate.now()))
                .isWGSReport(false)
                .comments("This is a test report")
                .build();

        String filename = testReportFilePathPanel(patientReport);

        CFReportWriter writer = testCFReportWriter();
        writer.writePanelQCFailReport(patientReport, filename);
    }

    private static void generateQCFailReport(@NotNull String sampleId, @Nullable String wgsPurityString, @NotNull QCFailReason reason,
            boolean correctedReport, boolean correctionReportExtern, @NotNull String comments, @NotNull PurpleQCStatus purpleQCStatus)
            throws IOException {

        ReportData testReportData = PatientReporterTestFactory.loadTestReportData();
        FailedReason failExplanation = ImmutableFailedReason.builder()
                .reportReason("reportReason")
                .reportExplanation("reportExplanation")
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
                .pharmacogeneticsGenotypes(createTestPharmacogeneticsGenotypes())
                .hlaAllelesReportingData(createTestHlaData())
                .purpleQC(Sets.newHashSet(purpleQCStatus))
                .reportDate(Formats.formatDate(LocalDate.now()))
                .isWGSReport(true)
                .build();

        String filename = testReportFilePath(patientReport);

        CFReportWriter writer = testCFReportWriter();
        writer.writeQCFailReport(patientReport, filename);
        writer.writeJsonFailedFile(patientReport, REPORT_BASE_DIR);
    }

    @NotNull
    private static ImmutableHlaReporting.Builder createHlaReporting() {
        return ImmutableHlaReporting.builder()
                .hlaAllele(ImmutableHlaAllele.builder().gene(Strings.EMPTY).germlineAllele(Strings.EMPTY).build())
                .germlineCopies(0)
                .tumorCopies(0)
                .somaticMutations(Strings.EMPTY)
                .interpretation(Strings.EMPTY);
    }

    @NotNull
    private static HlaAllelesReportingData createTestHlaData() {
        Map<String, List<HlaReporting>> alleles = Maps.newHashMap();

        alleles.put("HLA-A",
                Lists.newArrayList(createHlaReporting().hlaAllele(ImmutableHlaAllele.builder()
                        .gene("HLA-A")
                        .germlineAllele("A*01:01")
                        .build()).germlineCopies(2.0).tumorCopies(3.83).somaticMutations("None").interpretation("Yes").build()));
        alleles.put("HLA-B",
                Lists.newArrayList(createHlaReporting().hlaAllele(ImmutableHlaAllele.builder()
                                .gene("HLA-B")
                                .germlineAllele("B*40:02")
                                .build()).germlineCopies(1.0).tumorCopies(2.0).somaticMutations("None").interpretation("Yes").build(),
                        createHlaReporting().hlaAllele(ImmutableHlaAllele.builder().gene("HLA-B").germlineAllele("B*08:01").build())
                                .germlineCopies(1.0)
                                .tumorCopies(1.83)
                                .somaticMutations("None")
                                .interpretation("Yes")
                                .build()));
        alleles.put("HLA-C",
                Lists.newArrayList(createHlaReporting().hlaAllele(ImmutableHlaAllele.builder()
                                .gene("HLA-C")
                                .germlineAllele("C*07:01")
                                .build()).germlineCopies(1.0).tumorCopies(1.83).somaticMutations("None").interpretation("yes").build(),
                        createHlaReporting().hlaAllele(ImmutableHlaAllele.builder().gene("HLA-C").germlineAllele("C*03:04").build())
                                .germlineCopies(1.0)
                                .tumorCopies(2.0)
                                .somaticMutations("None")
                                .interpretation("Yes")
                                .build()));
        return ImmutableHlaAllelesReportingData.builder().hlaQC("PASS").hlaAllelesReporting(alleles).build();
    }

    @NotNull
    private static Map<String, List<PeachGenotype>> createTestPharmacogeneticsGenotypes() {
        Map<String, List<PeachGenotype>> pharmacogeneticsMap = Maps.newHashMap();
        pharmacogeneticsMap.put("DPYD",
                Lists.newArrayList(TestPeachFactory.builder()
                                .gene("DPYD")
                                .haplotype("*1_HOM")
                                .function("Normal Function")
                                .linkedDrugs("5-Fluorouracil;Capecitabine;Tegafur")
                                .urlPrescriptionInfo("https://www.pharmgkb.org/chemical/PA128406956/guidelineAnnotation/PA166104939"
                                        + "https://www.pharmgkb.org/chemical/PA448771/guidelineAnnotation/PA166104963"
                                        + "https://www.pharmgkb.org/chemical/PA452620/guidelineAnnotation/PA166104944")
                                .panelVersion("PGx_min_DPYD_v1.2")
                                .repoVersion("1.6")
                                .build(),
                        TestPeachFactory.builder()
                                .gene("DPYD")
                                .haplotype("*2_HOM")
                                .function("Normal Function")
                                .linkedDrugs("5-Fluorouracil;Capecitabine;Tegafur")
                                .urlPrescriptionInfo("https://www.pharmgkb.org/chemical/PA128406956/guidelineAnnotation/PA166104939"
                                        + "https://www.pharmgkb.org/chemical/PA448771/guidelineAnnotation/PA166104963"
                                        + "https://www.pharmgkb.org/chemical/PA452620/guidelineAnnotation/PA166104944")
                                .panelVersion("PGx_min_DPYD_v1.2")
                                .repoVersion("1.6")
                                .build()));
        return pharmacogeneticsMap;
    }

    @NotNull
    private static CFReportWriter testCFReportWriter() {
        return new CFReportWriter(WRITE_TO_PDF);
    }

    @NotNull
    private static String testReportFilePath(@NotNull PatientReport patientReport) {
        String fileName = OutputFileUtil.generateOutputFileNameForPdfReport(patientReport);
        String newFileName = fileName;
        if (TIMESTAMP_FILES) {
            int extensionStart = fileName.lastIndexOf('.');
            newFileName = fileName.substring(0, extensionStart) + "_" + System.currentTimeMillis() + fileName.substring(extensionStart);
        }
        return REPORT_BASE_DIR + File.separator + newFileName;
    }

    @NotNull
    private static String testReportFilePathPanel(@NotNull com.hartwig.oncoact.patientreporter.PanelReport patientReport) {
        String fileName = OutputFileUtil.generateOutputFileNameForPdfPanelResultReport(patientReport);
        String newFileName = fileName;
        if (TIMESTAMP_FILES) {
            int extensionStart = fileName.lastIndexOf('.');
            newFileName = fileName.substring(0, extensionStart) + "_" + System.currentTimeMillis() + fileName.substring(extensionStart);
        }
        return REPORT_BASE_DIR + File.separator + newFileName;
    }
}
