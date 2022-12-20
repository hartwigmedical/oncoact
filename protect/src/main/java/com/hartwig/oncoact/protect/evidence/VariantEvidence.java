package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.datamodel.DriverInterpretation;
import com.hartwig.oncoact.datamodel.ReportableVariant;
import com.hartwig.oncoact.datamodel.ReportableVariantSource;
import com.hartwig.oncoact.interpretation.AltTranscriptReportableInfo;
import com.hartwig.oncoact.interpretation.ReportableVariantFactory;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleCodingEffect;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleVariant;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleVariantType;
import com.hartwig.oncoact.orange.datamodel.purple.Variant;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class VariantEvidence {

    private static final Logger LOGGER = LogManager.getLogger(VariantEvidence.class);

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableHotspot> hotspots;
    @NotNull
    private final List<ActionableRange> ranges;
    @NotNull
    private final List<ActionableGene> genes;

    public VariantEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableHotspot> hotspots, @NotNull final List<ActionableRange> ranges,
            @NotNull final List<ActionableGene> genes) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.hotspots = hotspots;
        this.ranges = ranges;
        this.genes = genes.stream()
                .filter(x -> x.event() == GeneEvent.ACTIVATION || x.event() == GeneEvent.INACTIVATION
                        || x.event() == GeneEvent.ANY_MUTATION)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull Set<ReportableVariant> reportableGermline,
            @NotNull Set<ReportableVariant> reportableSomatic, @NotNull Set<PurpleVariant> allSomaticVariants) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        for (ReportableVariant reportableVariant : ReportableVariantFactory.mergeVariantLists(reportableGermline, reportableSomatic)) {
            evidences.addAll(evidence(reportableVariant));
        }

        for (PurpleVariant somaticVariant : allSomaticVariants) {
            if (!somaticVariant.reported()) {
                evidences.addAll(evidence(somaticVariant));
            }
        }

        return evidences;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull Variant variant) {
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
                evidences.add(evidence(variant, hotspot, mayReport));
            }
        }

        for (ActionableRange range : ranges) {
            if (rangeMatch(variant, range)) {
                evidences.add(evidence(variant, range, mayReport && driverInterpretation == DriverInterpretation.HIGH));
            }
        }

        for (ActionableGene gene : genes) {
            if (geneMatch(variant, gene)) {
                evidences.add(evidence(variant, gene, mayReport && driverInterpretation == DriverInterpretation.HIGH));
            }
        }

        return evidences;
    }

    @NotNull
    private ProtectEvidence evidence(@NotNull Variant variant, @NotNull ActionableEvent actionable, boolean report) {
        boolean isGermline;
        DriverInterpretation driverInterpretation;
        String transcript;
        boolean isCanonical;

        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;

            isGermline = reportable.source() == ReportableVariantSource.GERMLINE;
            driverInterpretation = reportable.driverLikelihoodInterpretation();
            transcript = reportable.transcript();
            isCanonical = reportable.isCanonical();
        } else {
            PurpleVariant purple = (PurpleVariant) variant;
            isGermline = false;
            driverInterpretation = DriverInterpretation.LOW;
            transcript = purple.canonicalImpact().transcript();
            isCanonical = true;
        }

        return personalizedEvidenceFactory.evidenceBuilder(actionable)
                .gene(variant.gene())
                .transcript(transcript)
                .isCanonical(isCanonical)
                .event(EventGenerator.variantEvent(variant))
                .germline(isGermline)
                .reported(report)
                .eventIsHighDriver(EvidenceDriverLikelihood.interpretVariant(driverInterpretation))
                .build();
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
        assert gene.event() == GeneEvent.ACTIVATION || gene.event() == GeneEvent.INACTIVATION || gene.event() == GeneEvent.ANY_MUTATION;

        return gene.gene().equals(variant.gene()) && meetsMutationType(variant, MutationType.ANY);
    }

    private static boolean meetsMutationType(@NotNull Variant variant, @NotNull MutationType applicableMutationType) {
        PurpleCodingEffect effect;
        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            effect = reportable.isCanonical()
                    ? reportable.canonicalCodingEffect()
                    : AltTranscriptReportableInfo.firstOtherCodingEffect(reportable.otherReportedEffects());
        } else {
            PurpleVariant purple = (PurpleVariant) variant;
            effect = purple.canonicalImpact().codingEffect();
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