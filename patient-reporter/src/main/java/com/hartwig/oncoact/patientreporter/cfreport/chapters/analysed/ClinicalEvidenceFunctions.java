package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import static com.hartwig.oncoact.util.ActionabilityIntervation.setToField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
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
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Treatment;
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
    private static final EnumMap<EvidenceDirection, Integer> DIRECTION_PRIORITY_MAP =
            EvidenceDirectionComparator.generateDirectionPriorityMap();
    private static final HashMap<Boolean, Integer> TUMOR_TYPE_PRIORITY_MAP =
            EvidenceTumorTypeSpecificComparator.generateTumorTypePriorityMap();
    private static final Set<EvidenceDirection> RESISTANT_DIRECTIONS =
            Sets.newHashSet(EvidenceDirection.RESISTANT, EvidenceDirection.PREDICTED_RESISTANT);
    private static final Set<EvidenceDirection> RESPONSE_DIRECTIONS =
            Sets.newHashSet(EvidenceDirection.RESPONSIVE, EvidenceDirection.PREDICTED_RESPONSIVE);
    private static final Set<EvidenceDirection> PREDICTED =
            Sets.newHashSet(EvidenceDirection.PREDICTED_RESISTANT, EvidenceDirection.PREDICTED_RESPONSIVE);

    @NotNull
    public static TreeSet<String> extractCombinedTreatmentApproaches(@NotNull Set<String> treatmentApproachesDrugClass,
            @NotNull Set<String> treatmentApproachesTherapy) {
        TreeSet<String> combinedTreatmentApproach = Sets.newTreeSet();
        if (!treatmentApproachesDrugClass.isEmpty()) {
            combinedTreatmentApproach.addAll(treatmentApproachesDrugClass);
        } else if (!treatmentApproachesTherapy.isEmpty()) {
            combinedTreatmentApproach.addAll(treatmentApproachesTherapy);
        }
        return combinedTreatmentApproach;
    }

    @NotNull
    public static Map<String, List<ProtectEvidence>> buildTreatmentMap(@NotNull List<ProtectEvidence> evidences, boolean reportGermline,
            Boolean requireOnLabel, @NotNull String name) {
        Map<String, List<ProtectEvidence>> evidencePerTreatmentMap = Maps.newHashMap();

        evidences.sort(Comparator.comparing(ProtectEvidence::event)
                .thenComparing(it -> it.gene() != null ? it.gene() : Strings.EMPTY)
                .thenComparing(ProtectEvidence::onLabel)
                .reversed()
                .thenComparing(ProtectEvidence::level)
                .thenComparing(ProtectEvidence::direction)
                .thenComparing(it -> {
                    Set<String> treatmentApproaches =
                            it.treatment() != null ? extractCombinedTreatmentApproaches(it.treatment().treatmentApproachesDrugClass(),
                                    it.treatment().treatmentApproachesTherapy()) : Sets.newHashSet();
                    return String.join(",", treatmentApproaches);
                }));

        for (ProtectEvidence evidence : evidences) {
            Treatment treatmentModel = evidence.treatment();
            if (treatmentModel != null) {
                if ((reportGermline || !evidence.germline()) && (requireOnLabel == null || evidence.onLabel() == requireOnLabel)) {
                    String treatment = Strings.EMPTY;
                    List<ProtectEvidence> treatmentEvidences = Lists.newArrayList();

                    Set<String> treatmentApproaches = extractCombinedTreatmentApproaches(treatmentModel.treatmentApproachesDrugClass(),
                            treatmentModel.treatmentApproachesTherapy());
                    String treatmentJoin = String.join(",", treatmentApproaches);
                    if (name.equals("treatmentApproach")) {
                        if (!treatmentJoin.isEmpty()) {
                            List<String> treatmentSort = Lists.newArrayList(treatmentApproaches);
                            Collections.sort(treatmentSort);
                            treatment = String.join(",", treatmentSort);
                            treatmentEvidences = evidencePerTreatmentMap.getOrDefault(treatment, new ArrayList<>());
                            if (!hasHigherOrEqualEvidenceForEventAndTreatmentApproach(treatmentEvidences, evidence) && !treatment.equals(
                                    Strings.EMPTY)) {
                                treatmentEvidences.add(evidence);
                                evidencePerTreatmentMap.put(treatment, treatmentEvidences);
                            }
                        }
                    } else {
                        if (treatmentJoin.isEmpty()) {
                            treatment = treatmentModel.name();
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
        }
        return evidencePerTreatmentMap;
    }

    @NotNull
    public static Map<String, List<ProtectEvidence>> buildTrialMap(@NotNull List<ProtectEvidence> evidences, boolean reportGermline,
            Boolean requireOnLabel, @NotNull String name) {
        Map<String, List<ProtectEvidence>> evidencePerTreatmentMap = Maps.newHashMap();

        evidences.sort(Comparator.comparing(ProtectEvidence::event)
                .thenComparing(it -> it.gene() != null ? it.gene() : Strings.EMPTY)
                .thenComparing(ProtectEvidence::onLabel)
                .reversed()
                .thenComparing(ProtectEvidence::level)
                .thenComparing(ProtectEvidence::direction)
                .thenComparing(it -> it.clinicalTrial() != null ? it.clinicalTrial().studyNctId() : Strings.EMPTY)
                .thenComparing(it -> {
                    Set<String> treatmentApproaches = it.clinicalTrial() != null ? it.clinicalTrial().therapyNames() : Sets.newHashSet();
                    return String.join(",", treatmentApproaches);
                }));

        for (ProtectEvidence evidence : evidences) {
            ClinicalTrial clinicalTrialModel = evidence.clinicalTrial();

            if (clinicalTrialModel != null) {
                String trial = clinicalTrialModel.studyTitle();
                if ((reportGermline || !evidence.germline()) && (requireOnLabel == null || evidence.onLabel() == requireOnLabel)) {
                    if (!trial.equals(Strings.EMPTY)) {

                        List<ProtectEvidence> trialEvidences = Lists.newArrayList();
                        trialEvidences = evidencePerTreatmentMap.getOrDefault(trial, new ArrayList<>());
                        if (!hasHigherOrEqualEvidenceForEventAndTrial(trialEvidences, evidence)) {
                            trialEvidences.add(evidence);
                            evidencePerTreatmentMap.put(trial, trialEvidences);
                        }
                    }
                }
            }
        }
        return evidencePerTreatmentMap;
    }

    public static boolean hasHigherOrEqualEvidenceForEventAndTreatment(@NotNull List<ProtectEvidence> evidences,
            @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {

            Treatment treatment = evidence.treatment();
            Treatment evidenceToCheckTreatment = evidenceToCheck.treatment();

            if (treatment != null && evidenceToCheckTreatment != null) {
                if (treatment.name().equals(evidenceToCheckTreatment.name()) && StringUtils.equals(evidence.gene(), evidenceToCheck.gene())
                        && evidence.event().equals(evidenceToCheck.event())) {
                    return priorityEvidence(evidence, evidenceToCheck);
                }
            }
        }
        return false;
    }

    public static boolean hasHigherOrEqualEvidenceForEventAndTrial(@NotNull List<ProtectEvidence> evidences,
            @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {
            ClinicalTrial clinicalTrial = evidence.clinicalTrial();
            ClinicalTrial evidenceToCheckTrial = evidenceToCheck.clinicalTrial();

            if (clinicalTrial != null && evidenceToCheckTrial != null) {
                if (clinicalTrial.studyNctId().equals(evidenceToCheckTrial.studyNctId()) && StringUtils.equals(evidence.gene(),
                        evidenceToCheck.gene()) && evidence.event().equals(evidenceToCheck.event())) {
                    return priorityEvidence(evidence, evidenceToCheck);
                }
            }
        }
        return false;
    }

    public static boolean hasHigherOrEqualEvidenceForEventAndTreatmentApproach(@NotNull List<ProtectEvidence> evidences,
            @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {
            Treatment treatment = evidence.treatment();
            Treatment evidenceToCheckTreatment = evidenceToCheck.treatment();

            if (treatment != null && evidenceToCheckTreatment != null) {
                if (setToField(extractCombinedTreatmentApproaches(treatment.treatmentApproachesDrugClass(),
                        treatment.treatmentApproachesTherapy())).equals(setToField(extractCombinedTreatmentApproaches(
                        evidenceToCheckTreatment.treatmentApproachesDrugClass(),
                        evidenceToCheckTreatment.treatmentApproachesTherapy()))) && StringUtils.equals(evidence.gene(),
                        evidenceToCheck.gene()) && evidence.event().equals(evidenceToCheck.event())) {

                    return priorityEvidence(evidence, evidenceToCheck);
                }
            }
        }
        return false;
    }

    public static boolean priorityEvidence(@NotNull ProtectEvidence evidence, @NotNull ProtectEvidence evidenceToCheck) {
        if (TUMOR_TYPE_PRIORITY_MAP.get(evidence.onLabel()) <= TUMOR_TYPE_PRIORITY_MAP.get(evidenceToCheck.onLabel())) {
            if (evidence.level().equals(evidenceToCheck.level())) {
                return !DIRECTION_PRIORITY_MAP.get(evidence.direction()).equals(DIRECTION_PRIORITY_MAP.get(evidenceToCheck.direction()))
                        || DIRECTION_PRIORITY_MAP.get(evidence.direction()) <= DIRECTION_PRIORITY_MAP.get(evidenceToCheck.direction());
            } else if (evidence.level().isHigher(evidenceToCheck.level())) {
                return !DIRECTION_PRIORITY_MAP.get(evidence.direction()).equals(DIRECTION_PRIORITY_MAP.get(evidenceToCheck.direction()))
                        || DIRECTION_PRIORITY_MAP.get(evidence.direction()) <= DIRECTION_PRIORITY_MAP.get(evidenceToCheck.direction());
            }
        }
        return true;
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
    public Table createTrialTable(@NotNull String title, @NotNull Map<String, List<ProtectEvidence>> treatmentMap, float contentWidth) {
        Table treatmentTable = TableUtil.createReportContentTable(new float[] { 20, 70, 130, 80, 80, 130 },
                new Cell[] { tableUtil.createHeaderCell("nct ID", 2), tableUtil.createHeaderCell("Trial", 1),
                        tableUtil.createHeaderCell("Treatment", 1), tableUtil.createHeaderCell("Match", 1),
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
            if (evidenceType.equals("trial")) {
                if (addEvidenceWithMaxLevelStudy(treatmentTable, treatmentMap, level)) {
                    hasEvidence = true;
                }
            } else {
                if (addEvidenceWithMaxLevel(treatmentTable, treatmentMap, level, evidenceType)) {
                    hasEvidence = true;
                }
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

    private boolean addEvidenceWithMaxLevelStudy(@NotNull Table table, @NotNull Map<String, List<ProtectEvidence>> trialMap,
            @NotNull EvidenceLevel allowedHighestLevel) {
        Set<String> sortedTrials = Sets.newTreeSet(trialMap.keySet());
        boolean hasEvidence = false;

        for (String trial : sortedTrials) {
            List<ProtectEvidence> evidencesTrials = trialMap.get(trial);
            if (allowedHighestLevel == highestEvidence(trialMap.get(trial))) {
                boolean addTrial = true;

                Map<String, List<ProtectEvidence>> treatmentMap = sortTrial(evidencesTrials).stream()
                        .collect(Collectors.groupingBy(evidence -> evidence.clinicalTrial() != null ? String.join(" | ",
                                Objects.requireNonNull(evidence.clinicalTrial()).therapyNames()) : Strings.EMPTY));
                Set<String> sortedTreatments = Sets.newTreeSet(treatmentMap.keySet());
                for (String treatment : sortedTreatments) {
                    boolean addTreatment = true;

                    List<ProtectEvidence> evidencesTreatments = treatmentMap.get(treatment);
                    for (ProtectEvidence responsive : evidencesTreatments) {
                        ClinicalTrial clinicalTrial = responsive.clinicalTrial();
                        Cell cellGenomic = tableUtil.createTransparentCell(display(responsive));

                        Map<String, String> sourceUrls = Maps.newHashMap();

                        for (KnowledgebaseSource source : responsive.sources()) {
                            if (source.sourceUrls().size() >= 1) {
                                sourceUrls.put(determineEvidenceType(source), source.sourceUrls().stream().iterator().next());
                            } else {
                                sourceUrls.put(determineEvidenceType(source), Strings.EMPTY);
                            }
                        }
                        if (addTrial) {
                            table.addCell(tableUtil.createContentCellRowSpan(createTreatmentIcons(trial), evidencesTrials.size())
                                    .setVerticalAlignment(VerticalAlignment.TOP));
                            table.addCell(tableUtil.createContentCellRowSpan(evidenceItems.createClinicalTrialLink(Objects.requireNonNull(
                                    clinicalTrial).studyNctId()), evidencesTrials.size()));
                            String shortenTrial = clinicalTrial.studyAcronym() != null
                                    ? clinicalTrial.studyAcronym()
                                    : EvidenceItems.shortenTrialName(clinicalTrial.studyTitle());
                            assert shortenTrial != null;
                            table.addCell(tableUtil.createContentCellRowSpan(shortenTrial, evidencesTrials.size()));
                            addTrial = false;
                        }

                        if (addTreatment) {
                            String therapyName = treatment.split("\\|").length > 3 ? "Multiple" : treatment;
                            table.addCell(tableUtil.createContentCellRowSpan(therapyName, evidencesTreatments.size()));
                            addTreatment = false;

                        }
                        table.addCell(tableUtil.createContentCell(evidenceItems.createLinksSource(sourceUrls)));
                        table.addCell(tableUtil.createContentCell(cellGenomic));
                    }
                }
                hasEvidence = true;
            }
        }
        return hasEvidence;
    }

    private boolean addEvidenceWithMaxLevel(@NotNull Table table, @NotNull Map<String, List<ProtectEvidence>> treatmentMap,
            @NotNull EvidenceLevel allowedHighestLevel, @NotNull String evidenceType) {
        Set<String> sortedTreatments = Sets.newTreeSet(treatmentMap.keySet());
        boolean hasEvidence = false;
        for (String treatment : sortedTreatments) {
            List<ProtectEvidence> evidences = treatmentMap.get(treatment);
            if (allowedHighestLevel == highestEvidence(treatmentMap.get(treatment))) {

                boolean addTreatment = true;
                for (ProtectEvidence responsive : sortEvidence(evidences)) {
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
                    Cell cellPredicted = tableUtil.createTransparentCell(Strings.EMPTY);
                    Cell cellResistant = tableUtil.createTransparentCell(Strings.EMPTY);
                    if (PREDICTED.contains(responsive.direction())) {
                        cellPredicted = tableUtil.createTransparentCell(PREDICTED_SYMBOL).addStyle(reportResources.predictedStyle());
                    }

                    if (RESISTANT_DIRECTIONS.contains(responsive.direction())) {
                        cellResistant = tableUtil.createTransparentCell(RESISTANT_SYMBOL).addStyle(reportResources.resistantStyle());
                    }

                    if (RESPONSE_DIRECTIONS.contains(responsive.direction())) {
                        cellResistant = tableUtil.createTransparentCell(RESPONSE_SYMBOL).addStyle(reportResources.responseStyle());
                    }

                    Cell cellLevel = tableUtil.createTransparentCell(new Paragraph(Icon.createLevelIcon(reportResources,
                            responsive.level().name())));

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

                    table.addCell(tableUtil.createContentCell(cellOnLabel));
                    table.addCell(tableUtil.createContentCell(cellType));
                    table.addCell(tableUtil.createContentCell(cellLevel));
                    table.addCell(tableUtil.createContentCell(cellResistant));
                    table.addCell(tableUtil.createContentCell(cellPredicted));
                    table.addCell(tableUtil.createContentCell(cellGenomic));
                    if (evidenceType.equals("treatment")) {
                        table.addCell(tableUtil.createContentCell(publications));
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
    private static List<ProtectEvidence> sortTrial(@NotNull List<ProtectEvidence> evidenceItems) {
        return evidenceItems.stream().sorted((item1, item2) -> {
            ClinicalTrial clinicalTrial1 = item1.clinicalTrial();
            ClinicalTrial clinicalTrial2 = item2.clinicalTrial();

            if (clinicalTrial1 != null && clinicalTrial2 != null) {
                if (clinicalTrial1.studyNctId().equals(clinicalTrial2.studyNctId())) {
                    if (setToField(clinicalTrial1.therapyNames()).equals(setToField(clinicalTrial2.therapyNames()))) {
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
                        return setToField(clinicalTrial1.therapyNames()).compareTo(setToField(clinicalTrial2.therapyNames()));
                    }
                } else {
                    return clinicalTrial1.studyNctId().compareTo(clinicalTrial2.studyNctId());
                }
            }
            return 0;
        }).collect(Collectors.toList());
    }

    @NotNull
    private static List<ProtectEvidence> sortEvidence(@NotNull List<ProtectEvidence> evidenceItems) {
        return evidenceItems.stream().sorted((item1, item2) -> {
            Treatment treatment1 = item1.treatment();
            Treatment treatment2 = item2.treatment();

            if (treatment1 != null && treatment2 != null) {
                if (treatment1.name().equals(treatment2.name())) {
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
                    return treatment1.name().compareTo(treatment2.name());
                }
            }
            return 0;
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
                .addStyle(reportResources.subTextStyle())
                .add("For MMR genes only: If the evidence matching is based on an inactivation or deletion, evidence that corresponds "
                        + "with ‘absence of protein' expression of that gene, mRNA, or protein is also matched.")
                .addStyle(reportResources.subTextStyle());
    }

    @NotNull
    public Paragraph note(@NotNull String message) {
        return new Paragraph(message).addStyle(reportResources.subTextStyle());
    }
}