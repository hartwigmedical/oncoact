package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.model.Summary;
import com.hartwig.oncoact.rose.RoseConclusionFile;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

import static com.hartwig.oncoact.patientreporter.algo.wgs.GenomicAlterationsInCancerGenesCreator.createGenomicAlterationsInCancerGenesCreator;
import static com.hartwig.oncoact.patientreporter.algo.wgs.TumorCharacteristicsCreator.createTumorCharacteristics;

class SummaryCreator {
    static Summary createSummary(
            @NotNull GenomicAnalysis curatedAnalysis,
            @Nullable CuppaData cuppa,
            @Nullable Correction correction,
            @Nullable String roseTsvFile
    ) throws IOException {
        return Summary.builder()
                .mostRelevantFindings(getClinicalSummary(roseTsvFile))
                .specialRemark(getSpecialRemark(correction))
                .tumorCharacteristics(createTumorCharacteristics(curatedAnalysis, cuppa))
                .genomicAlterations(createGenomicAlterationsInCancerGenesCreator(curatedAnalysis))
                // TODO: pharmacogenetics, hlaAlleles
                .build();
    }

    private static String getClinicalSummary(String roseTsvFile) throws IOException {
        return roseTsvFile != null ? RoseConclusionFile.read(roseTsvFile) : Strings.EMPTY;
    }

    private static String getSpecialRemark(@Nullable Correction correction) {
        return Optional.ofNullable(correction).map(Correction::specialRemark).orElse("");
    }
}
