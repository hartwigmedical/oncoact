package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.model.Summary;
import com.hartwig.oncoact.rose.RoseConclusionFile;
import com.hartwig.oncoact.util.Formats;
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
            boolean isCorrection,
            @Nullable Correction correction,
            @Nullable String roseTsvFile,
            @NotNull QsFormNumber qcFormNumber
    ) throws IOException {
        return Summary.builder()
                .titleReport(getPdfTitle(isCorrection, qcFormNumber))
                .mostRelevantFindings(getClinicalSummary(roseTsvFile, curatedAnalysis.hasReliablePurity(), curatedAnalysis.impliedPurity()))
                .specialRemark(getSpecialRemark(correction))
                .tumorCharacteristics(createTumorCharacteristics(curatedAnalysis, cuppa))
                .genomicAlterations(createGenomicAlterationsInCancerGenesCreator(curatedAnalysis))
                .pharmacogenetics(createPharmacogeneticsGenotypeSummary(pharmacogeneticsGenotypesMap))
                .hlaAlleles(createHlaAllelesSummary(hlaReportingData))
                .hlaQc(hlaReportingData.hlaQC().equals("PASS"))
                .build();
    }

    private static String getPdfTitle(boolean isCorrection, QsFormNumber qcFormNumber) {
        if (isCorrection) {
            if (qcFormNumber.number.equals(QsFormNumber.FOR_209.number)) {
                return "OncoAct tumor WGS report \n- low purity analysis (Corrected)";
            } else {
                return "OncoAct tumor WGS report (Corrected)";
            }
        } else {
            if (qcFormNumber.number.equals(QsFormNumber.FOR_209.number)) {
                return "OncoAct tumor WGS report \n- low purity analysis";
            } else {
                return "OncoAct tumor WGS report";
            }
        }
    }

    private static String getClinicalSummary(String roseTsvFile, boolean hasReliablePurity, double purity) throws IOException {
        String roseSummary = roseTsvFile != null ? RoseConclusionFile.read(roseTsvFile) : Strings.EMPTY;

        String clinicalConclusion = Strings.EMPTY;
        String sentence = "An overview of all detected cancer associated DNA aberrations can be found in the report";

        if (!hasReliablePurity) {
            clinicalConclusion = "Of note, WGS analysis indicated a very low abundance of genomic aberrations, which can be caused "
                    + "by a low tumor percentage in the received tumor material or due to genomic very stable/normal tumor type. "
                    + "As a consequence no reliable tumor purity assessment is possible and no information regarding "
                    + "mutation copy number and tVAF can be provided.\n" + sentence;
        } else if (purity < ReportResources.PURITY_CUTOFF) {
            clinicalConclusion = "Due to the lower sensitivity (" + Formats.formatPercentage(purity) + ") "
                    + "of this test potential (subclonal) DNA aberrations might not have been detected using this test. "
                    + "This result should therefore be considered with caution.\n" + sentence;
        } else {
            clinicalConclusion = roseSummary + sentence;
        }

        return clinicalConclusion;
    }

    @NotNull
    private static String getSpecialRemark(@Nullable Correction correction) {
        return Optional.ofNullable(correction).map(Correction::specialRemark).orElse("");
    }
}