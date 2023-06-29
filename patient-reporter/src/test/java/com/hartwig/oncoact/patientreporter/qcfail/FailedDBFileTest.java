package com.hartwig.oncoact.patientreporter.qcfail;

import com.google.common.io.Resources;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedDBFile;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedDatabase;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FailedDBFileTest {

    private static final String FAIL_DATABASE_TSV = Resources.getResource("qcfail/fail_reasons.tsv").getPath();

    @Test
    public void canReadFailedDatabaseTsv() throws IOException {
        Map<String, FailedDatabase> failedDatabaseMap = FailedDBFile.buildFromTsv(FAIL_DATABASE_TSV);
        assertEquals(6, failedDatabaseMap.size());

        FailedDatabase technical_failure = failedDatabaseMap.get("technical_failure");
        assertEquals("Technical failure", technical_failure.reportReason());
        assertEquals("Whole Genome Sequencing could not be successfully performed on the received biomaterial(s) due to technical problems.", technical_failure.reportExplanation());
        assertEquals(Strings.EMPTY, technical_failure.reportExplanationDetail());

        FailedDatabase sufficient_tcp_qc_failure = failedDatabaseMap.get("sufficient_tcp_qc_failure");
        assertEquals("Insufficient quality of received biomaterial(s)", sufficient_tcp_qc_failure.reportReason());
        assertEquals("The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", sufficient_tcp_qc_failure.reportExplanation());
        assertEquals("The tumor percentage based on molecular estimation was above the minimal of 20% tumor cells but could not be further analyzed due to insufficient quality.", sufficient_tcp_qc_failure.reportExplanationDetail());

        FailedDatabase insufficient_tcp_shallow_wgs = failedDatabaseMap.get("insufficient_tcp_shallow_wgs");
        assertEquals("Insufficient quality of received biomaterial(s)", insufficient_tcp_shallow_wgs.reportReason());
        assertEquals("The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", insufficient_tcp_shallow_wgs.reportExplanation());
        assertEquals("The tumor percentage based on molecular estimation was below the minimal of 20% tumor cells and could not be further analyzed.", insufficient_tcp_shallow_wgs.reportExplanationDetail());

        FailedDatabase insufficient_tcp_deep_wgs = failedDatabaseMap.get("insufficient_tcp_deep_wgs");
        assertEquals("Insufficient quality of received biomaterial(s)", insufficient_tcp_deep_wgs.reportReason());
        assertEquals("The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", insufficient_tcp_deep_wgs.reportExplanation());
        assertEquals("The tumor percentage based on molecular estimation was below the minimal of 20% tumor cells and could not be further analyzed.", insufficient_tcp_deep_wgs.reportExplanationDetail());

        FailedDatabase insufficient_dna = failedDatabaseMap.get("insufficient_dna");
        assertEquals("Insufficient quality of received biomaterial(s)", insufficient_dna.reportReason());
        assertEquals("The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", insufficient_dna.reportExplanation());
        assertEquals("Sequencing could not be performed due to insufficient DNA.", insufficient_dna.reportExplanationDetail());

        FailedDatabase insufficient_dna_panel = failedDatabaseMap.get("insufficient_dna_panel");
        assertEquals("Insufficient quality of received biomaterial(s)", insufficient_dna_panel.reportReason());
        assertEquals("The received biomaterial(s) did not meet the requirements that are needed for high quality Next Generation Sequencing.", insufficient_dna_panel.reportExplanation());
        assertEquals("Sequencing could not be performed due to insufficient DNA.", insufficient_dna_panel.reportExplanationDetail());
    }
}