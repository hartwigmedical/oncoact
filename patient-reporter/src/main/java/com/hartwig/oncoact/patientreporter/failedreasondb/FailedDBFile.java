package com.hartwig.oncoact.patientreporter.failedreasondb;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public final class FailedDBFile {


    private static final String SEPARATOR = "\t";


    private FailedDBFile() {
    }

    @NotNull
    public static Map<String, FailedReason> buildFromTsv(@NotNull String failedDbTsv) throws IOException {
        List<String> failedDbLines = Files.readAllLines(new File(failedDbTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(failedDbLines.get(0), SEPARATOR);

        Map<String, FailedReason> failedDatabase = Maps.newHashMap();
        for (String line : failedDbLines.subList(1, failedDbLines.size())) {
            String[] values = line.split(SEPARATOR);

            String reasonKey = values[fields.get("key")];
            failedDatabase.put(reasonKey, ImmutableFailedReason.builder()
                    .reasonKey(reasonKey)
                    .reportReason(values[fields.get("reason")])
                    .reportExplanation(values[fields.get("explanation")])
                    .reportExplanationDetail(values.length == 4 ? values[fields.get("explanationDetail")] : Strings.EMPTY)
                    .build());
        }
        return failedDatabase;
    }
}