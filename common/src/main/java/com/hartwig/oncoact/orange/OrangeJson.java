package com.hartwig.oncoact.orange;

import static com.hartwig.oncoact.json.Json.array;
import static com.hartwig.oncoact.json.Json.bool;
import static com.hartwig.oncoact.json.Json.date;
import static com.hartwig.oncoact.json.Json.integer;
import static com.hartwig.oncoact.json.Json.nullableArray;
import static com.hartwig.oncoact.json.Json.nullableInteger;
import static com.hartwig.oncoact.json.Json.nullableIntegerList;
import static com.hartwig.oncoact.json.Json.nullableString;
import static com.hartwig.oncoact.json.Json.number;
import static com.hartwig.oncoact.json.Json.object;
import static com.hartwig.oncoact.json.Json.string;
import static com.hartwig.oncoact.json.Json.stringList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hartwig.oncoact.orange.chord.ChordRecord;
import com.hartwig.oncoact.orange.chord.ChordStatus;
import com.hartwig.oncoact.orange.chord.ImmutableChordRecord;
import com.hartwig.oncoact.orange.cuppa.CuppaPrediction;
import com.hartwig.oncoact.orange.cuppa.CuppaRecord;
import com.hartwig.oncoact.orange.cuppa.ImmutableCuppaPrediction;
import com.hartwig.oncoact.orange.cuppa.ImmutableCuppaRecord;
import com.hartwig.oncoact.orange.lilac.ImmutableLilacHlaAllele;
import com.hartwig.oncoact.orange.lilac.ImmutableLilacRecord;
import com.hartwig.oncoact.orange.lilac.LilacHlaAllele;
import com.hartwig.oncoact.orange.lilac.LilacRecord;
import com.hartwig.oncoact.orange.linx.ImmutableLinxBreakend;
import com.hartwig.oncoact.orange.linx.ImmutableLinxFusion;
import com.hartwig.oncoact.orange.linx.ImmutableLinxHomozygousDisruption;
import com.hartwig.oncoact.orange.linx.ImmutableLinxRecord;
import com.hartwig.oncoact.orange.linx.ImmutableLinxStructuralVariant;
import com.hartwig.oncoact.orange.linx.LinxBreakend;
import com.hartwig.oncoact.orange.linx.LinxBreakendType;
import com.hartwig.oncoact.orange.linx.LinxCodingType;
import com.hartwig.oncoact.orange.linx.LinxFusion;
import com.hartwig.oncoact.orange.linx.LinxFusionDriverLikelihood;
import com.hartwig.oncoact.orange.linx.LinxFusionType;
import com.hartwig.oncoact.orange.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.orange.linx.LinxPhasedType;
import com.hartwig.oncoact.orange.linx.LinxRecord;
import com.hartwig.oncoact.orange.linx.LinxRegionType;
import com.hartwig.oncoact.orange.linx.LinxStructuralVariant;
import com.hartwig.oncoact.orange.peach.ImmutablePeachEntry;
import com.hartwig.oncoact.orange.peach.ImmutablePeachRecord;
import com.hartwig.oncoact.orange.peach.PeachEntry;
import com.hartwig.oncoact.orange.peach.PeachRecord;
import com.hartwig.oncoact.orange.plots.ImmutableOrangePlots;
import com.hartwig.oncoact.orange.plots.OrangePlots;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleAllelicDepth;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleCharacteristics;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleCopyNumber;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleDriver;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleFit;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleGainLoss;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleGeneCopyNumber;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleRecord;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleTranscriptImpact;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleVariant;
import com.hartwig.oncoact.orange.purple.PurpleAllelicDepth;
import com.hartwig.oncoact.orange.purple.PurpleCharacteristics;
import com.hartwig.oncoact.orange.purple.PurpleCodingEffect;
import com.hartwig.oncoact.orange.purple.PurpleCopyNumber;
import com.hartwig.oncoact.orange.purple.PurpleDriver;
import com.hartwig.oncoact.orange.purple.PurpleDriverType;
import com.hartwig.oncoact.orange.purple.PurpleFit;
import com.hartwig.oncoact.orange.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.purple.PurpleGainLossInterpretation;
import com.hartwig.oncoact.orange.purple.PurpleGeneCopyNumber;
import com.hartwig.oncoact.orange.purple.PurpleGenotypeStatus;
import com.hartwig.oncoact.orange.purple.PurpleHotspotType;
import com.hartwig.oncoact.orange.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.orange.purple.PurpleQCStatus;
import com.hartwig.oncoact.orange.purple.PurpleRecord;
import com.hartwig.oncoact.orange.purple.PurpleTranscriptImpact;
import com.hartwig.oncoact.orange.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.orange.purple.PurpleVariant;
import com.hartwig.oncoact.orange.purple.PurpleVariantEffect;
import com.hartwig.oncoact.orange.purple.PurpleVariantType;
import com.hartwig.oncoact.orange.virus.ImmutableVirusInterpreterEntry;
import com.hartwig.oncoact.orange.virus.ImmutableVirusInterpreterRecord;
import com.hartwig.oncoact.orange.virus.VirusDriverLikelihood;
import com.hartwig.oncoact.orange.virus.VirusInterpretation;
import com.hartwig.oncoact.orange.virus.VirusInterpreterEntry;
import com.hartwig.oncoact.orange.virus.VirusInterpreterRecord;
import com.hartwig.oncoact.orange.virus.VirusQCStatus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OrangeJson {

    private OrangeJson() {
    }

    @NotNull
    public static OrangeRecord read(@NotNull String orangeJson) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(OrangeRecord.class, new OrangeRecordCreator()).create();

        Path orangePath = new File(orangeJson).toPath();
        OrangeRecord orange = gson.fromJson(Files.readString(orangePath), OrangeRecord.class);
        return fixPlotPaths(orange, orangePath.getParent().toString());
    }

    @NotNull
    private static OrangeRecord fixPlotPaths(@NotNull OrangeRecord orange, @NotNull String orangeBasePath) {
        // All ORANGE plots are relative to the base path of ORANGE JSON.
        OrangePlots fixedPlots = ImmutableOrangePlots.builder()
                .purpleFinalCircosPlot(orangeBasePath + File.separator + orange.plots().purpleFinalCircosPlot())
                .build();
        return ImmutableOrangeRecord.builder().from(orange).plots(fixedPlots).build();
    }

    private static class OrangeRecordCreator implements JsonDeserializer<OrangeRecord> {

        @Override
        public OrangeRecord deserialize(@NotNull JsonElement jsonElement, @NotNull Type type,
                @NotNull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject record = jsonElement.getAsJsonObject();

            return ImmutableOrangeRecord.builder()
                    .sampleId(string(record, "sampleId"))
                    .experimentDate(date(record, "experimentDate"))
                    .refGenomeVersion(OrangeRefGenomeVersion.valueOf(string(record, "refGenomeVersion")))
                    .purple(toPurpleRecord(object(record, "purple")))
                    .linx(toLinxRecord(object(record, "linx")))
                    .peach(toPeachRecord(array(record, "peach")))
                    .cuppa(toCuppaRecord(object(record, "cuppa")))
                    .virusInterpreter(toVirusInterpreterRecord(object(record, "virusInterpreter")))
                    .lilac(toLilacRecord(object(record, "lilac")))
                    .chord(toChordRecord(object(record, "chord")))
                    .plots(toOrangePlots(object(record, "plots")))
                    .build();
        }

        @NotNull
        private static PurpleRecord toPurpleRecord(@NotNull JsonObject purple) {
            return ImmutablePurpleRecord.builder()
                    .fit(toPurpleFit(object(purple, "fit")))
                    .characteristics(toPurpleCharacteristics(object(purple, "characteristics")))
                    .somaticDrivers(toPurpleDrivers(array(purple, "somaticDrivers")))
                    .germlineDrivers(toPurpleDrivers(nullableArray(purple, "germlineDrivers")))
                    .allSomaticVariants(toPurpleVariants(array(purple, "allSomaticVariants")))
                    .reportableSomaticVariants(toPurpleVariants(array(purple, "reportableSomaticVariants")))
                    .allGermlineVariants(toPurpleVariants(nullableArray(purple, "allGermlineVariants")))
                    .reportableGermlineVariants(toPurpleVariants(nullableArray(purple, "reportableGermlineVariants")))
                    .allSomaticCopyNumbers(toPurpleCopyNumbers(array(purple, "allSomaticCopyNumbers")))
                    .allSomaticGeneCopyNumbers(toPurpleGeneCopyNumbers(array(purple, "allSomaticGeneCopyNumbers")))
                    .allSomaticGainsLosses(toPurpleGainsLosses(array(purple, "allSomaticGainsLosses")))
                    .reportableSomaticGainsLosses(toPurpleGainsLosses(array(purple, "reportableSomaticGainsLosses")))
                    .build();
        }

        @NotNull
        private static PurpleFit toPurpleFit(@NotNull JsonObject fit) {
            JsonObject qc = object(fit, "qc");

            return ImmutablePurpleFit.builder()
                    .qcStatus(toPurpleQCStatus(stringList(qc, "status")))
                    .hasSufficientQuality(bool(fit, "hasSufficientQuality"))
                    .containsTumorCells(bool(fit, "containsTumorCells"))
                    .purity(number(fit, "purity"))
                    .ploidy(number(fit, "ploidy"))
                    .build();
        }

        @NotNull
        private static Set<PurpleQCStatus> toPurpleQCStatus(@NotNull List<String> statusEntries) {
            Set<PurpleQCStatus> status = Sets.newHashSet();
            for (String statusEntry : statusEntries) {
                status.add(PurpleQCStatus.valueOf(statusEntry));
            }
            return status;
        }

        @NotNull
        private static PurpleCharacteristics toPurpleCharacteristics(@NotNull JsonObject characteristics) {
            return ImmutablePurpleCharacteristics.builder()
                    .microsatelliteIndelsPerMb(number(characteristics, "microsatelliteIndelsPerMb"))
                    .microsatelliteStatus(PurpleMicrosatelliteStatus.valueOf(string(characteristics, "microsatelliteStatus")))
                    .tumorMutationalBurdenPerMb(number(characteristics, "tumorMutationalBurdenPerMb"))
                    .tumorMutationalBurdenStatus(PurpleTumorMutationalStatus.valueOf(string(characteristics,
                            "tumorMutationalBurdenStatus")))
                    .tumorMutationalLoad(integer(characteristics, "tumorMutationalLoad"))
                    .tumorMutationalLoadStatus(PurpleTumorMutationalStatus.valueOf(string(characteristics, "tumorMutationalLoadStatus")))
                    .build();
        }

        @NotNull
        private static Set<PurpleDriver> toPurpleDrivers(@Nullable JsonArray driverArray) {
            if (driverArray == null) {
                return Sets.newHashSet();
            }

            Set<PurpleDriver> drivers = Sets.newHashSet();
            for (JsonElement element : driverArray) {
                JsonObject driver = element.getAsJsonObject();
                drivers.add(ImmutablePurpleDriver.builder()
                        .gene(string(driver, "gene"))
                        .transcript(string(driver, "transcript"))
                        .type(PurpleDriverType.valueOf(string(driver, "driver")))
                        .driverLikelihood(number(driver, "driverLikelihood"))
                        .build());
            }
            return drivers;
        }

        @NotNull
        private static Set<PurpleVariant> toPurpleVariants(@Nullable JsonArray variantArray) {
            if (variantArray == null) {
                return Sets.newHashSet();
            }

            Set<PurpleVariant> variants = Sets.newHashSet();
            for (JsonElement element : variantArray) {
                JsonObject variant = element.getAsJsonObject();
                variants.add(ImmutablePurpleVariant.builder()
                        .reported(bool(variant, "reported"))
                        .type(PurpleVariantType.valueOf(string(variant, "type")))
                        .gene(string(variant, "gene"))
                        .chromosome(string(variant, "chromosome"))
                        .position(integer(variant, "position"))
                        .ref(string(variant, "ref"))
                        .alt(string(variant, "alt"))
                        .adjustedCopyNumber(number(variant, "adjustedCopyNumber"))
                        .minorAlleleCopyNumber(number(variant, "minorAlleleCopyNumber"))
                        .variantCopyNumber(number(variant, "variantCopyNumber"))
                        .hotspot(PurpleHotspotType.valueOf(string(variant, "hotspot")))
                        .tumorDepth(toPurpleAllelicDepth(object(variant, "tumorDepth")))
                        .subclonalLikelihood(number(variant, "subclonalLikelihood"))
                        .biallelic(bool(variant, "biallelic"))
                        .genotypeStatus(PurpleGenotypeStatus.valueOf(string(variant, "genotypeStatus")))
                        .localPhaseSets(nullableIntegerList(variant, "localPhaseSets"))
                        .canonicalImpact(toPurpleTranscriptImpact(object(variant, "canonicalImpact")))
                        .otherImpacts(toPurpleTranscriptImpacts(array(variant, "otherImpacts")))
                        .build());
            }
            return variants;
        }

        @NotNull
        private static PurpleAllelicDepth toPurpleAllelicDepth(@NotNull JsonObject depth) {
            return ImmutablePurpleAllelicDepth.builder()
                    .totalReadCount(integer(depth, "totalReadCount"))
                    .alleleReadCount(integer(depth, "alleleReadCount"))
                    .build();
        }

        @NotNull
        private static Set<PurpleTranscriptImpact> toPurpleTranscriptImpacts(@NotNull JsonArray impactArray) {
            Set<PurpleTranscriptImpact> impacts = Sets.newHashSet();
            for (JsonElement element : impactArray) {
                impacts.add(toPurpleTranscriptImpact(element.getAsJsonObject()));
            }
            return impacts;
        }

        @NotNull
        private static PurpleTranscriptImpact toPurpleTranscriptImpact(@NotNull JsonObject impact) {
            return ImmutablePurpleTranscriptImpact.builder()
                    .transcript(string(impact, "transcript"))
                    .hgvsCodingImpact(string(impact, "hgvsCodingImpact"))
                    .hgvsProteinImpact(string(impact, "hgvsProteinImpact"))
                    .affectedCodon(nullableInteger(impact, "affectedCodon"))
                    .affectedExon(nullableInteger(impact, "affectedExon"))
                    .spliceRegion(bool(impact, "spliceRegion"))
                    .codingEffect(PurpleCodingEffect.valueOf(string(impact, "codingEffect")))
                    .effects(toPurpleVariantEffects(array(impact, "effects")))
                    .build();
        }

        @NotNull
        private static Set<PurpleVariantEffect> toPurpleVariantEffects(@NotNull JsonArray effectArray) {
            Set<PurpleVariantEffect> effects = Sets.newHashSet();
            for (JsonElement element : effectArray) {
                effects.add(PurpleVariantEffect.valueOf(element.getAsString()));
            }
            return effects;
        }

        @NotNull
        private static Set<PurpleCopyNumber> toPurpleCopyNumbers(@NotNull JsonArray copyNumberArray) {
            Set<PurpleCopyNumber> copyNumbers = Sets.newHashSet();
            for (JsonElement element : copyNumberArray) {
                JsonObject copyNumber = element.getAsJsonObject();
                copyNumbers.add(ImmutablePurpleCopyNumber.builder()
                        .chromosome(string(copyNumber, "chromosome"))
                        .start(integer(copyNumber, "start"))
                        .end(integer(copyNumber, "end"))
                        .averageTumorCopyNumber(number(copyNumber, "averageTumorCopyNumber"))
                        .build());
            }
            return copyNumbers;
        }

        @NotNull
        private static Set<PurpleGeneCopyNumber> toPurpleGeneCopyNumbers(@NotNull JsonArray geneCopyNumberArray) {
            Set<PurpleGeneCopyNumber> geneCopyNumbers = Sets.newHashSet();
            for (JsonElement element : geneCopyNumberArray) {
                JsonObject geneCopyNumber = element.getAsJsonObject();
                geneCopyNumbers.add(ImmutablePurpleGeneCopyNumber.builder()
                        .chromosome(string(geneCopyNumber, "chromosome"))
                        .chromosomeBand(string(geneCopyNumber, "chromosomeBand"))
                        .gene(string(geneCopyNumber, "geneName"))
                        .minCopyNumber(number(geneCopyNumber, "minCopyNumber"))
                        .minMinorAlleleCopyNumber(number(geneCopyNumber, "minMinorAlleleCopyNumber"))
                        .build());
            }
            return geneCopyNumbers;
        }

        @NotNull
        private static Set<PurpleGainLoss> toPurpleGainsLosses(@NotNull JsonArray gainLossArray) {
            Set<PurpleGainLoss> gainsLosses = Sets.newHashSet();
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
        private static LinxRecord toLinxRecord(@NotNull JsonObject linx) {
            return ImmutableLinxRecord.builder()
                    .structuralVariants(toLinxStructuralVariants(array(linx, "allStructuralVariants")))
                    .homozygousDisruptions(toLinxHomozygousDisruptions(array(linx, "homozygousDisruptions")))
                    .allBreakends(toLinxBreakends(array(linx, "allBreakends")))
                    .reportableBreakends(toLinxBreakends(array(linx, "reportableBreakends")))
                    .allFusions(toLinxFusions(array(linx, "allFusions")))
                    .reportableFusions(toLinxFusions(array(linx, "reportableFusions")))
                    .build();
        }

        @NotNull
        private static Set<LinxStructuralVariant> toLinxStructuralVariants(@NotNull JsonArray structuralVariantArray) {
            Set<LinxStructuralVariant> structuralVariants = Sets.newHashSet();
            for (JsonElement element : structuralVariantArray) {
                JsonObject structuralVariant = element.getAsJsonObject();
                structuralVariants.add(ImmutableLinxStructuralVariant.builder()
                        .svId(integer(structuralVariant, "svId"))
                        .clusterId(integer(structuralVariant, "clusterId"))
                        .build());
            }
            return structuralVariants;
        }

        @NotNull
        private static Set<LinxHomozygousDisruption> toLinxHomozygousDisruptions(@NotNull JsonArray homozygousDisruptionArray) {
            Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet();
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
        private static Set<LinxBreakend> toLinxBreakends(@NotNull JsonArray breakendArray) {
            Set<LinxBreakend> breakends = Sets.newHashSet();
            for (JsonElement element : breakendArray) {
                JsonObject breakend = element.getAsJsonObject();
                breakends.add(ImmutableLinxBreakend.builder()
                        .reported(bool(breakend, "reportedDisruption"))
                        .disruptive(bool(breakend, "disruptive"))
                        .svId(integer(breakend, "svId"))
                        .gene(string(breakend, "gene"))
                        .chromosome(string(breakend, "chromosome"))
                        .chrBand(string(breakend, "chrBand"))
                        .transcriptId(string(breakend, "transcriptId"))
                        .canonical(bool(breakend, "canonical"))
                        .type(LinxBreakendType.valueOf(string(breakend, "type")))
                        .junctionCopyNumber(number(breakend, "junctionCopyNumber"))
                        .undisruptedCopyNumber(number(breakend, "undisruptedCopyNumber"))
                        .nextSpliceExonRank(integer(breakend, "nextSpliceExonRank"))
                        .exonUp(integer(breakend, "exonUp"))
                        .exonDown(integer(breakend, "exonDown"))
                        .geneOrientation(string(breakend, "geneOrientation"))
                        .orientation(integer(breakend, "orientation"))
                        .strand(integer(breakend, "strand"))
                        .regionType(LinxRegionType.valueOf(string(breakend, "regionType")))
                        .codingType(LinxCodingType.valueOf(string(breakend, "codingType")))
                        .build());
            }
            return breakends;
        }

        @NotNull
        private static Set<LinxFusion> toLinxFusions(@NotNull JsonArray fusionArray) {
            Set<LinxFusion> fusions = Sets.newHashSet();
            for (JsonElement element : fusionArray) {
                JsonObject fusion = element.getAsJsonObject();
                fusions.add(ImmutableLinxFusion.builder()
                        .reported(bool(fusion, "reported"))
                        .type(LinxFusionType.valueOf(string(fusion, "reportedType")))
                        .name(string(fusion, "name"))
                        .geneStart(string(fusion, "geneStart"))
                        .geneTranscriptStart(string(fusion, "geneTranscriptStart"))
                        .geneContextStart(string(fusion, "geneContextStart"))
                        .fusedExonUp(integer(fusion, "fusedExonUp"))
                        .geneEnd(string(fusion, "geneEnd"))
                        .geneTranscriptEnd(string(fusion, "geneTranscriptEnd"))
                        .geneContextEnd(string(fusion, "geneContextEnd"))
                        .fusedExonDown(integer(fusion, "fusedExonDown"))
                        .driverLikelihood(LinxFusionDriverLikelihood.valueOf(string(fusion, "likelihood")))
                        .phased(LinxPhasedType.valueOf(string(fusion, "phased")))
                        .junctionCopyNumber(number(fusion, "junctionCopyNumber"))
                        .build());
            }
            return fusions;
        }

        @NotNull
        private static PeachRecord toPeachRecord(@NotNull JsonArray peachArray) {
            Set<PeachEntry> entries = Sets.newHashSet();
            for (JsonElement element : peachArray) {
                JsonObject peach = element.getAsJsonObject();
                entries.add(ImmutablePeachEntry.builder()
                        .gene(string(peach, "gene"))
                        .haplotype(string(peach, "haplotype"))
                        .function(string(peach, "function"))
                        .linkedDrugs(string(peach, "linkedDrugs"))
                        .urlPrescriptionInfo(string(peach, "urlPrescriptionInfo"))
                        .panelVersion(string(peach, "panelVersion"))
                        .repoVersion(string(peach, "repoVersion"))
                        .build());
            }
            return ImmutablePeachRecord.builder().entries(entries).build();
        }

        @NotNull
        private static CuppaRecord toCuppaRecord(@NotNull JsonObject cuppa) {
            Set<CuppaPrediction> predictions = Sets.newHashSet();
            for (JsonElement element : array(cuppa, "predictions")) {
                JsonObject prediction = element.getAsJsonObject();
                predictions.add(ImmutableCuppaPrediction.builder()
                        .cancerType(string(prediction, "cancerType"))
                        .likelihood(number(prediction, "likelihood"))
                        .build());
            }
            return ImmutableCuppaRecord.builder().predictions(predictions).build();
        }

        @NotNull
        private static VirusInterpreterRecord toVirusInterpreterRecord(@NotNull JsonObject virusInterpreter) {
            return ImmutableVirusInterpreterRecord.builder()
                    .allViruses(toVirusInterpreterEntries(array(virusInterpreter, "allViruses")))
                    .reportableViruses(toVirusInterpreterEntries(array(virusInterpreter, "reportableViruses")))
                    .build();
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
        private static LilacRecord toLilacRecord(@NotNull JsonObject lilac) {
            Set<LilacHlaAllele> alleles = Sets.newHashSet();
            for (JsonElement element : array(lilac, "alleles")) {
                JsonObject allele = element.getAsJsonObject();
                alleles.add(ImmutableLilacHlaAllele.builder()
                        .allele(string(allele, "allele"))
                        .tumorCopyNumber(number(allele, "tumorCopyNumber"))
                        .somaticMissense(number(allele, "somaticMissense"))
                        .somaticNonsenseOrFrameshift(number(allele, "somaticNonsenseOrFrameshift"))
                        .somaticSplice(number(allele, "somaticSplice"))
                        .somaticSynonymous(number(allele, "somaticSynonymous"))
                        .somaticInframeIndel(number(allele, "somaticInframeIndel"))
                        .build());
            }

            return ImmutableLilacRecord.builder().qc(string(lilac, "qc")).alleles(alleles).build();
        }

        @NotNull
        private static ChordRecord toChordRecord(@NotNull JsonObject chord) {
            return ImmutableChordRecord.builder()
                    .hrdValue(number(chord, "hrdValue"))
                    .hrStatus(ChordStatus.valueOf(string(chord, "hrStatus")))
                    .build();
        }

        @NotNull
        private static OrangePlots toOrangePlots(@NotNull JsonObject plots) {
            return ImmutableOrangePlots.builder().purpleFinalCircosPlot(string(plots, "purpleFinalCircosPlot")).build();
        }
    }
}
