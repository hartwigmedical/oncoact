package com.hartwig.oncoact.protect;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.orange.ExperimentType;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptFile;
import com.hartwig.oncoact.doid.DiseaseOntology;
import com.hartwig.oncoact.doid.DoidParents;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.drivergene.DriverGeneFile;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.parser.CliAndPropertyParser;
import com.hartwig.oncoact.protect.algo.ProtectAlgo;
import com.hartwig.oncoact.protect.serve.ServeOutput;
import com.hartwig.serve.datamodel.ActionableEvents;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ProtectApplication {

    private static final Logger LOGGER = LogManager.getLogger(ProtectApplication.class);
    private static final String VERSION = ProtectApplication.class.getPackage().getImplementationVersion();

    public static void main(@NotNull String[] args) throws IOException {
        LOGGER.info("Running PROTECT v{}", VERSION);

        Options options = ProtectConfig.createOptions();

        ProtectConfig config = null;
        try {
            config = ProtectConfig.createConfig(new CliAndPropertyParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("PROTECT", options);
            System.exit(1);
        }

        LOGGER.info("Protect config is: {}", config);
        new ProtectApplication(config).run();

        LOGGER.info("Complete");
    }

    @NotNull
    private final ProtectConfig config;

    public ProtectApplication(@NotNull final ProtectConfig config) {
        this.config = config;
    }

    public void run() throws IOException {
        LOGGER.info("Loading ORANGE file from {}", config.orangeJson());
        OrangeRecord orange = OrangeJson.read(config.orangeJson());

        LOGGER.info("Loading DOID file from {}", config.doidJsonFile());
        DoidParents doidParentModel = DoidParents.fromEdges(DiseaseOntology.readDoidOwlEntryFromDoidJson(config.doidJsonFile()).edges());

        Set<String> patientTumorDoids = patientTumorDoids(config, doidParentModel);
        ActionableEvents actionableEvents = ServeOutput.loadServeData(config, orange.refGenomeVersion());

        LOGGER.info(" Reading driver genes from {}", config.driverGeneTsv());
        List<DriverGene> driverGenes = DriverGeneFile.read(config.driverGeneTsv());
        LOGGER.info("  Read {} driver gene entries", driverGenes.size());

        ProtectAlgo algo = ProtectAlgo.build(actionableEvents,
                patientTumorDoids,
                driverGenes,
                doidParentModel,
                ClinicalTranscriptFile.buildFromTsv(config.clinicalTranscriptsTsv()));

        ExperimentType experimentType = ExperimentType.valueOf(config.experimentType());
        List<ProtectEvidence> evidences = algo.run(orange, experimentType);

        String filename = config.outputDir() + File.separator + "protect.tsv";
        LOGGER.info("Writing {} evidence items to file: {}", evidences.size(), filename);
        ProtectEvidenceFile.write(filename, evidences);
    }

    @NotNull
    private static Set<String> patientTumorDoids(@NotNull ProtectConfig config, @NotNull DoidParents doidParentModel) {
        Set<String> result = Sets.newHashSet();

        Set<String> initialDoids = config.primaryTumorDoids();
        if (initialDoids.isEmpty()) {
            LOGGER.warn("No doids provided. Every treatment will be considered off-label.");
            return Sets.newHashSet();
        }

        LOGGER.info(" Starting doid resolving for patient with initial tumor doids '{}'", initialDoids);
        for (String initialDoid : initialDoids) {
            result.add(initialDoid);
            result.addAll(doidParentModel.parents(initialDoid));
        }

        LOGGER.info(" {} doids which are considered on-label for patient: '{}'", result.size(), result);
        return result;
    }
}