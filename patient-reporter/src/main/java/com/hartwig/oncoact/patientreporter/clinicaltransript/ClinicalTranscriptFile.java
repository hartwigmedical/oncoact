package com.hartwig.oncoact.patientreporter.clinicaltransript;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

        // Skip header
        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.split(SEPARATOR);

            if (parts.length == 2) {
                String geneName = parts[0];
                String clinicalTranscript = parts[1];
                clinicalTranscriptEntries.put(geneName, clinicalTranscript);
            } else {
                LOGGER.warn("Suspicious line detected in clinical transcripten tsv: {}", line);
            }
        }

        return new ClinicalTranscriptsModel(clinicalTranscriptEntries);
    }
}
