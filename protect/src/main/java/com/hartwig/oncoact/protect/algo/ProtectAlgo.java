package com.hartwig.oncoact.protect.algo;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptsModel;
import com.hartwig.oncoact.doid.DoidParents;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.evidence.ChordEvidence;
import com.hartwig.oncoact.protect.evidence.CopyNumberEvidence;
import com.hartwig.oncoact.protect.evidence.DisruptionEvidence;
import com.hartwig.oncoact.protect.evidence.FusionEvidence;
import com.hartwig.oncoact.protect.evidence.HlaEvidence;
import com.hartwig.oncoact.protect.evidence.PersonalizedEvidenceFactory;
import com.hartwig.oncoact.protect.evidence.PurpleSignatureEvidence;
import com.hartwig.oncoact.protect.evidence.VariantEvidence;
import com.hartwig.oncoact.protect.evidence.VirusEvidence;
import com.hartwig.oncoact.protect.evidence.WildTypeEvidence;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantFactory;
import com.hartwig.serve.datamodel.ActionableEvents;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProtectAlgo {

    private static final Logger LOGGER = LogManager.getLogger(ProtectAlgo.class);

    @NotNull
    private final VariantEvidence variantEvidenceFactory;
    @NotNull
    private final CopyNumberEvidence copyNumberEvidenceFactory;
    @NotNull
    private final DisruptionEvidence disruptionEvidenceFactory;
    @NotNull
    private final FusionEvidence fusionEvidenceFactory;
    @NotNull
    private final PurpleSignatureEvidence purpleSignatureEvidenceFactory;
    @NotNull
    private final VirusEvidence virusEvidenceFactory;
    @NotNull
    private final ChordEvidence chordEvidenceFactory;
    @NotNull
    private final HlaEvidence hlaEvidenceFactory;
    @NotNull
    private final WildTypeEvidence wildTypeEvidenceFactory;
    @NotNull
    private final ClinicalTranscriptsModel clinicalTranscriptsModel;

    @NotNull
    public static ProtectAlgo build(@NotNull ActionableEvents actionableEvents, @NotNull Set<String> patientTumorDoids,
            @NotNull List<DriverGene> driverGenes, @NotNull DoidParents doidParentModel,
            @NotNull ClinicalTranscriptsModel clinicalTranscriptsModel) {
        PersonalizedEvidenceFactory personalizedEvidenceFactory = new PersonalizedEvidenceFactory(patientTumorDoids, doidParentModel);

        VariantEvidence variantEvidenceFactory = new VariantEvidence(personalizedEvidenceFactory,
                actionableEvents.hotspots(),
                actionableEvents.codons(),
                actionableEvents.exons(),
                actionableEvents.genes());
        CopyNumberEvidence copyNumberEvidenceFactory = new CopyNumberEvidence(personalizedEvidenceFactory, actionableEvents.genes());
        DisruptionEvidence disruptionEvidenceFactory = new DisruptionEvidence(personalizedEvidenceFactory, actionableEvents.genes());
        FusionEvidence fusionEvidenceFactory =
                new FusionEvidence(personalizedEvidenceFactory, actionableEvents.genes(), actionableEvents.fusions());
        PurpleSignatureEvidence purpleSignatureEvidenceFactory =
                new PurpleSignatureEvidence(personalizedEvidenceFactory, actionableEvents.characteristics());
        VirusEvidence virusEvidenceFactory = new VirusEvidence(personalizedEvidenceFactory, actionableEvents.characteristics());
        ChordEvidence chordEvidenceFactory = new ChordEvidence(personalizedEvidenceFactory, actionableEvents.characteristics());
        HlaEvidence hlaEvidenceFactory = new HlaEvidence(personalizedEvidenceFactory, actionableEvents.hla());
        WildTypeEvidence wildTypeEvidenceFactory = new WildTypeEvidence(personalizedEvidenceFactory, actionableEvents.genes(), driverGenes);

        return new ProtectAlgo(variantEvidenceFactory,
                copyNumberEvidenceFactory,
                disruptionEvidenceFactory,
                fusionEvidenceFactory,
                purpleSignatureEvidenceFactory,
                virusEvidenceFactory,
                chordEvidenceFactory,
                hlaEvidenceFactory,
                wildTypeEvidenceFactory,
                clinicalTranscriptsModel);
    }

    private ProtectAlgo(@NotNull final VariantEvidence variantEvidenceFactory, @NotNull final CopyNumberEvidence copyNumberEvidenceFactory,
            @NotNull final DisruptionEvidence disruptionEvidenceFactory, @NotNull final FusionEvidence fusionEvidenceFactory,
            @NotNull final PurpleSignatureEvidence purpleSignatureEvidenceFactory, @NotNull final VirusEvidence virusEvidenceFactory,
            @NotNull final ChordEvidence chordEvidenceFactory, @NotNull final HlaEvidence hlaEvidenceFactory,
            @NotNull final WildTypeEvidence wildTypeEvidenceFactory, @NotNull final ClinicalTranscriptsModel clinicalTranscriptsModel) {
        this.variantEvidenceFactory = variantEvidenceFactory;
        this.copyNumberEvidenceFactory = copyNumberEvidenceFactory;
        this.disruptionEvidenceFactory = disruptionEvidenceFactory;
        this.fusionEvidenceFactory = fusionEvidenceFactory;
        this.purpleSignatureEvidenceFactory = purpleSignatureEvidenceFactory;
        this.virusEvidenceFactory = virusEvidenceFactory;
        this.chordEvidenceFactory = chordEvidenceFactory;
        this.hlaEvidenceFactory = hlaEvidenceFactory;
        this.wildTypeEvidenceFactory = wildTypeEvidenceFactory;
        this.clinicalTranscriptsModel = clinicalTranscriptsModel;
    }

    @NotNull
    public List<ProtectEvidence> run(@NotNull OrangeRecord orange, @Nullable PatientInformationResponse diagnosticPatientData) {
        LOGGER.info("Evidence extraction started");

        Set<ReportableVariant> reportableGermlineVariants =
                ReportableVariantFactory.createReportableGermlineVariants(orange.purple(), clinicalTranscriptsModel);
        Set<ReportableVariant> reportableSomaticVariants =
                ReportableVariantFactory.createReportableSomaticVariants(orange.purple(), clinicalTranscriptsModel);

        List<ProtectEvidence> variantEvidence = variantEvidenceFactory.evidence(reportableGermlineVariants,
                reportableSomaticVariants,
                orange.purple().allSomaticVariants(),
                orange.purple().allGermlineVariants(),
                diagnosticPatientData);
        printExtraction("somatic and germline variants", variantEvidence);

        List<ProtectEvidence> copyNumberEvidence = copyNumberEvidenceFactory.evidence(orange.purple().reportableSomaticGainsLosses(),
                orange.purple().allSomaticGainsLosses(),
                orange.purple().reportableGermlineFullLosses(),
                orange.purple().allGermlineFullLosses(),
                orange.purple().reportableGermlineLossOfHeterozygosities(),
                orange.purple().allGermlineLossOfHeterozygosities(),
                diagnosticPatientData);
        printExtraction("amplifications and deletions", copyNumberEvidence);

        List<ProtectEvidence> disruptionEvidence = disruptionEvidenceFactory.evidence(orange.linx().somaticHomozygousDisruptions(),
                orange.linx().germlineHomozygousDisruptions(),
                diagnosticPatientData);
        printExtraction("homozygous disruptions", disruptionEvidence);

        List<ProtectEvidence> fusionEvidence = fusionEvidenceFactory.evidence(orange.linx().reportableSomaticFusions(),
                orange.linx().allSomaticFusions(),
                diagnosticPatientData);
        printExtraction("fusions", fusionEvidence);

        List<ProtectEvidence> purpleSignatureEvidence =
                purpleSignatureEvidenceFactory.evidence(orange.purple().characteristics(), diagnosticPatientData);
        printExtraction("purple signatures", purpleSignatureEvidence);

        List<ProtectEvidence> virusEvidence = virusEvidenceFactory.evidence(orange.virusInterpreter(), diagnosticPatientData);
        printExtraction("viruses", virusEvidence);

        List<ProtectEvidence> chordEvidence = chordEvidenceFactory.evidence(orange.chord(), diagnosticPatientData);
        printExtraction("chord", chordEvidence);

        List<ProtectEvidence> hlaEvidence = hlaEvidenceFactory.evidence(orange.lilac(), diagnosticPatientData);
        printExtraction("hla", hlaEvidence);

        List<ProtectEvidence> wildTypeEvidence = wildTypeEvidenceFactory.evidence(reportableGermlineVariants,
                reportableSomaticVariants,
                orange.purple().reportableSomaticGainsLosses(),
                orange.linx().reportableSomaticFusions(),
                orange.linx().somaticHomozygousDisruptions(),
                orange.linx().reportableSomaticBreakends(),
                orange.purple().fit().qc().status(),
                diagnosticPatientData);
        printExtraction("wild-type", wildTypeEvidence);

        List<ProtectEvidence> result = Lists.newArrayList();
        result.addAll(variantEvidence);
        result.addAll(copyNumberEvidence);
        result.addAll(disruptionEvidence);
        result.addAll(fusionEvidence);
        result.addAll(purpleSignatureEvidence);
        result.addAll(virusEvidence);
        result.addAll(chordEvidence);
        result.addAll(hlaEvidence);
        result.addAll(wildTypeEvidence);

        List<ProtectEvidence> consolidated = EvidenceConsolidation.consolidate(result);
        LOGGER.debug("Consolidated {} evidence items to {} unique evidence items", result.size(), consolidated.size());

        List<ProtectEvidence> reported = EvidenceReportingFunctions.applyReportingAlgo(consolidated);
        LOGGER.debug("Reduced reported evidence from {} items to {} items after applying reporting algo",
                reportedCount(consolidated),
                reportedCount(reported));

        List<ProtectEvidence> updatedForTrials = EvidenceReportingFunctions.reportOnLabelTrialsOnly(reported);
        LOGGER.debug("Reduced reported evidence from {} items to {} items by removing off-label trials",
                reportedCount(reported),
                reportedCount(updatedForTrials));

        return updatedForTrials;
    }

    private static void printExtraction(@NotNull String title, @NotNull List<ProtectEvidence> evidences) {
        Set<EvidenceKey> keys = EvidenceKey.buildKeySet(evidences);
        LOGGER.debug("Extracted {} evidence items for {} having {} keys ", evidences.size(), title, keys.size());
        for (EvidenceKey key : keys) {
            int count = (int) evidences.stream().filter(x -> EvidenceKey.create(x).equals(key)).count();
            LOGGER.debug(" Resolved {} items for '{}'", count, key);
        }
    }

    private static int reportedCount(@NotNull List<ProtectEvidence> evidences) {
        return (int) evidences.stream().filter(ProtectEvidence::reported).count();
    }
}