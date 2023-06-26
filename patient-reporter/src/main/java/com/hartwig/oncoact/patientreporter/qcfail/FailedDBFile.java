package com.hartwig.oncoact.patientreporter.qcfail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.oncoact.patientreporter.remarks.SpecialRemarkFile;
import com.hartwig.oncoact.patientreporter.remarks.SpecialRemarkModel;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class FailedDBFile {

    private static final Logger LOGGER = LogManager.getLogger(FailedDBFile.class);

    private static final String SEPARATOR = "\t";

    private static final String LINE_BREAK_STRING = " <enter> ";

    private FailedDBFile() {
    }

    @NotNull
    public static List<FailedDatabase> buildFromTsv(@NotNull String failedDbTsv) throws IOException {
        List<String> failedDbLines = Files.readAllLines(new File(failedDbTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(failedDbLines.get(0), SEPARATOR);

        List<FailedDatabase> failedDatabases = Lists.newArrayList();
        for (String line : failedDbLines.subList(1, failedDbLines.size())) {
            failedDatabases.add(fromLine(line, fields));
        }

        return failedDatabases;
    }

    @NotNull
    private static FailedDatabase fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(SEPARATOR);

        return ImmutableFailedDatabase.builder()
                .reasonKey(values[fields.get("reason_key")])
                .explanation(values[fields.get("report_explanation")])
                .build();
    }
}
