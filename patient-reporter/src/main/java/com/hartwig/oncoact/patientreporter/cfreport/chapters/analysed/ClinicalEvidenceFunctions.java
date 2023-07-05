package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.components.Icon;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.EvidenceItems;
import com.hartwig.oncoact.protect.KnowledgebaseSource;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.AminoAcids;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class ClinicalEvidenceFunctions {

    private ClinicalEvidenceFunctions() {
    }

    private static final String TREATMENT_DELIMITER = " + ";

    private static final String RESPONSE_SYMBOL = "\u25B2";
    private static final String RESISTANT_SYMBOL = "\u25BC";
    private static final String PREDICTED_SYMBOL = "P";

    private static final Set<EvidenceDirection> RESISTANT_DIRECTIONS =
            Sets.newHashSet(EvidenceDirection.RESISTANT, EvidenceDirection.PREDICTED_RESISTANT);
    private static final Set<EvidenceDirection> RESPONSE_DIRECTIONS =
            Sets.newHashSet(EvidenceDirection.RESPONSIVE, EvidenceDirection.PREDICTED_RESPONSIVE);
    private static final Set<EvidenceDirection> PREDICTED =
            Sets.newHashSet(EvidenceDirection.PREDICTED_RESISTANT, EvidenceDirection.PREDICTED_RESPONSIVE);

    @NotNull
    public static Map<String, List<ProtectEvidence>> buildTreatmentMap(@NotNull List<ProtectEvidence> evidences, boolean reportGermline,
                                                                       Boolean requireOnLabel) {
        Map<String, List<ProtectEvidence>> evidencePerTreatmentMap = Maps.newHashMap();

        for (ProtectEvidence evidence : evidences) {
            if ((reportGermline || !evidence.germline()) && (requireOnLabel == null || evidence.onLabel() == requireOnLabel)) {
                List<ProtectEvidence> treatmentEvidences = evidencePerTreatmentMap.get(evidence.treatment().name());
                if (treatmentEvidences == null) {
                    treatmentEvidences = Lists.newArrayList();
                }
                if (!hasHigherOrEqualEvidenceForEventAndTreatment(treatmentEvidences, evidence)) {
                    treatmentEvidences.add(evidence);
                }
                evidencePerTreatmentMap.put(evidence.treatment().name(), treatmentEvidences);
            }
        }
        return evidencePerTreatmentMap;
    }

    private static boolean hasHigherOrEqualEvidenceForEventAndTreatment(@NotNull List<ProtectEvidence> evidences,
                                                                        @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.treatment().equals(evidenceToCheck.treatment()) && StringUtils.equals(evidence.gene(), evidenceToCheck.gene())
                    && evidence.event().equals(evidenceToCheck.event())) {
                if (!evidenceToCheck.level().isHigher(evidence.level())) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    public static Table createTreatmentApproachTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
                                                     float contentWidth) {
        Table treatmentTable = TableUtil.createReportContentTable(new float[]{150, 300},
                new Cell[]{TableUtil.createHeaderCellEmpty("", 1), TableUtil.createHeaderCellEmpty("", 1)},
                contentWidth);

        treatmentTable = addDataIntoTable(treatmentTable, treatmentMap, title, "evidenceTreatmentApproach", contentWidth);
        return treatmentTable;
    }

    @NotNull
    public static Table createTreatmentTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
                                             float contentWidth) {
        Table treatmentTable = TableUtil.createReportContentTable(new float[]{150, 300},
                new Cell[]{TableUtil.createHeaderCellEmpty("", 1), TableUtil.createHeaderCellEmpty("", 1)},
                contentWidth);

        treatmentTable = addDataIntoTable(treatmentTable, treatmentMap, title, "evidenceTreatment", contentWidth);
        return treatmentTable;
    }

    @NotNull
    public static Table createTrialTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
                                         float contentWidth) {

        Table treatmentTable = TableUtil.createReportContentTable(new float[]{190, 250},
                new Cell[]{TableUtil.createHeaderCellEmpty("", 1), TableUtil.createHeaderCellEmpty("", 1)}, contentWidth);
        treatmentTable = addDataIntoTable(treatmentTable, treatmentMap, title, "trial", contentWidth);
        return treatmentTable;
    }

    @NotNull
    private static Table addDataIntoTable(@NotNull Table treatmentTable, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
                                          @NotNull String title, @NotNull String evidenceType, float contentWidth) {
        boolean hasEvidence = false;
        for (EvidenceLevel level : EvidenceLevel.values()) {
            if (addEvidenceWithMaxLevel(treatmentTable, treatmentMap, level, evidenceType, contentWidth)) {
                hasEvidence = true;
            }
        }

        if (hasEvidence) {
            return TableUtil.createWrappingReportTable(title, null, treatmentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        } else {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }
    }

    @NotNull
    private static Paragraph createTreatmentIcons(@NotNull String allDrugs) {
        String[] drugs = allDrugs.split(Pattern.quote(TREATMENT_DELIMITER));
        Paragraph p = new Paragraph();
        for (String drug : drugs) {
            p.add(Icon.createTreatmentIcon(drug.trim()));
        }
        return p;
    }

    private static boolean addEvidenceWithMaxLevel(@NotNull Table table, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
                                                   @NotNull EvidenceLevel allowedHighestLevel, @NotNull String evidenceType, float contentWidth) {
        Set<String> sortedTreatments = Sets.newTreeSet(treatmentMap.keySet());
        boolean hasEvidence = false;
        boolean addHeader = true;

        for (String treatment : sortedTreatments) {
            List<ProtectEvidence> evidences = treatmentMap.get(treatment);

            if (allowedHighestLevel == highestEvidence(treatmentMap.get(treatment))) {
                Table matchTrialTable1;
                Table matchTrialTable2;

                Table matchEvidenceTable1Approach;
                Table matchEvidenceTable2Approach;
                Table matchEvidenceTable1Treatment;
                Table matchEvidenceTable2Treatment;
                if (addHeader && evidenceType.equals("evidenceTreatmentApproach")) {
                    matchEvidenceTable1Approach = TableUtil.createReportContentTable(new float[]{20, 190},
                            new Cell[]{TableUtil.createHeaderCell("Treatment", 2)},
                            contentWidth);

                    matchEvidenceTable2Approach = TableUtil.createReportContentTable(new float[]{30, 60, 20, 20, 20, 60, 60},
                            new Cell[]{TableUtil.createHeaderCell("OnLabel", 1),
                                    TableUtil.createHeaderCell("Match", 1),
                                    TableUtil.createHeaderCell("Level", 1),
                                    TableUtil.createHeaderCell("Response", 2),
                                    TableUtil.createHeaderCell("Genomic Event", 1),
                                    TableUtil.createHeaderCell("Publications", 1)},
                            contentWidth);
                    addHeader = false;
                } else {
                    matchEvidenceTable1Approach = TableUtil.createReportContentTable(new float[]{20, 190}, contentWidth);
                    matchEvidenceTable2Approach = TableUtil.createReportContentTable(new float[]{30, 60, 20, 20, 20, 60, 60}, contentWidth);
                }

                if (addHeader && evidenceType.equals("evidenceTreatment")) {
                    matchEvidenceTable1Treatment = TableUtil.createReportContentTable(new float[]{20, 190},
                            new Cell[]{TableUtil.createHeaderCell("Treatment", 2)},
                            contentWidth);

                    matchEvidenceTable2Treatment = TableUtil.createReportContentTable(new float[]{30, 60, 20, 20, 20, 60, 60},
                            new Cell[]{TableUtil.createHeaderCell("OnLabel", 1),
                                    TableUtil.createHeaderCell("Match", 1),
                                    TableUtil.createHeaderCell("Level", 1),
                                    TableUtil.createHeaderCell("Response", 2),
                                    TableUtil.createHeaderCell("Genomic Event", 1),
                                    TableUtil.createHeaderCell("Publications", 1)},
                            contentWidth);
                    addHeader = false;
                } else {
                    matchEvidenceTable1Treatment = TableUtil.createReportContentTable(new float[]{20, 190}, contentWidth);
                    matchEvidenceTable2Treatment = TableUtil.createReportContentTable(new float[]{30, 60, 20, 20, 20, 60, 60}, contentWidth);
                }

                if (addHeader && evidenceType.equals("trial")) {
                    matchTrialTable1 = TableUtil.createReportContentTable(new float[]{20, 190},
                            new Cell[]{TableUtil.createHeaderCell("Trial", 2)},
                            contentWidth);

                    matchTrialTable2 = TableUtil.createReportContentTable(new float[]{80, 180},
                            new Cell[]{TableUtil.createHeaderCell("Match", 1),
                                    TableUtil.createHeaderCell("genomic event", 1)},
                            contentWidth);
                    addHeader = false;
                } else {
                    matchTrialTable1 = TableUtil.createReportContentTable(new float[]{20, 190}, contentWidth);
                    matchTrialTable2 = TableUtil.createReportContentTable(new float[]{80, 180}, contentWidth);
                }

                matchTrialTable1.addCell(TableUtil.createTransparentCell(createTreatmentIcons(treatment)).setVerticalAlignment(VerticalAlignment.TOP));
                matchTrialTable1.addCell(TableUtil.createTransparentCell(TableUtil.createContentCell(treatment)));
                matchEvidenceTable1Approach.addCell(TableUtil.createTransparentCell(createTreatmentIcons(treatment)).setVerticalAlignment(VerticalAlignment.TOP));
                matchEvidenceTable1Approach.addCell(TableUtil.createTransparentCell(TableUtil.createContentCell(treatment)));
                matchEvidenceTable1Treatment.addCell(TableUtil.createTransparentCell(createTreatmentIcons(treatment)).setVerticalAlignment(VerticalAlignment.TOP));
                matchEvidenceTable1Treatment.addCell(TableUtil.createTransparentCell(TableUtil.createContentCell(treatment)));


                for (ProtectEvidence responsive : sort(evidences)) {
                    String onLabel = responsive.onLabel() ? "Yes" : "No";
                    Cell cellOnLabel = TableUtil.createTransparentCell(new Paragraph(onLabel));

                    Map<String, String> sourceUrls = Maps.newHashMap();
                    Set<String> evidenceUrls = Sets.newHashSet();

                    for (KnowledgebaseSource source : responsive.sources()) {
                        evidenceUrls.addAll(source.evidenceUrls());

                        if (source.sourceUrls().size() >= 1) {
                            sourceUrls.put(determineEvidenceType(source), source.sourceUrls().stream().iterator().next());
                        } else {
                            sourceUrls.put(determineEvidenceType(source), Strings.EMPTY);
                        }
                    }

                    Cell cellLevel = TableUtil.createTransparentCell(Strings.EMPTY);
                    Cell cellPredicted = TableUtil.createTransparentCell(Strings.EMPTY);
                    Cell cellResistant = TableUtil.createTransparentCell(Strings.EMPTY);
                    if (!evidenceType.equals("trial")) {
                        if (PREDICTED.contains(responsive.direction())) {
                            cellPredicted = TableUtil.createTransparentCell(PREDICTED_SYMBOL).addStyle(ReportResources.predictedStyle());
                        }

                        if (RESISTANT_DIRECTIONS.contains(responsive.direction())) {
                            cellResistant = TableUtil.createTransparentCell(RESISTANT_SYMBOL).addStyle(ReportResources.resistantStyle());
                        }

                        if (RESPONSE_DIRECTIONS.contains(responsive.direction())) {
                            cellResistant = TableUtil.createTransparentCell(RESPONSE_SYMBOL).addStyle(ReportResources.responseStyle());
                        }

                        cellLevel = TableUtil.createTransparentCell(new Paragraph(Icon.createLevelIcon(responsive.level().name())));
                    }


                    Cell cellType = TableUtil.createTransparentCell(EvidenceItems.createLinksSource(sourceUrls));

                    Cell cellGenomic = TableUtil.createTransparentCell(display(responsive));
                    Cell publications = TableUtil.createTransparentCell(EvidenceItems.createLinksPublications(evidenceUrls));

                    if (evidenceType.equals("evidenceTreatmentApproach")) {
                        matchEvidenceTable2Approach.addCell(TableUtil.createTransparentCell(cellOnLabel));
                        matchEvidenceTable2Approach.addCell(cellType);
                        matchEvidenceTable2Approach.addCell(TableUtil.createTransparentCell(cellLevel));
                        matchEvidenceTable2Approach.addCell(TableUtil.createTransparentCell(cellResistant));
                        matchEvidenceTable2Approach.addCell(TableUtil.createTransparentCell(cellPredicted));
                        matchEvidenceTable2Approach.addCell(TableUtil.createTransparentCell(cellGenomic));
                        matchEvidenceTable2Approach.addCell(TableUtil.createTransparentCell(publications));
                    } else if (evidenceType.equals("evidenceTreatment")) {
                        matchEvidenceTable2Treatment.addCell(TableUtil.createTransparentCell(cellOnLabel));
                        matchEvidenceTable2Treatment.addCell(cellType);
                        matchEvidenceTable2Treatment.addCell(TableUtil.createTransparentCell(cellLevel));
                        matchEvidenceTable2Treatment.addCell(TableUtil.createTransparentCell(cellResistant));
                        matchEvidenceTable2Treatment.addCell(TableUtil.createTransparentCell(cellPredicted));
                        matchEvidenceTable2Treatment.addCell(TableUtil.createTransparentCell(cellGenomic));
                        matchEvidenceTable2Treatment.addCell(TableUtil.createTransparentCell(publications));
                    } else {
                        matchTrialTable2.addCell(TableUtil.createTransparentCell(cellType));
                        matchTrialTable2.addCell(TableUtil.createTransparentCell(cellGenomic));
                    }

                }

                if (evidenceType.equals("evidenceTreatmentApproach")) {
                    table.addCell(TableUtil.createContentCell(matchEvidenceTable1Approach).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.CENTER));
                    table.addCell(TableUtil.createContentCell(matchEvidenceTable2Approach)).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.CENTER);

                } else if (evidenceType.equals("evidenceTreatment")) {
                    table.addCell(TableUtil.createContentCell(matchEvidenceTable1Treatment)).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table.addCell(TableUtil.createContentCell(matchEvidenceTable2Treatment)).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.CENTER);

                } else {
                    table.addCell(TableUtil.createContentCell(matchTrialTable1)).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.CENTER);
                    table.addCell(TableUtil.createContentCell(matchTrialTable2)).setVerticalAlignment(VerticalAlignment.TOP).setHorizontalAlignment(HorizontalAlignment.CENTER);
                }
                hasEvidence = true;
            }


        }

        return hasEvidence;
    }

    @NotNull
    private static String determineEvidenceType(@NotNull KnowledgebaseSource source) {
        String evidenceRank = Strings.EMPTY;
        String evidenceSource = source.evidenceType().display();

        // TODO Review in context of having no more range rank.
//        if (source.evidenceType().equals(EvidenceType.CODON_MUTATION) || source.evidenceType().equals(EvidenceType.EXON_MUTATION)) {
//            evidenceRank = String.valueOf(source.rangeRank());
//        }

        String evidenceMerged;
        if (!evidenceRank.isEmpty()) {
            evidenceMerged = evidenceSource + " " + evidenceRank;
        } else {
            evidenceMerged = evidenceSource;
        }
        return evidenceMerged;
    }

    @NotNull
    private static EvidenceLevel highestEvidence(@NotNull List<ProtectEvidence> evidences) {
        EvidenceLevel highest = null;
        for (ProtectEvidence evidence : evidences) {
            if (highest == null || evidence.level().isHigher(highest)) {
                highest = evidence.level();
            }
        }

        return highest;
    }

    @NotNull
    private static Paragraph display(@NotNull ProtectEvidence evidence) {
        Paragraph paragraphSources = new Paragraph();
        String event = evidence.gene() != null ? evidence.gene() + " " + evidence.event() : evidence.event();
        if (event.contains("p.")) {
            event = AminoAcids.forceSingleLetterProteinAnnotation(event);
        }

        paragraphSources.add(new Text(event).addStyle(ReportResources.subTextStyle()));

        return paragraphSources;

    }

    @NotNull
    private static List<ProtectEvidence> sort(@NotNull List<ProtectEvidence> evidenceItems) {
        return evidenceItems.stream().sorted((item1, item2) -> {
            if (item1.treatment().equals(item2.treatment())) {
                if (item1.level().equals(item2.level())) {
                    if (item1.direction().equals(item2.direction())) {
                        return item1.direction().compareTo(item2.direction());
                    } else {
                        return item1.direction().compareTo(item2.direction());
                    }
                } else {
                    return item1.level().compareTo(item2.level());
                }
            } else {
                return item1.treatment().name().compareTo(item2.treatment().name());
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    public static Paragraph noteGlossaryTerms() {
        return new Paragraph("The symbol ( ").add(new Text(RESPONSE_SYMBOL).addStyle(ReportResources.responseStyle()))
                .add(" ) means that the evidence is responsive. The symbol ( ")
                .add(new Text(RESISTANT_SYMBOL).addStyle(ReportResources.resistantStyle()))
                .add(" ) means that the evidence is resistant. The abbreviation ( ")
                .add(new Text(PREDICTED_SYMBOL).addStyle(ReportResources.predictedStyle()))
                .add(" mentioned after the level of evidence) indicates the evidence is predicted "
                        + "responsive/resistent. More details about CKB can be found in their")
                .addStyle(ReportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(" Glossary Of Terms").addStyle(ReportResources.urlStyle())
                        .setAction(PdfAction.createURI("https://ckbhome.jax.org/about/glossaryOfTerms")))
                .add(".")
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    public static Paragraph noteEvidence() {
        return new Paragraph().setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add("The Clinical Knowledgebase (CKB) is used to annotate variants of all types with clinical evidence. "
                        + "Only treatment associated evidence with evidence levels ( \n( ")
                .add(Icon.createIcon(Icon.IconType.LEVEL_A))
                .add(" FDA approved therapy and/or guidelines; ")
                .add(Icon.createIcon(Icon.IconType.LEVEL_B))
                .add(" late clinical trials; ")
                .add(Icon.createIcon(Icon.IconType.LEVEL_C))
                .add(" early clinical trials) can be reported. Potential evidence items with evidence level  \n( ")
                .add(Icon.createIcon(Icon.IconType.LEVEL_D))
                .add(" case reports and preclinical evidence) are not reported.")
                .addStyle(ReportResources.subTextStyle());
    }

    @NotNull
    public static Paragraph noteEvidenceMatching() {
        return new Paragraph().setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add("If the evidence matched is based on a mutation, but this is not a hotspot, evidence should be interpreted with "
                        + "extra caution. \n")
                .addStyle(ReportResources.subTextStyle())
                .add("If a genomic event that results in an amplification is found, evidence that corresponds with ‘overexpression’"
                        + " of the gene is also matched. The same rule applies for deletions and underexpression.\n")
                .addStyle(ReportResources.subTextStyle());
    }

    @NotNull
    public static Paragraph note(@NotNull String message) {
        return new Paragraph(message).addStyle(ReportResources.subTextStyle());
    }
}