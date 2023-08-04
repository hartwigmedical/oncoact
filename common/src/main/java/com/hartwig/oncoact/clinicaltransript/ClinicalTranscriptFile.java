package com.hartwig.oncoact.clinicaltransript;

import com.google.common.collect.Maps;
import com.hartwig.oncoact.util.CsvFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClinicalTranscriptFile {

    private ClinicalTranscriptFile(){
    }

    private static final Logger LOGGER = LogManager.getLogger(ClinicalTranscriptFile.class);

    private static final String SEPARATOR = "\t";

    @NotNull
    public static ClinicalTranscriptsModel buildFromTsv(@NotNull String clinicalTranscriptTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(clinicalTranscriptTsv).toPath());

        Map<String, String> clinicalTranscriptEntries = Maps.newHashMap();
        final Map<String,Integer> fieldIndexMap = CsvFileReader.getHeadersToDelimiter(lines.get(0), SEPARATOR);

        // Skip header
        for (String line : lines.subList(1, lines.size())) {
            final String[] parts = line.split(SEPARATOR, -1);

            if (parts.length == 2) {
                if (fieldIndexMap.containsKey("Gene") || fieldIndexMap.containsKey("Transcript")) {
                    String geneName = parts[fieldIndexMap.get("Gene")];
                    String clinicalTranscript = parts[fieldIndexMap.get("Transcript")];
                    clinicalTranscriptEntries.put(geneName, clinicalTranscript);
                } else {
                    LOGGER.warn("Wrong column names are present in clinical transcripts tsv: {}", line);
                }
            } else {
                LOGGER.warn("Suspicious line detected in clinical transcripts tsv: {}", line);
            }
        }

        return new ClinicalTranscriptsModel(clinicalTranscriptEntries);
    }
}