package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.data.ViralPresence;
import com.hartwig.oncoact.patientreporter.model.TumorCharacteristics;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.hartwig.oncoact.patientreporter.algo.wgs.HomologousRecombinationDeficiencyCreator.createHomologousRecombinationDeficiency;
import static com.hartwig.oncoact.patientreporter.algo.wgs.PurityCreator.createPurity;
import static com.hartwig.oncoact.patientreporter.algo.wgs.TissueOfOriginPredictionCreator.createTissueOfOriginPrediction;
import static com.hartwig.oncoact.patientreporter.algo.wgs.TumorMutationalBurdenCreator.createTumorMutationalBurden;

class TumorCharacteristicsCreator {

    static TumorCharacteristics createTumorCharacteristics(
            @NotNull GenomicAnalysis analysis,
            @Nullable CuppaData cuppa
    ) {
        return TumorCharacteristics.builder()
                .purity(createPurity(analysis))
                .tissueOfOriginPrediction(createTissueOfOriginPrediction(analysis, cuppa))
                .tumorMutationalBurden(createTumorMutationalBurden(analysis))
                .microsatellite(MicrosatelliteCreator.createMicrosatallite(analysis))
                .homologousRecombinationDeficiency(createHomologousRecombinationDeficiency(analysis))
                .viruses(createViruses(analysis))
                .build();
    }

    private static String createViruses(GenomicAnalysis analysis) {
        Set<String> viruses = ViralPresence.virusInterpretationSummary(analysis.reportableViruses());

        if (viruses.isEmpty()) {
            return Formats.NONE_STRING;
        } else {
            return String.join(", ", viruses);
        }
    }
}
