package com.hartwig.oncoact.patientreporter.reportingdb;

import static java.lang.String.join;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.patientreporter.ExampleAnalysisConfig;
import com.hartwig.oncoact.patientreporter.ExampleAnalysisTestFactory;

import org.junit.Test;

public class ReportingDbTest {

    @Test
    public void writeApiUpdateJson() throws IOException {
        File reportingDbTsv = File.createTempFile("reporting-db-test", ".tsv");

        BufferedWriter writer = new BufferedWriter(new FileWriter(reportingDbTsv, true));
        writer.write("tumorBarcode\tsampleId\tcohort\treportDate\treportType\tpurity\thasReliableQuality\thasReliablePurity\n");
        writer.close();

        ExampleAnalysisConfig config = new ExampleAnalysisConfig.Builder().sampleId("CPCT01_SUCCESS").build();

        ReportingDb reportingDb = new ReportingDb();

        File expectedOutput = new File("/tmp/reportingId_tumorIsolationBarcode_wgs_analysis_api-update.json");
        Files.deleteIfExists(expectedOutput.toPath());
        assertFalse(expectedOutput.exists());
        reportingDb.appendAnalysedReport(ExampleAnalysisTestFactory.createAnalysisWithAllTablesFilledIn(config, PurpleQCStatus.PASS),
                "/tmp");
        assertTrue(expectedOutput.exists());

        Map<String, Object> output = new GsonBuilder().serializeNulls()
                .serializeSpecialFloatingPointValues()
                .create()
                .fromJson(join("\n", Files.readAllLines(expectedOutput.toPath())), new TypeToken<Map<String, Object>>() {
                }.getType());
        assertEquals(output.get("has_reliable_quality"), true);
        assertEquals(output.get("has_reliable_purity"), true);
        assertEquals(output.get("purity"), 1.0);
        assertEquals(output.get("cohort"), "cohort");
        assertEquals(output.get("report_type"), "wgs_analysis");
        assertEquals(output.get("barcode"), "tumorIsolationBarcode");
        assertEquals(output.get("report_date"), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-y")));
    }
}