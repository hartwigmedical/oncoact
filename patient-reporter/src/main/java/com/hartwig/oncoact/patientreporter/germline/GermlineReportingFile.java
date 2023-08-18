package com.hartwig.oncoact.patientreporter.germline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.util.CsvFileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class GermlineReportingFile {
    private static final Logger LOGGER = LogManager.getLogger(GermlineReportingFile.class);

    private static final String SEPARATOR = "\t";

    private GermlineReportingFile() {
    }

    @NotNull
    public static GermlineReportingModel buildFromTsv(@NotNull String germlineReportingTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(germlineReportingTsv).toPath());

        List<GermlineReportingEntry> germlineReportingEntries = Lists.newArrayList();
        String columnNameGene = "Gene";
        String columnNameNotify = "Notify Condition";
        String columnNameVariant = "Notify Condition Filter";

        final Map<String, Integer> fieldIndexMap = CsvFileReader.getHeadersToDelimiter(lines.get(0), SEPARATOR);
        if (!fieldIndexMap.containsKey(columnNameGene) || !fieldIndexMap.containsKey(columnNameNotify) || !fieldIndexMap.containsKey(
                columnNameVariant)) {
            throw new IllegalArgumentException("Wrong column names " + fieldIndexMap.keySet() + " are present in germline reporting tsv!");
        }

        // Skip header
        for (String line : lines.subList(1, lines.size())) {
            final String[] parts = line.split(SEPARATOR, -1);

            if (parts.length == 2) {
                String geneName = parts[fieldIndexMap.get(columnNameGene)];
                GermlineCondition clinicalTranscript = GermlineCondition.toGermlineCondition(parts[fieldIndexMap.get(columnNameNotify)]);

                germlineReportingEntries.add(ImmutableGermlineReportingEntry.builder()
                        .gene(geneName)
                        .condition(clinicalTranscript)
                        .conditionFilter(null)
                        .build());

            } else if (parts.length == 3) {
                String geneName = parts[fieldIndexMap.get(columnNameGene)];
                GermlineCondition clinicalTranscript = GermlineCondition.toGermlineCondition(parts[fieldIndexMap.get(columnNameNotify)]);
                String variant = parts[fieldIndexMap.get(columnNameVariant)];

                germlineReportingEntries.add(ImmutableGermlineReportingEntry.builder()
                        .gene(geneName)
                        .condition(clinicalTranscript)
                        .conditionFilter(variant)
                        .build());
            } else {
                LOGGER.warn("Suspicious line detected in germline reporting tsv: {}", line);
            }
        }

        return new GermlineReportingModel(germlineReportingEntries);
    }
}