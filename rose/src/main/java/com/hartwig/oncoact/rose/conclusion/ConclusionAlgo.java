package com.hartwig.oncoact.rose.conclusion;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.chord.ChordRecord;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.hmftools.datamodel.cuppa.CuppaPrediction;
import com.hartwig.hmftools.datamodel.cuppa.ImmutableCuppaPrediction;
import com.hartwig.hmftools.datamodel.hla.LilacAllele;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxFusionType;
import com.hartwig.hmftools.datamodel.purple.*;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.hmftools.datamodel.virus.VirusInterpreterData;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;
import com.hartwig.oncoact.drivergene.DriverCategory;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.rose.ActionabilityConclusion;
import com.hartwig.oncoact.rose.ImmutableActionabilityConclusion;
import com.hartwig.oncoact.rose.RoseData;
import com.hartwig.oncoact.rose.actionability.*;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.DriverInterpretation;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public final class ConclusionAlgo {

    private static final Logger LOGGER = LogManager.getLogger(ConclusionAlgo.class);

    private static final Set<LinxFusionType> FUSION_TYPES = Sets.newHashSet(LinxFusionType.PROMISCUOUS_3,
            LinxFusionType.PROMISCUOUS_5,
            LinxFusionType.KNOWN_PAIR,
            LinxFusionType.IG_KNOWN_PAIR,
            LinxFusionType.IG_PROMISCUOUS);
    private static final Set<String> HRD_GENES = Sets.newHashSet("BRCA1", "BRCA2", "PALB2", "RAD51B", "RAD51C");

    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = decimalFormat("#.##");
    private static final double TMB_CUTOFF = 10;
    private static final double PURITY_CUTOFF = 0.195;

    private ConclusionAlgo() {
    }

    @NotNull
    public static ActionabilityConclusion generateConclusion(@NotNull RoseData rose) {
        Set<String> conclusion = Sets.newHashSet();
        Set<String> oncogenic = Sets.newHashSet();
        Set<String> actionable = Sets.newHashSet();
        Set<String> HRD = Sets.newHashSet();

        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = generateActionabilityMap(rose.actionabilityEntries());
        Map<String, DriverGene> driverGenesMap = generateDriverGenesMap(rose.driverGenes());

        PurpleRecord purple = rose.orange().purple();
        Set<ReportableVariant> reportableSomaticVariants = createReportableSomaticVariants(purple);
        Set<ReportableVariant> reportableGermlineVariants = createReportableGermlineVariants(purple);
        List<ReportableVariant> reportableVariants =
                ReportableVariantFactory.mergeVariantLists(reportableGermlineVariants, reportableSomaticVariants);

        List<PurpleGainLoss> reportableGainLosses = purple.reportableSomaticGainsLosses();

        List<LinxFusion> reportableFusions = rose.orange().linx().reportableSomaticFusions();
        List<HomozygousDisruption> homozygousDisruptions = rose.orange().linx().somaticHomozygousDisruptions();
        List<AnnotatedVirus> reportableViruses = Optional.ofNullable(rose.orange().virusInterpreter())
                .map(VirusInterpreterData::reportableViruses)
                .orElseGet(List::of);
        List<LilacAllele> lilac = rose.orange().lilac().alleles();

        CuppaPrediction bestPrediction = bestPrediction(rose.orange().cuppa());

        generatePurityConclusion(conclusion, purple.fit().purity(), purple.fit().containsTumorCells(), actionabilityMap);
        generateTechnique(conclusion);
        generatePlatform(conclusion);
        generateStartSentence(conclusion);
        generateCUPPAConclusion(conclusion, bestPrediction, actionabilityMap);
        generateVariantConclusion(conclusion, reportableVariants, actionabilityMap,
                driverGenesMap,
                oncogenic,
                actionable,
                HRD,
                rose.orange().chord());
        generateCNVConclusion(conclusion, reportableGainLosses, actionabilityMap, oncogenic, actionable);
        generateFusionConclusion(conclusion, reportableFusions, actionabilityMap, oncogenic, actionable);
        generateHomozygousDisruptionConclusion(conclusion, homozygousDisruptions, actionabilityMap, oncogenic, actionable);
        generateVirusHLAConclusion(conclusion, reportableViruses, lilac, actionabilityMap, oncogenic, actionable);
        generateHrdConclusion(conclusion, rose.orange().chord(), actionabilityMap, oncogenic, actionable, HRD);
        generateMSIConclusion(conclusion,
                purple.characteristics().microsatelliteStatus(),
                purple.characteristics().microsatelliteIndelsPerMb(),
                actionabilityMap,
                oncogenic,
                actionable);
        generateTMLConclusion(conclusion,
                purple.characteristics().tumorMutationalLoadStatus(),
                purple.characteristics().tumorMutationalLoad(),
                actionabilityMap,
                oncogenic,
                actionable);
        generateTMBConclusion(conclusion, purple.characteristics().tumorMutationalBurdenPerMb(), actionabilityMap, oncogenic, actionable);
        generateTotalResults(conclusion, actionabilityMap, oncogenic, actionable);

        return ImmutableActionabilityConclusion.builder().conclusion(conclusion).build();
    }

    @NotNull
    private static Map<ActionabilityKey, ActionabilityEntry> generateActionabilityMap(@NotNull List<ActionabilityEntry> actionabilityDB) {
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = Maps.newHashMap();
        for (ActionabilityEntry entry : actionabilityDB) {
            ActionabilityKey key = ImmutableActionabilityKey.builder().match(entry.match()).type(entry.type()).build();
            actionabilityMap.put(key, entry);
        }
        return actionabilityMap;
    }

    @NotNull
    private static Map<String, DriverGene> generateDriverGenesMap(@NotNull List<DriverGene> driverGenes) {
        Map<String, DriverGene> driverGeneMap = Maps.newHashMap();
        for (DriverGene entry : driverGenes) {
            driverGeneMap.put(entry.gene(), entry);
        }
        return driverGeneMap;
    }

    @NotNull
    private static Set<ReportableVariant> createReportableSomaticVariants(@NotNull PurpleRecord purple) {
        return ReportableVariantFactory.toReportableSomaticVariants(purple.reportableSomaticVariants(), purple.somaticDrivers());
    }

    @NotNull
    private static Set<ReportableVariant> createReportableGermlineVariants(@NotNull PurpleRecord purple) {
        Collection<PurpleVariant> reportableGermlineVariants = purple.reportableGermlineVariants();
        if (reportableGermlineVariants == null) {
            return Sets.newHashSet();
        }

        return ReportableVariantFactory.toReportableGermlineVariants(reportableGermlineVariants, purple.germlineDrivers());
    }

    @NotNull
    private static CuppaPrediction bestPrediction(@NotNull CuppaData cuppa) {
        CuppaPrediction best = null;
        for (CuppaPrediction prediction : cuppa.predictions()) {
            if (best == null || prediction.likelihood() > best.likelihood()) {
                best = prediction;
            }
        }

        if (best == null) {
            LOGGER.warn("No best CUPPA prediction found");
            return ImmutableCuppaPrediction.builder().cancerType("Unknown").likelihood(0D).build();
        }

        return best;
    }

    private static void generateTechnique(@NotNull Set<String> conclusion) {
        conclusion.add("Technique: WGS");
    }

    private static void generatePlatform(@NotNull Set<String> conclusion) {
        conclusion.add("Platform: HiSeqX (Illumina) WGS analysis, processed using Hartwig Medical" +
                " OncoAct® software and reporting (https://www.oncoact.nl/specsheetOncoActWGS).All activities are " +
                "performed under ISO17025 accreditation (RVA, L633).");
    }

    private static void generateStartSentence(@NotNull Set<String> conclusion) {
        conclusion.add("Sample showing: ");
    }

    @VisibleForTesting
    static void generateCUPPAConclusion(@NotNull Set<String> conclusion, @NotNull CuppaPrediction bestPrediction,
                                        @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap) {
        String likelihoodPercentage = Formats.formatPercentageDigit(bestPrediction.likelihood());
        if (bestPrediction.likelihood() < 0.8) {
            ActionabilityKey keyCuppaInconclusive =
                    ImmutableActionabilityKey.builder().match("CUPPA_INCONCLUSIVE").type(TypeAlteration.CUPPA_INCONCLUSIVE).build();

            ActionabilityEntry entry = actionabilityMap.get(keyCuppaInconclusive);
            if (entry != null && entry.condition() == Condition.OTHER) {
                if (bestPrediction.likelihood() >= 0.5) {
                    conclusion.add(
                            "- " + entry.conclusion().replace("xxx - xx%", bestPrediction.cancerType() + "-" + likelihoodPercentage));
                } else {
                    conclusion.add("- " + entry.conclusion().replace(" (highest likelihood: xxx - xx%)", ""));
                }

            }
        } else {
            ActionabilityKey keyCuppa = ImmutableActionabilityKey.builder().match("CUPPA").type(TypeAlteration.CUPPA).build();

            ActionabilityEntry entry = actionabilityMap.get(keyCuppa);
            if (entry != null && entry.condition() == Condition.OTHER) {
                conclusion.add("- " + entry.conclusion()
                        .replace("XXXX", bestPrediction.cancerType() + " (likelihood: " + likelihoodPercentage + ")"));
            }
        }
    }

    @VisibleForTesting
    static void generateVariantConclusion(@NotNull Set<String> conclusion, @NotNull List<ReportableVariant> reportableVariants,
                                          @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Map<String, DriverGene> driverGenesMap,
                                          @NotNull Set<String> oncogenic, @NotNull Set<String> actionable, @NotNull Set<String> HRD, @NotNull ChordRecord chord) {
        Map<String, Set<VariantKey>> variantKeyList = Maps.newHashMap();

        for (ReportableVariant reportableVariant : reportableVariants) {
            VariantKey variantKey = ImmutableVariantKey.builder()
                    .gene(reportableVariant.gene())
                    .variantAnnotation(EventGenerator.variantEvent(reportableVariant))
                    .driverInterpretation(reportableVariant.driverLikelihoodInterpretation())
                    .biallelic(reportableVariant.biallelic())
                    .canonicalEffect(reportableVariant.canonicalEffect())
                    .build();

            if (variantKeyList.containsKey(reportableVariant.gene())) {
                Set<VariantKey> current = variantKeyList.get(reportableVariant.gene());
                current.add(variantKey);
                variantKeyList.put(reportableVariant.gene(), current);
            } else {
                variantKeyList.put(reportableVariant.gene(), Sets.newHashSet(variantKey));
            }
        }

        for (Map.Entry<String, Set<VariantKey>> keyMap : variantKeyList.entrySet()) {
            boolean isHRDGene = false;
            TypeAlteration alteration = TypeAlteration.UNKNOWN;

            StringJoiner variantMerging = new StringJoiner(", ");
            List<String> variants = Lists.newArrayList();

            for (VariantKey key : keyMap.getValue()) {
                if (HRD_GENES.contains(keyMap.getKey())) {
                    HRD.add(keyMap.getKey());
                    isHRDGene = true;
                }
                oncogenic.add("variant");

                DriverGene driverGene = driverGenesMap.get(key.gene());
                if (keyMap.getKey().equals("KRAS") && key.variantAnnotation().equals("p.Gly12Cys")) {
                    alteration = TypeAlteration.ACTIVATING_MUTATION_KRAS_G12C;
                } else if (key.canonicalEffect().equals("upstream_gene_variant")) {
                    alteration = TypeAlteration.PROMOTER_MUTATION;
                } else if (driverGene != null && driverGene.likelihoodType() == DriverCategory.ONCO) {
                    alteration = TypeAlteration.ACTIVATING_MUTATION;
                } else if (driverGene != null && driverGene.likelihoodType() == DriverCategory.TSG) {
                    alteration = TypeAlteration.INACTIVATION;
                }

                variants.add(key.variantAnnotation());
            }

            Collections.sort(variants);
            for (String variant : variants) {
                variantMerging.add(variant);
            }

            ActionabilityKey keySomaticVariant = ImmutableActionabilityKey.builder().match(keyMap.getKey()).type(alteration).build();
            ActionabilityEntry entry = actionabilityMap.get(keySomaticVariant);
            if (entry != null) {
                if ((keyMap.getValue().iterator().next().driverInterpretation() == DriverInterpretation.HIGH
                        && (entry.condition() == Condition.ONLY_HIGH || entry.condition() == Condition.HIGH_NO_ACTIONABLE))) {
                    if (entry.condition() == Condition.ONLY_HIGH) {
                        actionable.add("variant");
                    }

                    DriverGene driverGene = driverGenesMap.get(keyMap.getKey());
                    if ((driverGene != null && driverGene.likelihoodType() == DriverCategory.TSG)
                            && variantMerging.toString().split(", ").length == 1) {
                        if (!keyMap.getValue().iterator().next().biallelic()) {
                            ActionabilityKey keyBiallelic =
                                    ImmutableActionabilityKey.builder().match("NOT_BIALLELIC").type(TypeAlteration.NOT_BIALLELIC).build();
                            ActionabilityEntry entryBiallelic = actionabilityMap.get(keyBiallelic);
                            if (entryBiallelic.condition() == Condition.OTHER) {
                                String sentence = entry.conclusion();
                                conclusion.add("- " + keyMap.getKey() + " (" + variantMerging + ") " + sentence.replace("inactivation", "inactivating mutation") + " "
                                        + entryBiallelic.conclusion());
                            }
                        } else {
                            conclusion.add("- " + keyMap.getKey() + " (" + variantMerging + ") " + entry.conclusion());
                        }
                    } else {
                        conclusion.add("- " + keyMap.getKey() + " (" + variantMerging + ") " + entry.conclusion());
                    }
                } else if (isHRDGene && chord.hrStatus() == ChordStatus.HR_DEFICIENT) {
                    conclusion.add("- " + keyMap.getKey() + " (" + variantMerging + ") " + entry.conclusion());
                    actionable.add("variant");
                }
            }
        }
        LOGGER.info(conclusion);
    }

    @VisibleForTesting
    static void generateCNVConclusion(@NotNull Collection<String> conclusion, @NotNull Collection<PurpleGainLoss> reportableGainLosses,
                                      @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Set<String> oncogenic,
                                      @NotNull Set<String> actionable) {
        for (PurpleGainLoss gainLoss : reportableGainLosses) {
            oncogenic.add("CNV");

            if (gainLoss.interpretation() == CopyNumberInterpretation.FULL_LOSS
                    || gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_LOSS) {

                ActionabilityKey keyLoss = ImmutableActionabilityKey.builder().match(gainLoss.gene()).type(TypeAlteration.LOSS).build();
                ActionabilityEntry entry = actionabilityMap.get(keyLoss);

                if (entry != null && (entry.condition() == Condition.ALWAYS || entry.condition() == Condition.HIGH_NO_ACTIONABLE)) {
                    String copies = " (copies: " + (int) gainLoss.minCopies() + ")";
                    String conclusionSentence = "- " + gainLoss.gene() + copies + " " + entry.conclusion();
                    addSentenceToCNVConclusion(conclusionSentence, gainLoss.gene(), conclusion, actionable);
                }
            }

            if (gainLoss.interpretation() == CopyNumberInterpretation.FULL_GAIN
                    || gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_GAIN) {
                ActionabilityKey keyGain =
                        ImmutableActionabilityKey.builder().match(gainLoss.gene()).type(TypeAlteration.AMPLIFICATION).build();
                ActionabilityEntry entry = actionabilityMap.get(keyGain);

                if (entry != null && entry.condition() == Condition.ALWAYS) {
                    String copies = " (copies: " + (int) gainLoss.maxCopies() + ")";
                    String conclusionSentence = "- " + gainLoss.gene() + copies + " " + entry.conclusion();
                    addSentenceToCNVConclusion(conclusionSentence, gainLoss.gene(), conclusion, actionable);
                }
            }
        }
    }

    private static void addSentenceToCNVConclusion(@NotNull String conclusionSentence, @NotNull String gene,
                                                   @NotNull Collection<String> conclusion, @NotNull Collection<String> actionable) {
        if (!conclusion.contains(conclusionSentence)) {
            conclusion.add(conclusionSentence);
            actionable.add("CNV");
        }
    }

    @VisibleForTesting
    static void generateFusionConclusion(@NotNull Collection<String> conclusion, @NotNull Collection<LinxFusion> reportableFusions,
                                         @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Set<String> oncogenic,
                                         @NotNull Set<String> actionable) {
        for (LinxFusion fusion : reportableFusions) {
            oncogenic.add("fusion");

            if ((fusion.reportedType() == LinxFusionType.EXON_DEL_DUP) && fusion.geneStart().equals("EGFR") && (fusion.fusedExonUp() == 25
                    && fusion.fusedExonDown() == 14) || (fusion.fusedExonUp() == 26 && fusion.fusedExonDown() == 18)) {
                ActionabilityKey keyFusion = ImmutableActionabilityKey.builder()
                        .match(fusion.geneStart())
                        .type(TypeAlteration.KINASE_DOMAIN_DUPLICATION)
                        .build();
                ActionabilityEntry entry = actionabilityMap.get(keyFusion);
                if (entry != null && entry.condition() == Condition.ALWAYS) {
                    conclusion.add("- " + fusion.geneStart() + " - " + fusion.geneEnd() + " (" + fusion.geneContextStart() + " - "
                            + fusion.geneContextEnd() + ") " + entry.conclusion());
                    actionable.add("fusion");
                }
            } else if (fusion.reportedType() == LinxFusionType.EXON_DEL_DUP) {
                ActionabilityKey keyFusion =
                        ImmutableActionabilityKey.builder().match(fusion.geneStart()).type(TypeAlteration.INTERNAL_DELETION).build();
                ActionabilityEntry entry = actionabilityMap.get(keyFusion);
                if (entry != null && entry.condition() == Condition.ALWAYS) {
                    conclusion.add("- " + fusion.geneStart() + " - " + fusion.geneEnd() + " (" + fusion.geneContextStart() + " - "
                            + fusion.geneContextEnd() + ") " + entry.conclusion());
                    actionable.add("fusion");
                }
            } else if (FUSION_TYPES.contains(fusion.reportedType())) {
                ActionabilityKey keyFusionStart =
                        ImmutableActionabilityKey.builder().match(fusion.geneStart()).type(TypeAlteration.FUSION).build();
                ActionabilityKey keyFusionEnd =
                        ImmutableActionabilityKey.builder().match(fusion.geneEnd()).type(TypeAlteration.FUSION).build();

                ActionabilityEntry entryStart = actionabilityMap.get(keyFusionStart);
                ActionabilityEntry entryEnd = actionabilityMap.get(keyFusionEnd);

                if (entryStart != null && entryStart.condition() == Condition.ALWAYS) {
                    conclusion.add("- " + fusion.geneStart() + " - " + fusion.geneEnd() + " (" + fusion.geneContextStart() + " - "
                            + fusion.geneContextEnd() + ") " + entryStart.conclusion());
                    actionable.add("fusion");
                } else if (entryEnd != null && entryEnd.condition() == Condition.ALWAYS) {
                    conclusion.add("- " + fusion.geneStart() + " - " + fusion.geneEnd() + " (" + fusion.geneContextStart() + " - "
                            + fusion.geneContextEnd() + ") " + entryEnd.conclusion());
                    actionable.add("fusion");
                }
            }
        }
    }

    @VisibleForTesting
    static void generateHomozygousDisruptionConclusion(@NotNull Set<String> conclusion,
                                                       @NotNull Collection<HomozygousDisruption> homozygousDisruptions, @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap,
                                                       @NotNull Set<String> oncogenic, @NotNull Set<String> actionable) {

        for (HomozygousDisruption homozygousDisruption : homozygousDisruptions) {
            oncogenic.add("homozygousDisruption");

            ActionabilityKey keyHomozygousDisruption =
                    ImmutableActionabilityKey.builder().match(homozygousDisruption.gene()).type(TypeAlteration.INACTIVATION).build();
            ActionabilityEntry entry = actionabilityMap.get(keyHomozygousDisruption);
            if (entry != null && (entry.condition() == Condition.ALWAYS || entry.condition() == Condition.HIGH_NO_ACTIONABLE)) {
                conclusion.add("- " + homozygousDisruption.gene() + " " + entry.conclusion());
                actionable.add("homozygousDisruption");
            }
        }
    }

    @VisibleForTesting
    static void generateVirusHLAConclusion(@NotNull Collection<String> conclusion, @NotNull Collection<AnnotatedVirus> reportableViruses, @NotNull Collection<LilacAllele> lilac,
                                           @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Collection<String> oncogenic,
                                           @NotNull Collection<String> actionable) {
        String HlaAllele = null;
        for (LilacAllele allele : lilac) {

            if (allele.allele().equals("A*02:01")) {
                HlaAllele = allele.allele();
            }
        }

        if (reportableViruses.size() >= 1) {
            for (AnnotatedVirus virus : reportableViruses) {
                if (virus.interpretation() != null && HlaAllele != null) {
                    ActionabilityKey key = ImmutableActionabilityKey.builder()
                            .match("HPV-16 | HLA-A*02")
                            .type(TypeAlteration.POSITIVE)
                            .build();
                    ActionabilityEntry entry = actionabilityMap.get(key);

                    if (entry != null && entry.condition() == Condition.ALWAYS) {
                        if (virus.interpretation() == VirusInterpretation.HPV) {
                            oncogenic.add("HLA | virus");
                            conclusion.add("- " + HlaAllele + " " + virus.interpretation() + " " + entry.conclusion());
                        }
                    }
                } else if (virus.interpretation() != null) {
                    ActionabilityKey key = ImmutableActionabilityKey.builder()
                            .match(virus.virusDriverLikelihoodType().equals(VirusLikelihoodType.HIGH) ? virus.interpretation().toString() : Strings.EMPTY)
                            .type(TypeAlteration.POSITIVE)
                            .build();
                    ActionabilityEntry entry = actionabilityMap.get(key);
                    if (entry != null && entry.condition() == Condition.ONLY_HIGH) {
                        oncogenic.add("virus");
                        conclusion.add("- " + virus.interpretation() + " " + entry.conclusion());
                        if (virus.interpretation() != null && virus.virusDriverLikelihoodType() == VirusLikelihoodType.HIGH) {
                            actionable.add("virus");
                        }
                    }
                }
            }
        } else {
            if (HlaAllele != null) {
                ActionabilityKey key = ImmutableActionabilityKey.builder()
                        .match("HLA-A*02")
                        .type(TypeAlteration.POSITIVE)
                        .build();
                ActionabilityEntry entry = actionabilityMap.get(key);
                if (entry != null && entry.condition() == Condition.ALWAYS) {
                    oncogenic.add("hla");
                    conclusion.add("- " + HlaAllele + " " + entry.conclusion());
                }
            }
        }
    }

    @VisibleForTesting
    static void generateHrdConclusion(@NotNull Set<String> conclusion, @NotNull ChordRecord chord,
                                      @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Set<String> oncogenic,
                                      @NotNull Set<String> actionable, @NotNull Set<String> HRD) {
        if (chord.hrStatus() == ChordStatus.HR_DEFICIENT) {
            ActionabilityKey keyHRD = ImmutableActionabilityKey.builder().match("HRD").type(TypeAlteration.POSITIVE).build();
            ActionabilityEntry entry = actionabilityMap.get(keyHRD);
            if (entry != null && entry.condition() == Condition.ALWAYS) {
                if (HRD.size() == 0) {
                    ActionabilityKey keyNoHRD =
                            ImmutableActionabilityKey.builder().match("NO_HRD_CAUSE").type(TypeAlteration.NO_HRD_CAUSE).build();
                    ActionabilityEntry entryNoHRd = actionabilityMap.get(keyNoHRD);
                    if (entryNoHRd != null && entry.condition() == Condition.OTHER) {
                        conclusion.add("- " + "HRD (" + chord.hrdValue() + ") " + entry.conclusion() + entryNoHRd.conclusion());
                    }
                }
                conclusion.add("- " + "HRD (" + DOUBLE_DECIMAL_FORMAT.format(chord.hrdValue()) + ") " + entry.conclusion());

                actionable.add("HRD");
                oncogenic.add("HRD");
            }
        }
    }

    @VisibleForTesting
    static void generateMSIConclusion(@NotNull Set<String> conclusion, @NotNull PurpleMicrosatelliteStatus microsatelliteStatus,
                                      double microsatelliteMb, @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Set<String> oncogenic,
                                      @NotNull Set<String> actionable) {
        if (microsatelliteStatus == PurpleMicrosatelliteStatus.MSI) {
            ActionabilityKey keyMSI = ImmutableActionabilityKey.builder().match("MSI").type(TypeAlteration.POSITIVE).build();
            ActionabilityEntry entry = actionabilityMap.get(keyMSI);
            if (entry != null && entry.condition() == Condition.ALWAYS) {
                conclusion.add("- " + "MSI (" + DOUBLE_DECIMAL_FORMAT.format(microsatelliteMb) + ") " + entry.conclusion());
                actionable.add("MSI");
                oncogenic.add("MSI");
            }
        }
    }

    @VisibleForTesting
    static void generateTMLConclusion(@NotNull Set<String> conclusion, @NotNull PurpleTumorMutationalStatus tumorMutationalStatus,
                                      int tumorMutationalLoad, @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Set<String> oncogenic,
                                      @NotNull Set<String> actionable) {
        if (tumorMutationalStatus == PurpleTumorMutationalStatus.HIGH) {
            ActionabilityKey keyTML = ImmutableActionabilityKey.builder().match("High-TML").type(TypeAlteration.POSITIVE).build();
            ActionabilityEntry entry = actionabilityMap.get(keyTML);
            if (entry != null && entry.condition() == Condition.ALWAYS) {
                conclusion.add("- " + "TML (" + tumorMutationalLoad + ") " + entry.conclusion());
                actionable.add("TML");
                oncogenic.add("TML");
            }
        }
    }

    @VisibleForTesting
    static void generateTMBConclusion(@NotNull Set<String> conclusion, double tumorMutationalBurden,
                                      @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap, @NotNull Set<String> oncogenic,
                                      @NotNull Set<String> actionable) {
        if (tumorMutationalBurden >= TMB_CUTOFF) {
            ActionabilityKey keyTMB = ImmutableActionabilityKey.builder().match("High-TMB").type(TypeAlteration.POSITIVE).build();
            ActionabilityEntry entry = actionabilityMap.get(keyTMB);
            if (entry != null && entry.condition() == Condition.ALWAYS) {
                conclusion.add("- " + "TMB (" + tumorMutationalBurden + ") " + entry.conclusion());
                actionable.add("TMB");
                oncogenic.add("TMB");
            }
        }
    }

    @VisibleForTesting
    static void generatePurityConclusion(@NotNull Set<String> conclusion, double purity, boolean containsTumorCells,
                                         @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap) {
        if (!containsTumorCells) {
            ActionabilityKey keyReliable =
                    ImmutableActionabilityKey.builder().match("PURITY_UNRELIABLE").type(TypeAlteration.PURITY_UNRELIABLE).build();

            ActionabilityEntry entryReliable = actionabilityMap.get(keyReliable);
            if (entryReliable != null && entryReliable.condition() == Condition.OTHER) {
                conclusion.add("- " + entryReliable.conclusion() + "\n");
            }
        } else if (purity < PURITY_CUTOFF) {
            ActionabilityKey keyPurity = ImmutableActionabilityKey.builder().match("PURITY").type(TypeAlteration.PURITY).build();

            ActionabilityEntry entry = actionabilityMap.get(keyPurity);
            if (entry != null && entry.condition() == Condition.OTHER) {
                conclusion.add("- " + entry.conclusion().replace("XX%", Formats.formatPercentageRound(purity)) + "\n");
            }
        }
    }

    @VisibleForTesting
    static void generateTotalResults(@NotNull Set<String> conclusion, @NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap,
                                     @NotNull Set<String> oncogenic, @NotNull Set<String> actionable) {
        if (oncogenic.size() == 0) {
            ActionabilityKey keyOncogenic =
                    ImmutableActionabilityKey.builder().match("NO_ONCOGENIC").type(TypeAlteration.NO_ONCOGENIC).build();

            ActionabilityEntry entry = actionabilityMap.get(keyOncogenic);
            if (entry != null && entry.condition() == Condition.OTHER) {
                conclusion.add("- " + entry.conclusion());
            }
        } else if (actionable.size() == 0) {
            ActionabilityKey keyActionable =
                    ImmutableActionabilityKey.builder().match("NO_ACTIONABLE").type(TypeAlteration.NO_ACTIONABLE).build();
            ActionabilityEntry entry = actionabilityMap.get(keyActionable);
            if (entry != null && entry.condition() == Condition.OTHER) {
                conclusion.add("- " + entry.conclusion());
            }
        }
    }

    @NotNull
    private static DecimalFormat decimalFormat(@NotNull String format) {
        // To make sure every decimal format uses a dot as separator rather than a comma.
        return new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }
}