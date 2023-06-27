package com.hartwig.oncoact.patientreporter.qcfail;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FailedDBFileTest {

    private static final String FAIL_DATABASE_TSV = Resources.getResource("qcfail/failDB.tsv").getPath();

    @Test
    public void canReadFailedDatabaseTsv() throws IOException {
        Map<String, FailedDatabase> failedDatabaseMap = FailedDBFile.buildFromTsv(FAIL_DATABASE_TSV);
        assertEquals(1, failedDatabaseMap.size());
    }
}