package com.hartwig.oncoact.patientreporter;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.hartwig.oncoact.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.knownfusion.KnownFusionCache;
import com.hartwig.oncoact.knownfusion.KnownFusionCacheLoader;
import com.hartwig.oncoact.lims.Lims;
import com.hartwig.oncoact.lims.LimsFactory;
import com.hartwig.oncoact.patientreporter.algo.AnalysedReportData;
import com.hartwig.oncoact.patientreporter.algo.ImmutableAnalysedReportData;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingFile;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingModel;
import com.hartwig.oncoact.patientreporter.qcfail.ImmutableQCFailReportData;
import com.hartwig.oncoact.patientreporter.remarks.SpecialRemarkFile;
import com.hartwig.oncoact.patientreporter.remarks.SpecialRemarkModel;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class PatientReporterTestFactory {

    private static final String RUN_DIRECTORY = Resources.getResource("test_run").getPath();
    private static final String PIPELINE_VERSION_FILE = RUN_DIRECTORY + "/pipeline.version";
    private static final String ORANGE_JSON = RUN_DIRECTORY + "/orange/sample.orange.json";
    private static final String CUPPA_PLOT = RUN_DIRECTORY + "/cuppa/sample.cuppa.chart.png";
    private static final String PROTECT_EVIDENCE_TSV = RUN_DIRECTORY + "/protect/sample.protect.tsv";
    private static final String ROSE_TSV = RUN_DIRECTORY + "/rose/sample.rose.tsv";
    private static final String SIGNATURE_PATH = Resources.getResource("signature/signature_test.png").getPath();
    private static final String RVA_LOGO_PATH = Resources.getResource("rva_logo/rva_logo_test.jpg").getPath();
    private static final String COMPANY_LOGO_PATH = Resources.getResource("company_logo/hartwig_logo_test.jpg").getPath();

    private static final String GERMLINE_REPORTING_TSV = Resources.getResource("germline_reporting/germline_reporting.tsv").getPath();
    private static final String SAMPLE_SPECIAL_REMARK_TSV = Resources.getResource("special_remark/sample_special_remark.tsv").getPath();
    private static final String KNOWN_FUSION_FILE = Resources.getResource("known_fusion_data/known_fusion_file.csv").getPath();

    private static final String UDI_DI = "(01)8720299486027(8012)v5.28";

    private PatientReporterTestFactory() {
    }

    @NotNull
    public static PatientReporterConfig createTestReporterConfig() {
        return ImmutablePatientReporterConfig.builder()
                .tumorSampleId(Strings.EMPTY)
                .tumorSampleBarcode(Strings.EMPTY)
                .outputDirReport(Strings.EMPTY)
                .outputDirData(Strings.EMPTY)
                .knownFusionFile(KNOWN_FUSION_FILE)
                .primaryTumorTsv(Strings.EMPTY)
                .limsDir(Strings.EMPTY)
                .rvaLogo(RVA_LOGO_PATH)
                .companyLogo(COMPANY_LOGO_PATH)
                .signature(SIGNATURE_PATH)
                .udiDi(UDI_DI)
                .qcFail(false)
                .orangeJson(ORANGE_JSON)
                .cuppaPlot(CUPPA_PLOT)
                .protectEvidenceTsv(PROTECT_EVIDENCE_TSV)
                .addRose(true)
                .roseTsv(ROSE_TSV)
                .germlineReportingTsv(Strings.EMPTY)
                .sampleSpecialRemarkTsv(SAMPLE_SPECIAL_REMARK_TSV)
                .isCorrectedReport(false)
                .isCorrectedReportExtern(false)
                .onlyCreatePDF(false)
                .requirePipelineVersionFile(true)
                .pipelineVersionFile(PIPELINE_VERSION_FILE)
                .expectedPipelineVersion("5.28")
                .overridePipelineVersion(false)
                .allowDefaultCohortConfig(false)
                .build();
    }

    @NotNull
    public static ReportData loadTestReportData() {
        List<PatientPrimaryTumor> patientPrimaryTumors = Lists.newArrayList();
        Lims lims = LimsFactory.empty();

        return ImmutableQCFailReportData.builder()
                .patientPrimaryTumors(patientPrimaryTumors)
                .limsModel(lims)
                .signaturePath(SIGNATURE_PATH)
                .logoRVAPath(RVA_LOGO_PATH)
                .logoCompanyPath(COMPANY_LOGO_PATH)
                .udiDi(UDI_DI)
                .build();
    }

    @NotNull
    public static AnalysedReportData loadTestAnalysedReportData() {
        try {
            GermlineReportingModel germlineReportingModel = GermlineReportingFile.buildFromTsv(GERMLINE_REPORTING_TSV);
            SpecialRemarkModel specialRemarkModel = SpecialRemarkFile.buildFromTsv(SAMPLE_SPECIAL_REMARK_TSV);
            KnownFusionCache knownFusionCache = KnownFusionCacheLoader.load(KNOWN_FUSION_FILE);

            return ImmutableAnalysedReportData.builder()
                    .from(loadTestReportData())
                    .germlineReportingModel(germlineReportingModel)
                    .specialRemarkModel(specialRemarkModel)
                    .knownFusionCache(knownFusionCache)
                    .build();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load test analysed report data: " + exception.getMessage());
        }
    }
}
