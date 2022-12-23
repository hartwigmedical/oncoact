package com.hartwig.oncoact.rose;

import java.io.IOException;
import java.util.List;

import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.drivergene.DriverGeneFile;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.orange.OrangeRecord;
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
    public static RoseAlgo build(@NotNull String actionabilityDatabaseTsv, @NotNull String driverGeneTsv) throws IOException {
        List<ActionabilityEntry> actionabilityEntry = ActionabilityFileReader.read(actionabilityDatabaseTsv);
        List<DriverGene> driverGenes = readDriverGenesFromFile(driverGeneTsv);

        return new RoseAlgo(actionabilityEntry, driverGenes);
    }

    private RoseAlgo(final @NotNull List<ActionabilityEntry> actionabilityEntries, final @NotNull List<DriverGene> driverGenes) {
        this.actionabilityEntries = actionabilityEntries;
        this.driverGenes = driverGenes;
    }

    @NotNull
    private static List<DriverGene> readDriverGenesFromFile(@NotNull String driverGeneTsv) throws IOException {
        LOGGER.info(" Reading driver genes from {}", driverGeneTsv);
        List<DriverGene> driverGenes = DriverGeneFile.read(driverGeneTsv);
        LOGGER.info("  Read {} driver gene entries", driverGenes.size());

        return driverGenes;
    }

    @NotNull
    public RoseData run(@NotNull RoseConfig config) throws IOException {
        OrangeRecord orange = OrangeJson.read(config.orangeJson());

        return ImmutableRoseData.builder()
                .patientId(config.patientId())
                .orange(orange)
                .actionabilityEntries(actionabilityEntries)
                .driverGenes(driverGenes)
                .build();
    }
}