package com.hartwig.oncoact.rose;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.hartwig.oncoact.common.chord.ChordData;
import com.hartwig.oncoact.common.chord.ChordDataFile;
import com.hartwig.oncoact.common.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.common.clinical.PatientPrimaryTumorFile;
import com.hartwig.oncoact.common.cuppa.CuppaDataFile;
import com.hartwig.oncoact.common.cuppa.interpretation.CuppaPrediction;
import com.hartwig.oncoact.common.cuppa.interpretation.CuppaPredictionFactory;
import com.hartwig.oncoact.common.drivercatalog.panel.DriverGene;
import com.hartwig.oncoact.common.drivercatalog.panel.DriverGeneFile;
import com.hartwig.oncoact.common.genome.refgenome.RefGenomeVersion;
import com.hartwig.oncoact.common.linx.LinxData;
import com.hartwig.oncoact.common.linx.LinxDataLoader;
import com.hartwig.oncoact.common.purple.loader.PurpleData;
import com.hartwig.oncoact.common.purple.loader.PurpleDataLoader;
import com.hartwig.oncoact.common.virus.VirusInterpreterData;
import com.hartwig.oncoact.common.virus.VirusInterpreterDataLoader;
import com.hartwig.oncoact.rose.actionability.ActionabilityEntry;
import com.hartwig.oncoact.rose.actionability.ActionabilityFileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class RoseAlgo {

    private static final Logger LOGGER = LogManager.getLogger(RoseAlgo.class);

    @NotNull
    private final List<ActionabilityEntry> actionabilityEntry;
    @NotNull
    private final List<DriverGene> driverGenes;

    @NotNull
    public static RoseAlgo build(@NotNull String actionabilityDatabaseTsv, @NotNull String driverGeneTsv,
            @NotNull RefGenomeVersion refGenomeVersion) throws IOException {
        LOGGER.info("ROSE is running on ref genome version: {}", refGenomeVersion);
        List<ActionabilityEntry> actionabilityEntry = ActionabilityFileReader.read(actionabilityDatabaseTsv);
        List<DriverGene> driverGenes = readDriverGenesFromFile(driverGeneTsv);
        return new RoseAlgo(actionabilityEntry, driverGenes);
    }

    private RoseAlgo(final @NotNull List<ActionabilityEntry> actionabilityEntry, final @NotNull List<DriverGene> driverGenes) {
        this.actionabilityEntry = actionabilityEntry;
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
    private static List<PatientPrimaryTumor> readPatientPrimaryTumors(@NotNull String primaryTumorTsv) throws IOException {
        List<PatientPrimaryTumor> patientPrimaryTumors = PatientPrimaryTumorFile.read(primaryTumorTsv);
        LOGGER.info("Loaded primary tumors for {} patients from {}", patientPrimaryTumors.size(), primaryTumorTsv);
        return patientPrimaryTumors;
    }

    @NotNull
    public RoseData run(@NotNull RoseConfig config) throws IOException {
        PurpleData purple = loadPurpleData(config);

        return ImmutableRoseData.builder()
                .sampleId(config.tumorSampleId())
                .patientId(config.patientId())
                .purple(purple)
                .linx(loadLinxData(config))
                .virusInterpreter(loadVirusInterpreterData(config))
                .chord(loadChordAnalysis(config))
                .cuppaPrediction(loadCuppaData(config))
                .actionabilityEntries(actionabilityEntry)
                .driverGenes(driverGenes)
                .build();
    }

    @NotNull
    private static PurpleData loadPurpleData(@NotNull RoseConfig config) throws IOException {
        return PurpleDataLoader.load(config.tumorSampleId(),
                config.refSampleId(),
                null,
                config.purpleQcFile(),
                config.purplePurityTsv(),
                config.purpleSomaticDriverCatalogTsv(),
                config.purpleSomaticVariantVcf(),
                config.purpleGermlineDriverCatalogTsv(),
                config.purpleGermlineVariantVcf(),
                config.purpleSomaticCopyNumberTsv(),
                null,
                null,
                null);
    }

    @NotNull
    private static CuppaPrediction loadCuppaData(@NotNull RoseConfig config) throws IOException {
        LOGGER.info("Loading CUPPA from {}", new File(config.cuppaResultCsv()).getParent());
        List<CuppaDataFile> cuppaEntries = CuppaDataFile.read(config.cuppaResultCsv());
        LOGGER.info(" Loaded {} entries from {}", cuppaEntries.size(), config.cuppaResultCsv());

        List<CuppaPrediction> predictions = CuppaPredictionFactory.create(cuppaEntries);
        CuppaPrediction best = predictions.get(0);
        LOGGER.info(" Predicted cancer type '{}' with likelihood {}", best.cancerType(), best.likelihood());
        return best;
    }

    @NotNull
    private static LinxData loadLinxData(@NotNull RoseConfig config) throws IOException {
        return LinxDataLoader.load(config.linxFusionTsv(), config.linxBreakendTsv(), config.linxDriverCatalogTsv());
    }

    @NotNull
    private static VirusInterpreterData loadVirusInterpreterData(@NotNull RoseConfig config) throws IOException {
        return VirusInterpreterDataLoader.load(config.annotatedVirusTsv());
    }

    @NotNull
    private static ChordData loadChordAnalysis(@NotNull RoseConfig config) throws IOException {
        return ChordDataFile.read(config.chordPredictionTxt(), true);
    }
}