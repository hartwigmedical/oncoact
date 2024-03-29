package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.ArrayList;
import java.util.Collections;
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
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.KnowledgebaseSource;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.AminoAcids;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.VerticalAlignment;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class ClinicalEvidenceFunctions {

    private final ReportResources reportResources;
    private final TableUtil tableUtil;
    private final EvidenceItems evidenceItems;

    public ClinicalEvidenceFunctions(ReportResources reportResources) {
        this.reportResources = reportResources;
        this.tableUtil = new TableUtil(reportResources);
        this.evidenceItems = new EvidenceItems(reportResources);
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
            Boolean requireOnLabel, @NotNull String name) {
        Map<String, List<ProtectEvidence>> evidencePerTreatmentMap = Maps.newHashMap();

        for (ProtectEvidence evidence : evidences) {
            if ((reportGermline || !evidence.germline()) && (requireOnLabel == null || evidence.onLabel() == requireOnLabel)) {
                String treatment = Strings.EMPTY;
                List<ProtectEvidence> treatmentEvidences = Lists.newArrayList();
                Set<String> treatmentApproaches = evidence.treatment().sourceRelevantTreatmentApproaches();
                String treatmentJoin = String.join(",", treatmentApproaches);
                if (name.equals("treatmentApproach")) {
                    if (!treatmentJoin.isEmpty()) {
                        List<String> treatentSort = Lists.newArrayList(evidence.treatment().sourceRelevantTreatmentApproaches());
                        Collections.sort(treatentSort);
                        treatment = String.join(",", treatentSort);
                        treatmentEvidences = evidencePerTreatmentMap.getOrDefault(treatment, new ArrayList<>());
                        if (!hasHigherOrEqualEvidenceForEventAndTreatmentApproach(treatmentEvidences, evidence)
                                && !treatment.equals(Strings.EMPTY)) {
                            treatmentEvidences.add(evidence);
                            evidencePerTreatmentMap.put(treatment, treatmentEvidences);
                        }
                    }
                } else {
                    if (treatmentJoin.isEmpty()) {
                        treatment = evidence.treatment().name();
                        treatmentEvidences = evidencePerTreatmentMap.getOrDefault(treatment, new ArrayList<>());
                        if (!hasHigherOrEqualEvidenceForEventAndTreatment(treatmentEvidences, evidence)
                                && !treatment.equals(Strings.EMPTY)) {
                            treatmentEvidences.add(evidence);
                            evidencePerTreatmentMap.put(treatment, treatmentEvidences);
                        }

                    }
                }

            }
        }
        return evidencePerTreatmentMap;
    }

    private static boolean hasHigherOrEqualEvidenceForEventAndTreatment(@NotNull List<ProtectEvidence> evidences,
            @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.treatment().name().equals(evidenceToCheck.treatment().name()) && StringUtils.equals(evidence.gene(),
                    evidenceToCheck.gene()) && evidence.event().equals(evidenceToCheck.event())) {
                if (!evidenceToCheck.level().isHigher(evidence.level())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasHigherOrEqualEvidenceForEventAndTreatmentApproach(@NotNull List<ProtectEvidence> evidences,
            @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.treatment().relevantTreatmentApproaches().equals(evidenceToCheck.treatment().relevantTreatmentApproaches())
                    && StringUtils.equals(evidence.gene(), evidenceToCheck.gene()) && evidence.event().equals(evidenceToCheck.event())) {
                if (!evidenceToCheck.level().isHigher(evidence.level())) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    public Table createTreatmentApproachTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
            float contentWidth) {
        Table treatmentTable = TableUtil.createReportContentTable(new float[] { 25, 100, 50, 80, 25, 15, 40, 100 },
                new Cell[] { tableUtil.createHeaderCell("Drug type", 2), tableUtil.createHeaderCell("Tumor type specific", 1),
                        tableUtil.createHeaderCell("Match", 1), tableUtil.createHeaderCell("Level", 1),
                        tableUtil.createHeaderCell("Response", 2), tableUtil.createHeaderCell("Genomic event", 1) },
                contentWidth);

        treatmentTable = addDataIntoTable(treatmentTable, treatmentMap, title, "treatmentApproach");
        return treatmentTable;
    }

    @NotNull
    public Table createTreatmentTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap, float contentWidth) {
        Table treatmentTable = TableUtil.createReportContentTable(new float[] { 20, 100, 50, 80, 25, 15, 40, 100, 60 },
                new Cell[] { tableUtil.createHeaderCell("Drug", 2), tableUtil.createHeaderCell("Tumor type specific", 1),
                        tableUtil.createHeaderCell("Match", 1), tableUtil.createHeaderCell("Level", 1),
                        tableUtil.createHeaderCell("Response", 2), tableUtil.createHeaderCell("Genomic event", 1),
                        tableUtil.createHeaderCell("Evidence links", 1) },
                contentWidth);

        treatmentTable = addDataIntoTable(treatmentTable, treatmentMap, title, "treatment");
        return treatmentTable;
    }

    @NotNull
    public Table createTrialTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap, float contentWidth) {
        Table treatmentTable = TableUtil.createReportContentTable(new float[] { 20, 170, 80, 170 },
                new Cell[] { tableUtil.createHeaderCell("Trial", 2), tableUtil.createHeaderCell("Match", 1),
                        tableUtil.createHeaderCell("Genomic event", 1) },
                contentWidth);

        treatmentTable = addDataIntoTable(treatmentTable, treatmentMap, title, "trial");
        return treatmentTable;
    }

    @NotNull
    private Table addDataIntoTable(@NotNull Table treatmentTable, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
            @NotNull String title, @NotNull String evidenceType) {
        boolean hasEvidence = false;
        for (EvidenceLevel level : EvidenceLevel.values()) {
            if (addEvidenceWithMaxLevel(treatmentTable, treatmentMap, level, evidenceType)) {
                hasEvidence = true;
            }
        }

        if (hasEvidence) {
            return tableUtil.createWrappingReportTable(title, null, treatmentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        } else {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }
    }

    @NotNull
    private Paragraph createTreatmentIcons(@NotNull String allDrugs) {
        String[] drugs = allDrugs.split(Pattern.quote(TREATMENT_DELIMITER));
        Paragraph p = new Paragraph();
        for (String drug : drugs) {
            p.add(Icon.createTreatmentIcon(reportResources, drug.trim()));
        }
        return p;
    }

    private boolean addEvidenceWithMaxLevel(@NotNull Table table, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
            @NotNull EvidenceLevel allowedHighestLevel, @NotNull String evidenceType) {
        Set<String> sortedTreatments = Sets.newTreeSet(treatmentMap.keySet());
        boolean hasEvidence = false;
        for (String treatment : sortedTreatments) {
            List<ProtectEvidence> evidences = treatmentMap.get(treatment);
            if (allowedHighestLevel == highestEvidence(treatmentMap.get(treatment))) {

                boolean addTreatment = true;
                for (ProtectEvidence responsive : sort(evidences)) {
                    Cell cellGenomic = tableUtil.createTransparentCell(display(responsive));

                    String onLabel = responsive.onLabel() ? "Yes" : "No";
                    Cell cellOnLabel = tableUtil.createTransparentCell(onLabel);

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

                    Cell cellType = tableUtil.createTransparentCell(evidenceItems.createLinksSource(sourceUrls));
                    Cell cellLevel = tableUtil.createTransparentCell(Strings.EMPTY);
                    Cell cellPredicted = tableUtil.createTransparentCell(Strings.EMPTY);
                    Cell cellResistant = tableUtil.createTransparentCell(Strings.EMPTY);
                    if (!evidenceType.equals("trial")) {
                        if (PREDICTED.contains(responsive.direction())) {
                            cellPredicted = tableUtil.createTransparentCell(PREDICTED_SYMBOL).addStyle(reportResources.predictedStyle());
                        }

                        if (RESISTANT_DIRECTIONS.contains(responsive.direction())) {
                            cellResistant = tableUtil.createTransparentCell(RESISTANT_SYMBOL).addStyle(reportResources.resistantStyle());
                        }

                        if (RESPONSE_DIRECTIONS.contains(responsive.direction())) {
                            cellResistant = tableUtil.createTransparentCell(RESPONSE_SYMBOL).addStyle(reportResources.responseStyle());
                        }

                        cellLevel = tableUtil.createTransparentCell(new Paragraph(Icon.createLevelIcon(reportResources,
                                responsive.level().name())));
                    }

                    Cell publications = tableUtil.createTransparentCell(Strings.EMPTY);
                    if (evidenceType.equals("treatment")) {
                        publications = tableUtil.createTransparentCell(evidenceItems.createLinksPublications(evidenceUrls));
                    }

                    if (addTreatment) {
                        table.addCell(tableUtil.createContentCellRowSpan(createTreatmentIcons(treatment), evidences.size())
                                .setVerticalAlignment(VerticalAlignment.TOP));
                        table.addCell(tableUtil.createContentCellRowSpan(treatment, evidences.size()));
                        addTreatment = false;
                    }

                    if (evidenceType.equals("treatmentApproach") || evidenceType.equals("treatment")) {
                        table.addCell(tableUtil.createContentCell(cellOnLabel));
                        table.addCell(tableUtil.createContentCell(cellType));
                        table.addCell(tableUtil.createContentCell(cellLevel));
                        table.addCell(tableUtil.createContentCell(cellResistant));
                        table.addCell(tableUtil.createContentCell(cellPredicted));
                        table.addCell(tableUtil.createContentCell(cellGenomic));
                        if (evidenceType.equals("treatment")) {
                            table.addCell(tableUtil.createContentCell(publications));
                        }
                    } else {
                        table.addCell(tableUtil.createContentCell(tableUtil.createTransparentCell(evidenceItems.createSourceIclusion(
                                sourceUrls))));
                        table.addCell(tableUtil.createContentCell(cellGenomic));
                    }
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

        if (source.evidenceType().equals(EvidenceType.CODON_MUTATION) || source.evidenceType().equals(EvidenceType.EXON_MUTATION)) {
            evidenceRank = String.valueOf(source.rangeRank());
        }

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
    private static String display(@NotNull ProtectEvidence evidence) {
        String event = evidence.gene() != null ? evidence.gene() + " " + evidence.event() : evidence.event();
        if (event.contains("p.")) {
            event = AminoAcids.forceSingleLetterProteinAnnotation(event);
        }

        return event;
    }

    public static int compareOnNaturalOrder(boolean x, boolean y) {
        if (x == y) {
            return 0;
        } else {
            return x ? -1 : 1;
        }
    }

    @NotNull
    private static List<ProtectEvidence> sort(@NotNull List<ProtectEvidence> evidenceItems) {
        return evidenceItems.stream().sorted((item1, item2) -> {
            if (item1.treatment().equals(item2.treatment())) {
                if (item1.level().equals(item2.level())) {
                    if (item1.direction().equals(item2.direction())) {
                        return compareOnNaturalOrder(item1.onLabel(), item2.onLabel());
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
    public Paragraph noteEvidence() {
        return new Paragraph().setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add("The Clinical Knowledgebase (CKB) is used to annotate genomic events with clinical evidence. Only evidence of level ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_A))
                .add(" (FDA approved therapy and/or guidelines), level ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_B))
                .add(" (late clinical trials), and/or level ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_C))
                .add(" (early clinical trials) are reported. Evidence items of level ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_D))
                .add(" (case reports and preclinical evidence) are not reported. The response symbol ")
                .add(new Text(RESPONSE_SYMBOL).addStyle(reportResources.responseStyle()))
                .add(" means that the evidence is responsive. The resistent symbol ")
                .add(new Text(RESISTANT_SYMBOL).addStyle(reportResources.resistantStyle()))
                .add(" means that the evidence is resistant. The abbreviation ")
                .add(new Text(PREDICTED_SYMBOL).addStyle(reportResources.predictedStyle()))
                .add(" (mentioned after the response symbol) indicates the evidence is predicted responsive/resistent "
                        + "(meaning, the evidence data are limited but a potenial response/resistence is suggested). "
                        + "More details about CKB can be found in their ")
                .add(new Text(" Glossary Of Terms").addStyle(reportResources.urlStyle())
                        .setAction(PdfAction.createURI("https://ckbhome.jax.org/about/glossaryOfTerms")))
                .add(".")
                .addStyle(reportResources.subTextStyle());

    }

    @NotNull
    public Paragraph noteEvidenceMatching() {
        return new Paragraph().setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(" If the evidence matching is based on a mutation, but this is not a hotspot (see table Tumor observed variants under "
                        + "Genomic events), evidence should be interpreted with extra caution.\n")
                .addStyle(reportResources.subTextStyle())
                .add("If the evidence matching is based on an amplification, evidence that corresponds with ‘overexpression’ of that gene "
                        + "is also matched. The same rule applies for deletions and 'underexpression'.\n")
                .addStyle(reportResources.subTextStyle());
    }

    @NotNull
    public Paragraph note(@NotNull String message) {
        return new Paragraph(message).addStyle(reportResources.subTextStyle());
    }
}