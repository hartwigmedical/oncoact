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
            boolean requireOnLabel) {
        Map<String, List<ProtectEvidence>> evidencePerTreatmentMap = Maps.newHashMap();

        for (ProtectEvidence evidence : evidences) {
            if ((reportGermline || !evidence.germline()) && evidence.onLabel() == requireOnLabel) {
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
    public Table createTreatmentTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
            float contentWidth) {
        Table treatmentTable = TableUtil.createReportContentTable(new float[] { 25, 120, 80, 25, 40, 120, 60 },
                new Cell[] { tableUtil.createHeaderCell("Treatment", 2), tableUtil.createHeaderCell("Match", 1),
                        tableUtil.createHeaderCell("Level", 1), tableUtil.createHeaderCell("Response", 1),
                        tableUtil.createHeaderCell("Genomic event", 1), tableUtil.createHeaderCell("Evidence links", 1) },
                contentWidth);

        treatmentTable = addDataIntoTable(treatmentTable, treatmentMap, title, "evidence");
        return treatmentTable;
    }

    @NotNull
    public Table createTrialTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
            float contentWidth) {
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
                table.addCell(tableUtil.createContentCell(createTreatmentIcons(treatment)).setVerticalAlignment(VerticalAlignment.TOP));
                table.addCell(tableUtil.createContentCell(treatment));

                Table typeTable = new Table(new float[] { 1 });
                Table levelTable = new Table(new float[] { 1 });
                Table responseTable = new Table(new float[] { 1, 1 });

                Table responsiveTable = new Table(new float[] { 1 });
                Table linksTable = new Table(new float[] { 1 });

                for (ProtectEvidence responsive : sort(evidences)) {
                    Cell cellGenomic = tableUtil.createTransparentCell(display(responsive));

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

                    Cell cellType;
                    cellType = tableUtil.createTransparentCell(evidenceItems.createLinksSource(sourceUrls));
                    typeTable.addCell(cellType);

                    Cell cellLevel;
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

                        cellLevel = tableUtil.createTransparentCell(new Paragraph(Icon.createLevelIcon(reportResources, responsive.level().name())));

                        levelTable.addCell(cellLevel);
                        responseTable.addCell(cellResistant);
                        responseTable.addCell(cellPredicted);
                    }
                    responsiveTable.addCell(cellGenomic);

                    Cell publications = tableUtil.createTransparentCell(Strings.EMPTY);
                    if (evidenceType.equals("evidence")) {
                        publications = tableUtil.createTransparentCell(evidenceItems.createLinksPublications(evidenceUrls));
                        linksTable.addCell(publications);
                    } else {
                        linksTable.addCell(publications);
                    }
                }

                if (evidenceType.equals("evidence")) {
                    table.addCell(tableUtil.createContentCell(typeTable));
                    table.addCell(tableUtil.createContentCell(levelTable));
                    table.addCell(tableUtil.createContentCell(responseTable));
                    table.addCell(tableUtil.createContentCell(responsiveTable));
                    table.addCell(tableUtil.createContentCell(linksTable));
                } else {
                    table.addCell(tableUtil.createContentCell(typeTable));
                    table.addCell(tableUtil.createContentCell(responsiveTable));
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
    public Paragraph noteGlossaryTerms() {
        return new Paragraph("The symbol ( ").add(new Text(RESPONSE_SYMBOL).addStyle(reportResources.responseStyle()))
                .add(" ) means that the evidence is responsive. The symbol ( ")
                .add(new Text(RESISTANT_SYMBOL).addStyle(reportResources.resistantStyle()))
                .add(" ) means that the evidence is resistant. The abbreviation ( ")
                .add(new Text(PREDICTED_SYMBOL).addStyle(reportResources.predictedStyle()))
                .add(" mentioned after the level of evidence) indicates the evidence is predicted "
                        + "responsive/resistent. More details about CKB can be found in their")
                .addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(" Glossary Of Terms").addStyle(reportResources.urlStyle())
                        .setAction(PdfAction.createURI("https://ckbhome.jax.org/about/glossaryOfTerms")))
                .add(".")
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    public Paragraph noteEvidence() {
        return new Paragraph().setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add("The Clinical Knowledgebase (CKB) is used to annotate variants of all types with clinical evidence. "
                        + "Only treatment associated evidence with evidence levels ( \n( ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_A))
                .add(" FDA approved therapy and/or guidelines; ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_B))
                .add(" late clinical trials; ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_C))
                .add(" early clinical trials) can be reported. Potential evidence items with evidence level  \n( ")
                .add(Icon.createIcon(reportResources, Icon.IconType.LEVEL_D))
                .add(" case reports and preclinical evidence) are not reported.")
                .addStyle(reportResources.subTextStyle());
    }

    @NotNull
    public Paragraph noteEvidenceMatching() {
        return new Paragraph().setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add("If the evidence matched is based on a mutation, but this is not a hotspot, evidence should be interpreted with "
                        + "extra caution. \n")
                .addStyle(reportResources.subTextStyle())
                .add("If a genomic event that results in an amplification is found, evidence that corresponds with ‘overexpression’"
                        + " of the gene is also matched. The same rule applies for deletions and underexpression.\n")
                .addStyle(reportResources.subTextStyle());
    }

    @NotNull
    public Paragraph note(@NotNull String message) {
        return new Paragraph(message).addStyle(reportResources.subTextStyle());
    }
}