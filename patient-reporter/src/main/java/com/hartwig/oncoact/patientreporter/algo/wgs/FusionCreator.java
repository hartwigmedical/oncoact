package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.oncoact.patientreporter.algo.CurationFunctions;
import com.hartwig.oncoact.patientreporter.model.FusionDriverInterpretation;
import com.hartwig.oncoact.patientreporter.model.ObservedGeneFusion;
import com.hartwig.oncoact.patientreporter.model.ObservedGeneFusionType;
import com.hartwig.oncoact.patientreporter.model.PhasedType;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class FusionCreator {

    static List<ObservedGeneFusion> createFusion(
            @NotNull List<LinxFusion> geneFusions,
            boolean hasReliablePurity
    ) {
        List<ObservedGeneFusion> observedGeneFusions = Lists.newArrayList();
        for (LinxFusion fusion : geneFusions) {
            observedGeneFusions.add(ObservedGeneFusion.builder()
                    .name(CurationFunctions.curateGeneNamePdf(fusion.geneStart()) + " - " + CurationFunctions.curateGeneNamePdf(fusion.geneEnd()))
                    .type(type(fusion))
                    .fivePromiscuousTranscript(fusion.geneTranscriptStart())
                    .threePromiscuousTranscript(fusion.geneTranscriptEnd())
                    .fivePromiscuousEnd(fusion.geneContextStart())
                    .threePromiscuousStart(fusion.geneContextEnd())
                    .copies(roundCopyNumber(fusion.junctionCopyNumber(), hasReliablePurity))
                    .phasing(phased(fusion))
                    .driver(likelihood(fusion))
                    .build());
        }
        return observedGeneFusions;
    }

    @NotNull
    public static ObservedGeneFusionType type(@NotNull LinxFusion fusion) {

        switch (fusion.reportedType()) {
            case NONE:
                return ObservedGeneFusionType.NONE;
            case PROMISCUOUS_3:
                return ObservedGeneFusionType.PROMISCUOUS_3;
            case PROMISCUOUS_5:
                return ObservedGeneFusionType.PROMISCUOUS_5;
            case PROMISCUOUS_BOTH:
                return ObservedGeneFusionType.PROMISCUOUS_BOTH;
            case IG_PROMISCUOUS:
                return ObservedGeneFusionType.IG_PROMISCUOUS;
            case KNOWN_PAIR:
                return ObservedGeneFusionType.KNOWN_PAIR;
            case IG_KNOWN_PAIR:
                return ObservedGeneFusionType.IG_KNOWN_PAIR;
            case EXON_DEL_DUP:
                return ObservedGeneFusionType.EXON_DEL_DUP;
            case PROMISCUOUS_ENHANCER_TARGET:
                return ObservedGeneFusionType.PROMISCUOUS_ENHANCER_TARGET;
            default:
                throw new IllegalStateException("Unknown fusion type: " + fusion.reportedType());
        }
    }

    @NotNull
    public static PhasedType phased(@NotNull LinxFusion fusion) {
        switch (fusion.phased()) {
            case INFRAME: {
                return PhasedType.INFRAME;
            }
            case SKIPPED_EXONS: {
                return PhasedType.SKIPPED_EXONS;
            }
            case OUT_OF_FRAME: {
                return PhasedType.OUT_OF_FRAME;
            }
            default: {
                throw new IllegalStateException("Invalid fusion type: " + fusion.phased());
            }
        }
    }

    @NotNull
    public static FusionDriverInterpretation likelihood(@NotNull LinxFusion fusion) {
        switch (fusion.likelihood()) {
            case HIGH: {
                return FusionDriverInterpretation.HIGH;
            }
            case LOW: {
                return FusionDriverInterpretation.LOW;
            }
            case NA: {
                return FusionDriverInterpretation.NA;
            }
            default: {
                throw new IllegalStateException("Invalid fusion likelihood: " + fusion.likelihood());
            }
        }
    }


    @NotNull
    public static String roundCopyNumber(Double copyNumber, boolean hasReliablePurity) {
        return hasReliablePurity && !copyNumber.isNaN() ? String.valueOf(Math.round(copyNumber)) : Formats.NA_STRING;
    }
}