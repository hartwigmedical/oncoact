package com.hartwig.oncoact.hla;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import com.ctc.wstx.util.DataUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.oncoact.orange.lilac.LilacHlaAllele;
import com.hartwig.oncoact.orange.lilac.LilacRecord;
import com.hartwig.oncoact.orange.purple.PurpleQCStatus;
import com.hartwig.oncoact.util.Doubles;

import com.hartwig.oncoact.util.Formats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class HlaAllelesReportingFactory {

    private static final Logger LOGGER = LogManager.getLogger(HlaAllelesReportingFactory.class);

    private static final DecimalFormat SINGLE_DIGIT = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    private HlaAllelesReportingFactory() {
    }

    @NotNull
    public static Map<String, List<LilacHlaAllele>> generateLilacMap(@NotNull LilacRecord lilac) {
        Map<String, List<LilacHlaAllele>> mapLilacReportingAlleles = Maps.newHashMap();
        for (LilacHlaAllele lilacAllele : lilac.alleles()) {
            if (mapLilacReportingAlleles.containsKey(lilacAllele.allele())) {
                List<LilacHlaAllele> current = mapLilacReportingAlleles.get(lilacAllele.allele());
                current.add(lilacAllele);
                mapLilacReportingAlleles.put(lilacAllele.allele(), current);
            } else {
                mapLilacReportingAlleles.put(lilacAllele.allele(), Lists.newArrayList(lilacAllele));
            }
        }
        return mapLilacReportingAlleles;
    }

    @NotNull
    public static HlaAllelesReportingData convertToReportData(@NotNull LilacRecord lilac, boolean containsTumorCells, Set<PurpleQCStatus> purpleQCStatus) {
        List<HlaReporting> lilacReportingList = Lists.newArrayList();

        Map<String, List<LilacHlaAllele>> mapLilacReportingAlleles = generateLilacMap(lilac);

        for (Map.Entry<String, List<LilacHlaAllele>> keyMap : mapLilacReportingAlleles.entrySet()) {
            double germlineCopies = 0;
            double tumorCopies = 0;
            String mutationString = Strings.EMPTY;
            if (keyMap.getValue().size() == 1) {
                germlineCopies = 1;
                tumorCopies = keyMap.getValue().get(0).tumorCopyNumber();
                mutationString = mutationString(keyMap.getValue().get(0));
            } else if (keyMap.getValue().size() == 2) {
                germlineCopies = 2;
                tumorCopies = keyMap.getValue().get(0).tumorCopyNumber() + keyMap.getValue().get(1).tumorCopyNumber();
                //Assume only somatic count is added to one of the 2 alleles
                mutationString = mutationString(keyMap.getValue().get(0));
            } else {
                LOGGER.warn("To many hla alleles of allele '{}'", keyMap.getKey());
            }

            boolean contamination = purpleQCStatus.contains(PurpleQCStatus.FAIL_CONTAMINATION);
            boolean failure = purpleQCStatus.contains(PurpleQCStatus.FAIL_NO_TUMOR);

            String interpretation = HLAPresenceInTumor(keyMap.getValue().get(0), mutationString, containsTumorCells);
            String interpretInterpretation = containsTumorCells ? interpretation : "Unknown";

            if (!contamination) {
                lilacReportingList.add(ImmutableHlaReporting.builder()
                        .hlaAllele(ImmutableHlaAllele.builder()
                                .germlineAllele(keyMap.getValue().get(0).allele())
                                .gene(extractHLAGene(keyMap.getValue().get(0)))
                                .build())
                        .germlineCopies(germlineCopies)
                        .tumorCopies(containsTumorCells && !failure ? tumorCopies : Double.NaN)
                        .somaticMutations(!failure ? mutationString : Formats.NA_STRING)
                        .interpretation(!failure ? interpretInterpretation  : Formats.NA_STRING)
                        .build());
            }

        }

        Map<String, List<HlaReporting>> hlaAlleleMap = Maps.newHashMap();

        for (HlaReporting hlaReporting : lilacReportingList) {
            if (hlaAlleleMap.containsKey(hlaReporting.hlaAllele().gene())) {
                List<HlaReporting> current = hlaAlleleMap.get(hlaReporting.hlaAllele().gene());
                current.add(hlaReporting);
                hlaAlleleMap.put(hlaReporting.hlaAllele().gene(), current);
            } else {
                hlaAlleleMap.put(hlaReporting.hlaAllele().gene(), Lists.newArrayList(hlaReporting));
            }
        }

        return ImmutableHlaAllelesReportingData.builder()
                .hlaQC(lilac.qc())
                .hlaAllelesReporting(hlaAlleleMap)
                .build();
    }

    @NotNull
    @VisibleForTesting
    static String extractHLAGene(@NotNull LilacHlaAllele lilacAllele) {
        if (lilacAllele.allele().startsWith("A*")) {
            return "HLA-A";
        } else if (lilacAllele.allele().startsWith("B*")) {
            return "HLA-B";
        } else if (lilacAllele.allele().startsWith("C*")) {
            return "HLA-C";
        } else {
            LOGGER.warn("Unknown HLA gene name '{}' present! ", lilacAllele.allele());
            return Strings.EMPTY;
        }
    }

    @NotNull
    @VisibleForTesting
    static String mutationString(@NotNull LilacHlaAllele allele) {
        StringJoiner joiner = new StringJoiner(", ");
        if (Doubles.positive(allele.somaticMissense())) {
            joiner.add(SINGLE_DIGIT.format(allele.somaticMissense()) + " missense");
        }

        if (Doubles.positive(allele.somaticNonsenseOrFrameshift())) {
            joiner.add(SINGLE_DIGIT.format(allele.somaticNonsenseOrFrameshift()) + " nonsense or frameshift");
        }

        if (Doubles.positive(allele.somaticSplice())) {
            joiner.add(SINGLE_DIGIT.format(allele.somaticSplice()) + " splice");
        }

        if (Doubles.positive(allele.somaticInframeIndel())) {
            joiner.add(SINGLE_DIGIT.format(allele.somaticInframeIndel()) + " inframe indel");
        }

        String result = joiner.toString();
        return !result.isEmpty() ? result : "None";
    }

    @NotNull
    @VisibleForTesting
    static String HLAPresenceInTumor(@NotNull LilacHlaAllele allele, @NotNull String mutationString, Boolean hasReliablePurity) {
        double tumorCopies = Double.parseDouble(SINGLE_DIGIT.format(allele.tumorCopyNumber()));
        boolean mutation = mutationString.contains("missense") || mutationString.contains("nonsense or frameshift")
                || mutationString.contains("splice") || mutationString.contains("inframe indel");
        if (hasReliablePurity) {
            if (tumorCopies == 0) {
                if (mutation) {
                    return "Unknown";
                } else {
                    return "No";
                }
            } else {
                if (!mutation) {
                    return "Yes";
                } else {
                    return "Yes, but mutation(s) detected";
                }
            }
        } else {
            return "Unknown";
        }
    }
}