package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.model.Summary;
import com.hartwig.oncoact.rose.RoseConclusionFile;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.hartwig.oncoact.patientreporter.algo.wgs.GenomicAlterationsInCancerGenesCreator.createGenomicAlterationsInCancerGenesCreator;
import static com.hartwig.oncoact.patientreporter.algo.wgs.HlaAllelesCreator.createHlaAllelesSummary;
import static com.hartwig.oncoact.patientreporter.algo.wgs.PharmacogeneticsCreator.createPharmacogeneticsGenotypeSummary;
import static com.hartwig.oncoact.patientreporter.algo.wgs.TumorCharacteristicsCreator.createTumorCharacteristics;

class SummaryCreator {
    static Summary createSummary(
            @NotNull GenomicAnalysis curatedAnalysis,
            @NotNull Map<String, List<PeachGenotype>> pharmacogeneticsGenotypesMap,
            @NotNull HlaAllelesReportingData hlaReportingData,
            @Nullable CuppaData cuppa,
            @Nullable Correction correction,
            @Nullable String roseTsvFile
    ) throws IOException {
        return Summary.builder()
                .mostRelevantFindings(getClinicalSummary(roseTsvFile))
                .specialRemark(getSpecialRemark(correction))
                .tumorCharacteristics(createTumorCharacteristics(curatedAnalysis, cuppa))
                .genomicAlterations(createGenomicAlterationsInCancerGenesCreator(curatedAnalysis))
                .pharmacogenetics(createPharmacogeneticsGenotypeSummary(pharmacogeneticsGenotypesMap))
                .hlaAlleles(createHlaAllelesSummary(hlaReportingData))
                .build();
    }

    private static String getClinicalSummary(String roseTsvFile) throws IOException {
        return roseTsvFile != null ? RoseConclusionFile.read(roseTsvFile) : Strings.EMPTY;
    }

    private static String getSpecialRemark(@Nullable Correction correction) {
        return Optional.ofNullable(correction).map(Correction::specialRemark).orElse("");
    }
}