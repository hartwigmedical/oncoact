package com.hartwig.oncoact.clinicaltransript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.oncoact.util.CsvFileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ClinicalTranscriptFile {

    private ClinicalTranscriptFile() {
    }

    private static final Logger LOGGER = LogManager.getLogger(ClinicalTranscriptFile.class);

    private static final String SEPARATOR = "\t";

    @NotNull
    public static ClinicalTranscriptsModel buildFromTsv(@NotNull String clinicalTranscriptTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(clinicalTranscriptTsv).toPath());

        Map<String, String> clinicalTranscriptEntries = Maps.newHashMap();
        String columnNameGene = "Gene";
        String columnNameTranscript = "Transcript";

        final Map<String, Integer> fieldIndexMap = CsvFileReader.getHeadersToDelimiter(lines.get(0), SEPARATOR);
        if (!fieldIndexMap.containsKey(columnNameGene) || !fieldIndexMap.containsKey(columnNameTranscript)) {
            throw new IllegalArgumentException(
                    "Wrong column names " + fieldIndexMap.keySet() + " are present in clinical transcripts tsv!");
        }

        // Skip header
        for (String line : lines.subList(1, lines.size())) {
            final String[] parts = line.split(SEPARATOR, -1);

            if (parts.length == 2) {
                String geneName = parts[fieldIndexMap.get(columnNameGene)];
                String clinicalTranscript = parts[fieldIndexMap.get(columnNameTranscript)];
                clinicalTranscriptEntries.put(geneName, clinicalTranscript);
            } else {
                LOGGER.warn("Suspicious line detected in clinical transcripts tsv: {}", line);
            }
        }

        return new ClinicalTranscriptsModel(clinicalTranscriptEntries);
    }
}