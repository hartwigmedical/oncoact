package com.hartwig.oncoact.reporting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.hartwig.oncoact.clinical.ImmutablePatientPrimaryTumor;
import com.hartwig.oncoact.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.copynumber.Chromosome;
import com.hartwig.oncoact.copynumber.ChromosomeArm;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.ImmutableCnPerChromosomeArmData;
import com.hartwig.oncoact.cuppa.ImmutableMolecularTissueOriginReporting;
import com.hartwig.oncoact.cuppa.MolecularTissueOriginReporting;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.disruption.ImmutableGeneDisruption;
import com.hartwig.oncoact.hla.*;
import com.hartwig.oncoact.lims.LimsGermlineReportingLevel;
import com.hartwig.oncoact.lims.cohort.ImmutableLimsCohortConfig;
import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;
import com.hartwig.oncoact.lims.hospital.HospitalContactData;
import com.hartwig.oncoact.lims.hospital.ImmutableHospitalContactData;
import com.hartwig.oncoact.orange.chord.ChordStatus;
import com.hartwig.oncoact.orange.linx.*;
import com.hartwig.oncoact.orange.peach.ImmutablePeachEntry;
import com.hartwig.oncoact.orange.peach.PeachEntry;
import com.hartwig.oncoact.orange.purple.*;
import com.hartwig.oncoact.orange.virus.*;
import com.hartwig.oncoact.protect.*;
import com.hartwig.oncoact.variant.ImmutableReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;
import com.hartwig.serve.datamodel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hartwig.oncoact.json.Json.*;
import static com.hartwig.oncoact.json.Json.object;

public class ReportingJson {

    private static final Logger LOGGER = LogManager.getLogger(ReportingJson.class);

    private ReportingJson() {
    }

    @NotNull
    public static AnalysedPatientReport read(@NotNull String reportingJson) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(AnalysedPatientReport.class, new ReportingJson.AnalysedPatientReportCreator()).create();

        Path reportingJsonPath = new File(reportingJson).toPath();
        return gson.fromJson(Files.readString(reportingJsonPath), AnalysedPatientReport.class);
    }

    private static class AnalysedPatientReportCreator implements JsonDeserializer<AnalysedPatientReport> {

        @Override
        public AnalysedPatientReport deserialize(@NotNull JsonElement jsonElement, @NotNull Type type,
                                                 @NotNull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject record = jsonElement.getAsJsonObject();

            return ImmutableAnalysedPatientReport.builder()
                    .sampleReport(toSampleReportRecord(object(record, "sampleReport")))
                    .qsFormNumber(string(record, "qsFormNumber"))
                    .clinicalSummary(string(record, "clinicalSummary"))
                    .specialRemark(string(record, "specialRemark"))
                    .pipelineVersion(string(record, "pipelineVersion"))
                    .genomicAnalysis(toGenomicAnalysis(object(record, "genomicAnalysis")))
                    .molecularTissueOriginReporting(toMolecularTissueOriginReporting(object(record, "molecularTissueOriginReporting")))
                    .molecularTissueOriginPlotPath(string(record, "molecularTissueOriginPlotPath"))
                    .circosPlotPath(string(record, "circosPlotPath"))
                    .pharmacogeneticsGenotypes(toPgx(object(record, "pharmacogeneticsGenotypes")))
                    .hlaAllelesReportingData(toHla(object(record, "hlaAllelesReportingData")))
                    .comments(string(record, "comments"))
                    .isCorrectedReport(bool(record, "isCorrectedReport"))
                    .isCorrectedReportExtern(bool(record, "isCorrectedReportExtern"))
                    .signaturePath(string(record, "signaturePath"))
                    .logoRVAPath(string(record, "logoRVAPath"))
                    .logoCompanyPath(string(record, "logoCompanyPath"))
                    .reportDate(string(record, "reportDate"))
                    .udiDi(string(record, "udiDi"))
                    .isWGSReport(bool(record, "isWGSreport"))
                    .build();
        }

        @NotNull
        private static SampleReport toSampleReportRecord(@NotNull JsonObject sampleReport) {
            return ImmutableSampleReport.builder()
                    .sampleMetadata(toSampleMetadataRecord(object(sampleReport, "sampleMetadata")))
                    .tumorReceivedSampleId(string(sampleReport, "tumorReceivedSampleId"))
                    .referenceReceivedSampleId(string(sampleReport, "referenceReceivedSampleId"))
                    .patientPrimaryTumor(toPatientPrimaryTumor(object(sampleReport, "patientPrimaryTumor")))
                    .biopsyLocation(string(sampleReport, "biopsyLocation"))
                    .germlineReportingLevel(LimsGermlineReportingLevel.valueOf(string(sampleReport, "germlineReportingLevel")))
                    .reportViralPresence(bool(sampleReport, "reportViralPresence"))
                    .reportPharmogenetics(bool(sampleReport, "reportPharmogenetics"))
                    .refArrivalDate(date(sampleReport, "refArrivalDate"))
                    .tumorArrivalDate(date(sampleReport, "tumorArrivalDate"))
                    .shallowSeqPurityString(string(sampleReport, "shallowSeqPurityString"))
                    .labProcedures(string(sampleReport, "labProcedures"))
                    .cohort(toCohort(object(sampleReport, "cohort")))
                    .projectName(string(sampleReport, "projectName"))
                    .submissionId(string(sampleReport, "submissionId"))
                    .hospitalContactData(hospitalContactData(object(sampleReport, "hospitalContactData")))
                    .hospitalPatientId(string(sampleReport, "hospitalPatientId"))
                    .hospitalPathologySampleId(string(sampleReport, "hospitalPathologySampleId"))
                    .build();
        }

        @NotNull
        private static SampleMetadata toSampleMetadataRecord(@NotNull JsonObject sampleMetadata) {
            return ImmutableSampleMetadata.builder()
                    .refSampleId(string(sampleMetadata, "refSampleId"))
                    .refSampleBarcode(string(sampleMetadata, "refSampleBarcode"))
                    .tumorSampleId(string(sampleMetadata, "tumorSampleId"))
                    .tumorSampleBarcode(string(sampleMetadata, "tumorSampleBarcode"))
                    .sampleNameForReport(string(sampleMetadata, "sampleNameForReport"))
                    .build();
        }

        @NotNull
        private static PatientPrimaryTumor toPatientPrimaryTumor(@NotNull JsonObject patientPrimaryTumor) {
            return ImmutablePatientPrimaryTumor.builder()
                    .patientIdentifier(string(patientPrimaryTumor, "patientIdentifier"))
                    .location(string(patientPrimaryTumor, "location"))
                    .subLocation(string(patientPrimaryTumor, "subLocation"))
                    .type(string(patientPrimaryTumor, "type"))
                    .subType(string(patientPrimaryTumor, "subType"))
                    .extraDetails(string(patientPrimaryTumor, "extraDetails"))
                    .doids(stringList(patientPrimaryTumor, "doids"))
                    .snomedConceptIds(stringList(patientPrimaryTumor, "snomedConceptIds"))
                    .isOverridden(bool(patientPrimaryTumor, "isOverridden"))
                    .build();
        }

        @NotNull
        private static LimsCohortConfig toCohort(@NotNull JsonObject cohort) {
            return ImmutableLimsCohortConfig.builder()
                    .cohortId(string(cohort, "cohortId"))
                    .sampleContainsHospitalCenterId(bool(cohort, "sampleContainsHospitalCenterId"))
                    .reportGermline(bool(cohort, "reportGermline"))
                    .reportGermlineFlag(bool(cohort, "reportGermlineFlag"))
                    .reportConclusion(bool(cohort, "reportConclusion"))
                    .reportViral(bool(cohort, "reportViral"))
                    .reportPeach(bool(cohort, "reportPeach"))
                    .requireHospitalId(bool(cohort, "requireHospitalId"))
                    .requireHospitalPAId(bool(cohort, "requireHospitalPAId"))
                    .requireHospitalPersonsStudy(bool(cohort, "requireHospitalPersonsStudy"))
                    .requireHospitalPersonsRequester(bool(cohort, "requireHospitalPersonsRequester"))
                    .requireAdditionalInformationForSidePanel(bool(cohort, "requireAdditionalInformationForSidePanel"))
                    .build();
        }

        @NotNull
        private static HospitalContactData hospitalContactData(@NotNull JsonObject hospitalContactData) {
            return ImmutableHospitalContactData.builder()
                    .hospitalPI(string(hospitalContactData, "hospitalPI"))
                    .requesterName(string(hospitalContactData, "requesterName"))
                    .requesterEmail(string(hospitalContactData, "requesterEmail"))
                    .hospitalName(string(hospitalContactData, "hospitalName"))
                    .hospitalAddress(string(hospitalContactData, "hospitalAddress"))
                    .build();
        }

        @NotNull
        private static GenomicAnalysis toGenomicAnalysis(@NotNull JsonObject genomicAnalysis) {
            return ImmutableGenomicAnalysis.builder()
                    .purpleQCStatus(toPurpleQCStatus(genomicAnalysis))
                    .impliedPurity(number(genomicAnalysis, "impliedPurity"))
                    .hasReliablePurity(bool(genomicAnalysis, "hasReliablePurity"))
                    .hasReliableQuality(bool(genomicAnalysis, "hasReliableQuality"))
                    .averageTumorPloidy(number(genomicAnalysis, "averageTumorPloidy"))
                    .tumorSpecificEvidence(toTumorSpecificEvidence(array(genomicAnalysis, "tumorSpecificEvidence")))
                    .clinicalTrials(toTumorSpecificEvidence(array(genomicAnalysis, "clinicalTrials")))
                    .offLabelEvidence(toTumorSpecificEvidence(array(genomicAnalysis, "offLabelEvidence")))
                    .reportableVariants(toReportableVariants(array(genomicAnalysis, "reportableVariants")))
                    .notifyGermlineStatusPerVariant(Maps.newHashMap()) //TODO: implement reading
                    .microsatelliteIndelsPerMb(number(genomicAnalysis, "microsatelliteIndelsPerMb"))
                    .microsatelliteStatus(PurpleMicrosatelliteStatus.valueOf(string(genomicAnalysis, "microsatelliteStatus")))
                    .tumorMutationalLoad(integer(genomicAnalysis, "tumorMutationalLoad"))
                    .tumorMutationalLoadStatus(PurpleTumorMutationalStatus.valueOf(string(genomicAnalysis, "tumorMutationalLoadStatus")))
                    .tumorMutationalBurden(number(genomicAnalysis, "tumorMutationalBurden"))
                    .hrdValue(number(genomicAnalysis, "hrdValue"))
                    .hrdStatus(ChordStatus.valueOf(string(genomicAnalysis, "hrdStatus")))
                    .gainsAndLosses(toPurpleGainsLosses(array(genomicAnalysis, "gainsAndLosses")))
                    .cnPerChromosome(toCnPerChromosome(array(genomicAnalysis, "cnPerChromosome")))
                    .geneFusions(toGeneFusions(array(genomicAnalysis, "geneFusions")))
                    .geneDisruptions(toGeneDisruption(array(genomicAnalysis, "geneDisruptions")))
                    .homozygousDisruptions(toHomozygousDisruptions(array(genomicAnalysis, "homozygousDisruptions")))
                    .reportableViruses(toVirusInterpreterEntries(array(genomicAnalysis, "reportableViruses")))
                    .suspectGeneCopyNumbersWithLOH(toLohEvents(array(genomicAnalysis, "suspectGeneCopyNumbersHRDWithLOH")))
                    .build();
        }

        @NotNull
        private static List<PurpleGeneCopyNumber> toLohEvents(@NotNull JsonArray lohEvents) {
            List<PurpleGeneCopyNumber> lohEventsList = Lists.newArrayList();
            for (JsonElement element : lohEvents) {
                JsonObject lohEvent = element.getAsJsonObject();
                lohEventsList.add(ImmutablePurpleGeneCopyNumber.builder()
                        .chromosome(string(lohEvent, "chromosome"))
                        .chromosomeBand(string(lohEvent, "chromosomeBand"))
                        .gene(string(lohEvent, "gene"))
                        .minCopyNumber(nullableNumber(lohEvent, "minCopyNumber"))
                        .minMinorAlleleCopyNumber(nullableNumber(lohEvent, "minMinorAlleleCopyNumber"))
                        .build());
            }
            return lohEventsList;
        }

        @NotNull
        private static Set<PurpleQCStatus> toPurpleQCStatus(@NotNull JsonObject genomicAnalysis) {
            JsonArray array = array(genomicAnalysis, "purpleQCStatus");
            Set<PurpleQCStatus> status = Sets.newHashSet();
            for (JsonElement statusEntry : array) {
                status.add(PurpleQCStatus.valueOf(statusEntry.getAsString()));
            }
            return status;
        }

        @NotNull
        private static List<ProtectEvidence> toTumorSpecificEvidence(@NotNull JsonArray tumorSpecificEvidenceArray) {
            List<ProtectEvidence> tumorSpecificEvidenceList = Lists.newArrayList();
            for (JsonElement element : tumorSpecificEvidenceArray) {
                JsonObject tumorSpecificEvidence = element.getAsJsonObject();
                tumorSpecificEvidenceList.add(ImmutableProtectEvidence.builder()
                        .gene(nullableString(tumorSpecificEvidence, "gene"))
                        .transcript(nullableString(tumorSpecificEvidence, "transcript"))
                        .isCanonical(nullableBool(tumorSpecificEvidence, "isCanonical"))
                        .event(string(tumorSpecificEvidence, "event"))
                        .eventIsHighDriver(nullableBool(tumorSpecificEvidence, "eventIsHighDriver"))
                        .germline(bool(tumorSpecificEvidence, "germline"))
                        .reported(bool(tumorSpecificEvidence, "reported"))
                        .treatment(toTreatment(object(tumorSpecificEvidence, "treatment")))
                        .onLabel(bool(tumorSpecificEvidence, "onLabel"))
                        .level(EvidenceLevel.valueOf(string(tumorSpecificEvidence, "level")))
                        .direction(EvidenceDirection.valueOf(string(tumorSpecificEvidence, "direction")))
                        .sources(toSources(array(tumorSpecificEvidence, "sources")))
                        .build());
            }
            return tumorSpecificEvidenceList;
        }

        @NotNull
        private static Treatment toTreatment(@NotNull JsonObject treatment) {
            return ImmutableTreatment.builder()
                    .name(string(treatment, "treament"))
                    .sourceRelevantTreatmentApproaches(toTreatmentApproaches(treatment, "sourceRelevantTreatmentApproaches"))
                    .relevantTreatmentApproaches(toTreatmentApproaches(treatment, "relevantTreatmentApproaches"))
                    .build();
        }

        @NotNull
        private static Set<String> toTreatmentApproaches(@NotNull JsonObject treatment, @NotNull String key) {
            JsonArray array = array(treatment, key);
            Set<String> treatmentApproaches = Sets.newHashSet();
            for (JsonElement statusEntry : array) {
                treatmentApproaches.add(statusEntry.getAsString());
            }
            return treatmentApproaches;
        }

        @NotNull
        private static Set<KnowledgebaseSource> toSources(@Nullable JsonArray knowledgebaseSourceArray) {
            if (knowledgebaseSourceArray == null) {
                return Sets.newHashSet();
            }

            Set<KnowledgebaseSource> knowledgebaseSourceSet = Sets.newHashSet();
            for (JsonElement element : knowledgebaseSourceArray) {
                JsonObject knowledgebaseSource = element.getAsJsonObject();
                knowledgebaseSourceSet.add(ImmutableKnowledgebaseSource.builder()
                        .name(Knowledgebase.valueOf(string(knowledgebaseSource, "name")))
                        .sourceEvent(string(knowledgebaseSource, "sourceEvent"))
                        .sourceUrls(stringSet(knowledgebaseSource, "sourceUrls"))
                        .evidenceType(EvidenceType.valueOf(string(knowledgebaseSource, "evidenceType")))
                        .evidenceUrls(stringSet(knowledgebaseSource, "evidenceUrls"))
                        .build());
            }
            return knowledgebaseSourceSet;
        }

        @NotNull
        private static List<ReportableVariant> toReportableVariants(@Nullable JsonArray variantArray) {
            if (variantArray == null) {
                return Lists.newArrayList();
            }

            List<ReportableVariant> variants = Lists.newArrayList();
            for (JsonElement element : variantArray) {
                JsonObject variant = element.getAsJsonObject();
                variants.add(ImmutableReportableVariant.builder()
                        .source(ReportableVariantSource.valueOf(string(variant, "source")))
                        .gene(string(variant, "gene"))
                        .transcript(string(variant, "transcript"))
                        .isCanonical(bool(variant, "isCanonical"))
                        .chromosome(string(variant, "chromosome"))
                        .position(integer(variant, "position"))
                        .ref(string(variant, "ref"))
                        .alt(string(variant, "alt"))
                        .canonicalTranscript(string(variant, "canonicalTranscript"))
                        .canonicalEffect(string(variant, "canonicalEffect"))
                        .canonicalCodingEffect(PurpleCodingEffect.valueOf(string(variant, "canonicalCodingEffect")))
                        .canonicalHgvsCodingImpact(string(variant, "canonicalHgvsCodingImpact"))
                        .canonicalHgvsProteinImpact(string(variant, "canonicalHgvsProteinImpact"))
                        .otherReportedEffects(string(variant, "otherReportedEffects"))
                        .totalReadCount(integer(variant, "totalReadCount"))
                        .alleleReadCount(integer(variant, "alleleReadCount"))
                        .totalCopyNumber(integer(variant, "totalCopyNumber"))
                        .alleleCopyNumber(integer(variant, "alleleCopyNumber"))
                        .minorAlleleCopyNumber(integer(variant, "minorAlleleCopyNumber"))
                        .hotspot(PurpleHotspotType.valueOf(string(variant, "hotspot")))
                        .clonalLikelihood(number(variant, "clonalLikelihood"))
                        .driverLikelihood(number(variant, "driverLikelihood"))
                        .biallelic(bool(variant, "biallelic"))
                        .genotypeStatus(PurpleGenotypeStatus.valueOf(string(variant, "genotypeStatus")))
                        .localPhaseSet(nullableInteger(variant, "localPhaseSet"))
                        .type(PurpleVariantType.valueOf(string(variant, "type")))
                        .build());
            }
            return variants;
        }

        @NotNull
        private static List<PurpleGainLoss> toPurpleGainsLosses(@NotNull JsonArray gainLossArray) {
            List<PurpleGainLoss> gainsLosses = Lists.newArrayList();
            for (JsonElement element : gainLossArray) {
                JsonObject gainLoss = element.getAsJsonObject();
                gainsLosses.add(ImmutablePurpleGainLoss.builder()
                        .chromosome(string(gainLoss, "chromosome"))
                        .chromosomeBand(string(gainLoss, "chromosomeBand"))
                        .gene(string(gainLoss, "gene"))
                        .transcript(string(gainLoss, "transcript"))
                        .isCanonical(bool(gainLoss, "isCanonical"))
                        .interpretation(PurpleGainLossInterpretation.valueOf(string(gainLoss, "interpretation")))
                        .minCopies(integer(gainLoss, "minCopies"))
                        .maxCopies(integer(gainLoss, "maxCopies"))
                        .build());
            }
            return gainsLosses;
        }

        @NotNull
        private static List<CnPerChromosomeArmData> toCnPerChromosome(@NotNull JsonArray CnPerChromosomeArmDataArray) {
            List<CnPerChromosomeArmData> cnPerChromosomeArmDataList = Lists.newArrayList();
            for (JsonElement element : CnPerChromosomeArmDataArray) {
                JsonObject cnPerChromosomeArmData = element.getAsJsonObject();
                cnPerChromosomeArmDataList.add(ImmutableCnPerChromosomeArmData.builder()
                        .chromosome(Chromosome.valueOf(string(cnPerChromosomeArmData, "chromosome")))
                        .chromosomeArm(ChromosomeArm.valueOf(string(cnPerChromosomeArmData, "chromosomeArm")))
                        .copyNumber(number(cnPerChromosomeArmData, "copyNumber"))
                        .build());
            }
            return cnPerChromosomeArmDataList;
        }

        @NotNull
        private static List<LinxFusion> toGeneFusions(@NotNull JsonArray linxFusionArray) {
            List<LinxFusion> linxFusionList = Lists.newArrayList();
            for (JsonElement element : linxFusionArray) {
                JsonObject linxFusion = element.getAsJsonObject();
                linxFusionList.add(ImmutableLinxFusion.builder()
                        .name(string(linxFusion, "name"))
                        .reported(bool(linxFusion, "reported"))
                        .type(LinxFusionType.valueOf(string(linxFusion, "reportedType")))
                        .phased(LinxPhasedType.valueOf(string(linxFusion, "phased")))
                        .fusedExonDown(integer(linxFusion, "fusedExonDown"))
                        .fusedExonUp(integer(linxFusion, "fusedExonUp"))
                        .geneStart(string(linxFusion, "geneStart"))
                        .geneEnd(string(linxFusion, "geneEnd"))
                        .geneContextStart(string(linxFusion, "geneContextStart"))
                        .geneTranscriptStart(string(linxFusion, "geneTranscriptStart"))
                        .geneContextEnd(string(linxFusion, "geneContextEnd"))
                        .geneTranscriptEnd(string(linxFusion, "geneTranscriptEnd"))
                        .junctionCopyNumber(number(linxFusion, "junctionCopyNumber"))
                        .build());
            }
            return linxFusionList;
        }

        @NotNull
        private static List<GeneDisruption> toGeneDisruption(@NotNull JsonArray linxGeneDisruptionArray) {
            List<GeneDisruption> linxGeneDisruptionList = Lists.newArrayList();
            for (JsonElement element : linxGeneDisruptionArray) {
                JsonObject linxGeneDisruption = element.getAsJsonObject();
                linxGeneDisruptionList.add(ImmutableGeneDisruption.builder()
                        .location(string(linxGeneDisruption, "location"))
                        .gene(string(linxGeneDisruption, "gene"))
                        .transcriptId(string(linxGeneDisruption, "transcriptId"))
                        .isCanonical(bool(linxGeneDisruption, "isCanonical"))
                        .range(string(linxGeneDisruption, "range"))
                        .type(string(linxGeneDisruption, "type"))
                        .junctionCopyNumber(number(linxGeneDisruption, "junctionCopyNumber"))
                        .undisruptedCopyNumber(number(linxGeneDisruption, "undisruptedCopyNumber"))
                        .firstAffectedExon(integer(linxGeneDisruption, "firstAffectedExon"))
                        .clusterId(nullableInteger(linxGeneDisruption, "clusterId"))
                        .build());
            }
            return linxGeneDisruptionList;
        }

        @NotNull
        private static List<LinxHomozygousDisruption> toHomozygousDisruptions(@NotNull JsonArray homozygousDisruptionArray) {
            List<LinxHomozygousDisruption> homozygousDisruptions = Lists.newArrayList();
            for (JsonElement element : homozygousDisruptionArray) {
                JsonObject homozygousDisruption = element.getAsJsonObject();
                homozygousDisruptions.add(ImmutableLinxHomozygousDisruption.builder()
                        .chromosome(string(homozygousDisruption, "chromosome"))
                        .chromosomeBand(string(homozygousDisruption, "chromosomeBand"))
                        .gene(string(homozygousDisruption, "gene"))
                        .transcript(string(homozygousDisruption, "transcript"))
                        .isCanonical(bool(homozygousDisruption, "isCanonical"))
                        .build());
            }
            return homozygousDisruptions;
        }

        @NotNull
        private static Set<VirusInterpreterEntry> toVirusInterpreterEntries(@NotNull JsonArray virusEntryArray) {
            Set<VirusInterpreterEntry> entries = Sets.newHashSet();
            for (JsonElement element : virusEntryArray) {
                JsonObject virus = element.getAsJsonObject();
                entries.add(ImmutableVirusInterpreterEntry.builder()
                        .reported(bool(virus, "reported"))
                        .name(string(virus, "name"))
                        .qcStatus(VirusQCStatus.valueOf(string(virus, "qcStatus")))
                        .interpretation(toVirusInterpretation(nullableString(virus, "interpretation")))
                        .integrations(integer(virus, "integrations"))
                        .driverLikelihood(VirusDriverLikelihood.valueOf(string(virus, "virusDriverLikelihoodType")))
                        .percentageCovered(number(virus, "percentageCovered"))
                        .build());
            }
            return entries;
        }

        @Nullable
        private static VirusInterpretation toVirusInterpretation(@Nullable String interpretation) {
            return interpretation != null ? VirusInterpretation.valueOf(interpretation) : null;
        }

        @NotNull
        private static MolecularTissueOriginReporting toMolecularTissueOriginReporting(@NotNull JsonObject molecularTissueOriginReportingObject) {
            return ImmutableMolecularTissueOriginReporting.builder()
                    .bestCancerType(string(molecularTissueOriginReportingObject, "bestCancerType"))
                    .bestLikelihood(number(molecularTissueOriginReportingObject, "bestLikelihood"))
                    .interpretCancerType(string(molecularTissueOriginReportingObject, "interpretCancerType"))
                    .interpretLikelihood(number(molecularTissueOriginReportingObject, "interpretLikelihood"))
                    .build();
        }

        @NotNull
        private static Map<String, List<PeachEntry>> toPgx(@NotNull JsonObject pgxObject) {
            Map<String, List<PeachEntry>> mapPgx = Maps.newHashMap();

            for (Map.Entry<String, JsonElement> entry : pgxObject.entrySet()) {
                mapPgx.put(entry.getKey(), toPgxList(entry.getValue().getAsJsonArray()));
            }
            return mapPgx;
        }

        @NotNull
        private static List<PeachEntry> toPgxList(@NotNull JsonArray pgxArray) {
            List<PeachEntry> entries = Lists.newArrayList();
            for (JsonElement element : pgxArray) {
                JsonObject pgx = element.getAsJsonObject();
                entries.add(ImmutablePeachEntry.builder()
                        .gene(string(pgx, "gene"))
                        .haplotype(string(pgx, "haplotype"))
                        .function(string(pgx, "function"))
                        .linkedDrugs(string(pgx, "linkedDrugs"))
                        .urlPrescriptionInfo(string(pgx, "urlPrescriptionInfo"))
                        .panelVersion(string(pgx, "panelVersion"))
                        .repoVersion(string(pgx, "repoVersion"))
                        .build());
            }
            return entries;
        }

        @NotNull
        private static HlaAllelesReportingData toHla(@NotNull JsonObject hlaObject) {
            return ImmutableHlaAllelesReportingData.builder()
                    .hlaAllelesReporting(toHlaMap(hlaObject))
                    .hlaQC(string(hlaObject, "hlaQC"))
                    .build();
        }

        @NotNull
        private static Map<String, List<HlaReporting>> toHlaMap(@NotNull JsonObject hlaObject) {
            Map<String, List<HlaReporting>> mapPgx = Maps.newHashMap();

            for (Map.Entry<String, JsonElement> entry : hlaObject.getAsJsonObject("hlaAllelesReporting").entrySet()) {
                mapPgx.put(entry.getKey(), toHlaList(entry.getValue().getAsJsonArray()));
            }


            return mapPgx;
        }

        @NotNull
        private static List<HlaReporting> toHlaList(@NotNull JsonArray hlaArray) {
            List<HlaReporting> entries = Lists.newArrayList();

            for (JsonElement element : hlaArray) {
                JsonObject hla = element.getAsJsonObject();
                entries.add(ImmutableHlaReporting.builder()
                        .hlaAllele(toHlaAllele(object(hla, "hlaAllele")))
                        .germlineCopies(number(hla, "germlineCopies"))
                        .tumorCopies(number(hla, "tumorCopies"))
                        .somaticMutations(string(hla, "somaticMutations"))
                        .interpretation(string(hla, "interpretation"))
                        .build());
            }

            return entries;
        }

        @NotNull
        private static HlaAllele toHlaAllele(@NotNull JsonObject hlaAlleleObject) {
            return ImmutableHlaAllele.builder()
                    .gene(string(hlaAlleleObject, "gene"))
                    .germlineAllele(string(hlaAlleleObject, "germlineAllele"))
                    .build();
        }
    }
}