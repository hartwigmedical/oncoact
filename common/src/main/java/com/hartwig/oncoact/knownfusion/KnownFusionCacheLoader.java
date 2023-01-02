package com.hartwig.oncoact.knownfusion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.util.FileReader;

import org.jetbrains.annotations.NotNull;

public final class KnownFusionCacheLoader {

    private static final String FIELD_DELIMITER = ",";
    private static final String ITEM_DELIM = ";";

    private static final String FLD_TYPE = "Type";
    private static final String FLD_FIVE_GENE = "FiveGene";
    private static final String FLD_THREE_GENE = "ThreeGene";
    private static final String FLD_PUB_MED = "PubMedId";
    private static final String FLD_CANCER_TYPES = "CancerTypes";
    private static final String FLD_HIGH_IMPACT_PROM = "HighImpactPromiscuous";
    private static final String FLD_SPECIFIC_EXON_TRANS = "KnownExonTranscript";
    private static final String FLD_KNOWN_EXON_UP_RANGE = "KnownExonUpRange";
    private static final String FLD_KNOWN_EXON_DOWN_RANGE = "KnownExonDownRange";

    private KnownFusionCacheLoader() {
    }

    @NotNull
    public static KnownFusionCache load(@NotNull String knownFusionFile) throws IOException {
        List<String> lines = Files.readAllLines(new File(knownFusionFile).toPath());

        List<KnownFusionData> knownFusions = Lists.newArrayList();

        Map<String, Integer> fields = FileReader.createFields(lines.get(0), FIELD_DELIMITER);

        for (String line : lines.subList(1, lines.size())) {
            knownFusions.add(fromLine(line, fields));
        }

        return KnownFusionCache.fromKnownFusions(knownFusions);
    }

    @NotNull
    private static KnownFusionData fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableKnownFusionData.builder()
                .type(KnownFusionType.valueOf(values[fields.get(FLD_TYPE)]))
                .fiveGene(values[fields.get(FLD_FIVE_GENE)])
                .threeGene(values[fields.get(FLD_THREE_GENE)])
                .cancerTypes(values[fields.get(FLD_CANCER_TYPES)])
                .pubMedId(values[fields.get(FLD_PUB_MED)])
                .highImpactPromiscuous(values[fields.get(FLD_HIGH_IMPACT_PROM)].equalsIgnoreCase("TRUE"))
                .specificExonsTransName(values[fields.get(FLD_SPECIFIC_EXON_TRANS)])
                .fiveGeneExonRange(toExonRange(values[fields.get(FLD_KNOWN_EXON_UP_RANGE)]))
                .threeGeneExonRange(toExonRange(values[fields.get(FLD_KNOWN_EXON_DOWN_RANGE)]))
                .build();
    }

    @NotNull
    private static int[] toExonRange(@NotNull String exonValue) {
        if (exonValue.isEmpty()) {
            return new int[2];
        }

        String[] exons = exonValue.split(ITEM_DELIM);

        int[] converted = new int[2];
        converted[0] =Integer.parseInt(exons[0]);
        converted[1] =Integer.parseInt(exons[1]);
        return converted;
    }
}
