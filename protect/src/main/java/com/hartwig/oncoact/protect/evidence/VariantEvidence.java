package com.hartwig.oncoact.protect.evidence;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantType;
import com.hartwig.hmftools.datamodel.purple.Variant;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.Genes;
import com.hartwig.oncoact.variant.DriverInterpretation;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantFactory;
import com.hartwig.oncoact.variant.ReportableVariantSource;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariantEvidence {

    private static final Logger LOGGER = LogManager.getLogger(VariantEvidence.class);

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableHotspot> hotspots;
    @NotNull
    private final List<ActionableRange> codons;
    @NotNull
    private final List<ActionableRange> exons;
    @NotNull
    private final List<ActionableGene> genes;

    public VariantEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableHotspot> hotspots, @NotNull final List<ActionableRange> codons,
            @NotNull final List<ActionableRange> exons, @NotNull final List<ActionableGene> genes) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.hotspots = hotspots;
        this.codons = codons;
        this.exons = exons;
        this.genes = genes.stream()
                .filter(x -> x.event() == GeneEvent.ACTIVATION || x.event() == GeneEvent.INACTIVATION
                        || x.event() == GeneEvent.ABSENCE_OF_PROTEIN || x.event() == GeneEvent.ANY_MUTATION)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull Collection<ReportableVariant> reportableGermline,
            @NotNull Collection<ReportableVariant> reportableSomatic, @NotNull Collection<PurpleVariant> allSomaticVariants,
            @Nullable Collection<PurpleVariant> allGermlineVariants, @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        for (ReportableVariant reportableVariant : ReportableVariantFactory.mergeVariantLists(reportableGermline, reportableSomatic)) {
            evidences.addAll(evidence(reportableVariant, diagnosticPatientData));
        }

        for (PurpleVariant somaticVariant : ReportableVariantFactory.mergeAllVariantLists(allSomaticVariants, allGermlineVariants)) {
            if (!somaticVariant.reported()) {
                evidences.addAll(evidence(somaticVariant, diagnosticPatientData));
            }
        }

        return evidences;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull Variant variant, @Nullable PatientInformationResponse diagnosticPatientData) {
        boolean mayReport;
        DriverInterpretation driverInterpretation;

        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            mayReport = true;
            driverInterpretation = reportable.driverLikelihoodInterpretation();
        } else {
            mayReport = false;
            driverInterpretation = DriverInterpretation.LOW;
        }

        List<ProtectEvidence> evidences = Lists.newArrayList();
        for (ActionableHotspot hotspot : hotspots) {
            if (hotspotMatch(variant, hotspot)) {
                evidences.add(evidence(variant, hotspot, mayReport, "hotspot", diagnosticPatientData));
            }
        }

        for (ActionableRange codon : codons) {
            if (rangeMatch(variant, codon)) {
                evidences.add(evidence(variant,
                        codon,
                        mayReport && driverInterpretation == DriverInterpretation.HIGH,
                        "codon",
                        diagnosticPatientData));
            }
        }

        for (ActionableRange exon : exons) {
            if (rangeMatch(variant, exon)) {
                evidences.add(evidence(variant,
                        exon,
                        mayReport && driverInterpretation == DriverInterpretation.HIGH,
                        "exon",
                        diagnosticPatientData));
            }
        }

        for (ActionableGene gene : genes) {
            if (geneMatch(variant, gene)) {
                boolean report = mayReport && driverInterpretation == DriverInterpretation.HIGH;
                EvidenceType type = PersonalizedEvidenceFactory.determineEvidenceType(gene, null);
                if (type.equals(EvidenceType.ABSENCE_OF_PROTEIN)) {
                    if (Genes.MSI_GENES.contains(gene.gene())) {
                        report = true;
                    } else {
                        report = false;
                    }
                }
                evidences.add(evidence(variant, gene, report, "gene", diagnosticPatientData));
            }
        }

        return evidences;
    }

    @NotNull
    private ProtectEvidence evidence(@NotNull Variant variant, @NotNull ActionableEvent actionable, boolean report, @NotNull String range,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        boolean isGermline;
        DriverInterpretation driverInterpretation;
        String transcript;
        boolean isCanonical;
        Integer rangeRank;
        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            isGermline =
                    reportable.source() == ReportableVariantSource.GERMLINE || reportable.source() == ReportableVariantSource.GERMLINE_ONLY;
            driverInterpretation = reportable.driverLikelihoodInterpretation();
            transcript = reportable.transcript();
            isCanonical = reportable.isCanonical();
            rangeRank = determineRangeRank(range, reportable.affectedCodon(), reportable.affectedExon());
        } else if (variant instanceof PurpleVariant) {
            PurpleVariant purple = (PurpleVariant) variant;
            isGermline = false;
            driverInterpretation = DriverInterpretation.LOW;
            transcript = purple.canonicalImpact().transcript();
            isCanonical = true;
            rangeRank = determineRangeRank(range, purple.canonicalImpact().affectedCodon(), purple.canonicalImpact().affectedExon());
        } else {
            throw new IllegalArgumentException(String.format("Variant of type '%s' not supported", variant.getClass().getName()));
        }

        return personalizedEvidenceFactory.evidenceBuilderRange(actionable, range, rangeRank, diagnosticPatientData, report)
                .gene(variant.gene())
                .transcript(transcript)
                .isCanonical(isCanonical)
                .event(EventGenerator.variantEvent(variant))
                .germline(isGermline)
                .eventIsHighDriver(driverInterpretation == null ? null : EvidenceDriverLikelihood.interpretVariant(driverInterpretation))
                .build();
    }

    private static Integer determineRangeRank(@NotNull String range, @Nullable Integer affectedCodon, @Nullable Integer affectedExon) {
        switch (range) {
            case "codon":
                return affectedCodon;
            case "exon":
                return affectedExon;
            case "hotspot":
            case "gene":
                return null;
            default:
                throw new IllegalStateException("Unknown range string detected: " + range);
        }
    }

    private static boolean hotspotMatch(@NotNull Variant variant, @NotNull ActionableHotspot hotspot) {
        return variant.chromosome().equals(hotspot.chromosome()) && hotspot.position() == variant.position() && hotspot.ref()
                .equals(variant.ref()) && hotspot.alt().equals(variant.alt());
    }

    private static boolean rangeMatch(@NotNull Variant variant, @NotNull ActionableRange range) {
        return variant.chromosome().equals(range.chromosome()) && variant.gene().equals(range.gene()) && variant.position() >= range.start()
                && variant.position() <= range.end() && meetsMutationType(variant, range.applicableMutationType());
    }

    private static boolean geneMatch(@NotNull Variant variant, @NotNull ActionableGene gene) {
        assert gene.event() == GeneEvent.ACTIVATION || gene.event() == GeneEvent.INACTIVATION || gene.event() == GeneEvent.ANY_MUTATION
                || gene.event() == GeneEvent.ABSENCE_OF_PROTEIN;

        return gene.gene().equals(variant.gene()) && meetsMutationType(variant, MutationType.ANY);
    }

    private static boolean meetsMutationType(@NotNull Variant variant, @NotNull MutationType applicableMutationType) {
        PurpleCodingEffect effect;
        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            effect = reportable.canonicalCodingEffect();
        } else if (variant instanceof PurpleVariant) {
            PurpleVariant purple = (PurpleVariant) variant;
            effect = purple.canonicalImpact().codingEffect();
        } else {
            throw new IllegalArgumentException("Variant is defined in a wrong variant other than ReportableVariant and PurpleVariant");
        }

        switch (applicableMutationType) {
            case NONSENSE_OR_FRAMESHIFT:
                return effect == PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT;
            case SPLICE:
                return effect == PurpleCodingEffect.SPLICE;
            case INFRAME:
                return effect == PurpleCodingEffect.MISSENSE && variant.type() == PurpleVariantType.INDEL;
            case INFRAME_DELETION:
                return effect == PurpleCodingEffect.MISSENSE && isDelete(variant);
            case INFRAME_INSERTION:
                return effect == PurpleCodingEffect.MISSENSE && isInsert(variant);
            case MISSENSE:
                return effect == PurpleCodingEffect.MISSENSE;
            case ANY:
                return effect == PurpleCodingEffect.MISSENSE || effect == PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT
                        || effect == PurpleCodingEffect.SPLICE;
            default: {
                LOGGER.warn("Unrecognized mutation type filter: '{}'", applicableMutationType);
                return false;
            }
        }
    }

    private static boolean isInsert(@NotNull Variant variant) {
        return variant.type() == PurpleVariantType.INDEL && variant.alt().length() > variant.ref().length();
    }

    private static boolean isDelete(@NotNull Variant variant) {
        return variant.type() == PurpleVariantType.INDEL && variant.alt().length() < variant.ref().length();
    }
}