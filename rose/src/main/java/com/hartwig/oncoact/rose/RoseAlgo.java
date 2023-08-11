package com.hartwig.oncoact.rose;

import java.io.IOException;
import java.util.List;

import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptFile;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptsModel;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.drivergene.DriverGeneFile;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.rose.actionability.ActionabilityEntry;
import com.hartwig.oncoact.rose.actionability.ActionabilityFileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class RoseAlgo {

    private static final Logger LOGGER = LogManager.getLogger(RoseAlgo.class);

    @NotNull
    private final List<ActionabilityEntry> actionabilityEntries;
    @NotNull
    private final List<DriverGene> driverGenes;
    @NotNull
    private final ClinicalTranscriptsModel clinicalTranscriptsModel;

    @NotNull
    public static RoseAlgo build(@NotNull String actionabilityDatabaseTsv, @NotNull String driverGeneTsv,
            @NotNull String clinicalTranscriptsTsv) throws IOException {
        List<ActionabilityEntry> actionabilityEntry = readActionabilityEntries(actionabilityDatabaseTsv);
        List<DriverGene> driverGenes = readDriverGenesFromFile(driverGeneTsv);
        ClinicalTranscriptsModel clinicalTranscriptsModel = ClinicalTranscriptFile.buildFromTsv(clinicalTranscriptsTsv);

        return new RoseAlgo(actionabilityEntry, driverGenes, clinicalTranscriptsModel);
    }

    private RoseAlgo(final @NotNull List<ActionabilityEntry> actionabilityEntries, final @NotNull List<DriverGene> driverGenes,
            final @NotNull ClinicalTranscriptsModel clinicalTranscriptsModel) {
        this.actionabilityEntries = actionabilityEntries;
        this.driverGenes = driverGenes;
        this.clinicalTranscriptsModel = clinicalTranscriptsModel;
    }

    @NotNull
    private static List<DriverGene> readDriverGenesFromFile(@NotNull String driverGeneTsv) throws IOException {
        LOGGER.info(" Reading driver genes from {}", driverGeneTsv);
        List<DriverGene> driverGenes = DriverGeneFile.read(driverGeneTsv);
        LOGGER.info("  Read {} driver gene entries", driverGenes.size());

        return driverGenes;
    }

    @NotNull
    private static List<ActionabilityEntry> readActionabilityEntries(@NotNull String actionabilityDatabaseTsv) throws IOException {
        LOGGER.info(" Reading actionability database from {}", actionabilityDatabaseTsv);
        List<ActionabilityEntry> entries = ActionabilityFileReader.read(actionabilityDatabaseTsv);
        LOGGER.info("  Read {} actionability entries", entries.size());

        return entries;
    }

    @NotNull
    public RoseData run(@NotNull RoseConfig config) throws IOException {
        LOGGER.info("Loading ORANGE file from {}", config.orangeJson());
        OrangeRecord orange = OrangeJson.read(config.orangeJson());

        return ImmutableRoseData.builder()
                .orange(orange)
                .actionabilityEntries(actionabilityEntries)
                .driverGenes(driverGenes)
                .clinicalTranscriptsModel(clinicalTranscriptsModel)
                .build();
    }
}