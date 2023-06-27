package com.hartwig.oncoact.patientreporter.qcfail;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class FailedDBFile {


    private static final String SEPARATOR = "\t";


    private FailedDBFile() {
    }

    @NotNull
    public static Map<String, FailedDatabase> buildFromTsv(@NotNull String failedDbTsv) throws IOException {
        List<String> failedDbLines = Files.readAllLines(new File(failedDbTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(failedDbLines.get(0), SEPARATOR);

        Map<String, FailedDatabase> failedDatabases = Maps.newHashMap();
        for (String line : failedDbLines.subList(1, failedDbLines.size())) {
            String[] values = line.split(SEPARATOR);

            String reasonKey = values[fields.get("reason_key")];
            failedDatabases.put(reasonKey, ImmutableFailedDatabase.builder()
                    .reasonKey(reasonKey)
                    .reportReason(values[fields.get("report_explanation")])
                    .reportExplanation(values[fields.get("report_explanation")])
                    .reportExplanationDetail(values[fields.get("report_explanation")])
                    .build());
        }
        return failedDatabases;
    }
}