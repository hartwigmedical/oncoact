package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.lims.Lims;
import com.hartwig.oncoact.orange.chord.ChordStatus;
import com.hartwig.oncoact.orange.peach.PeachEntry;
import com.hartwig.oncoact.orange.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.MathUtil;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.InlineBarChart;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.cfreport.data.ClinicalTrials;
import com.hartwig.oncoact.patientreporter.cfreport.data.EvidenceItems;
import com.hartwig.oncoact.patientreporter.cfreport.data.GainsAndLosses;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneFusions;
import com.hartwig.oncoact.patientreporter.cfreport.data.HLAAllele;
import com.hartwig.oncoact.patientreporter.cfreport.data.HomozygousDisruptions;
import com.hartwig.oncoact.patientreporter.cfreport.data.SomaticVariants;
import com.hartwig.oncoact.patientreporter.cfreport.data.TumorPurity;
import com.hartwig.oncoact.patientreporter.cfreport.data.ViralPresence;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummaryChapter implements ReportChapter {

    private static final float TABLE_SPACER_HEIGHT = 5;
    private static final DecimalFormat SINGLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.#");
    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.##");

    @NotNull
    private final AnalysedPatientReport patientReport;

    public SummaryChapter(@NotNull final AnalysedPatientReport patientReport) {
        this.patientReport = patientReport;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        if (patientReport.isCorrectedReport()) {
            return "DNA Analysis Report (Corrected)";
        } else {
            if (patientReport.qsFormNumber().equals(QsFormNumber.FOR_209.display())) {
                return "DNA Analysis Report - Low Sensitivity";
            } else {
                return "DNA Analysis Report";
            }
        }
    }

    @NotNull
    @Override
    public String name() {
        return "Summary";
    }

    @Override
    public boolean isFullWidth() {
        return false;
    }

    @Override
    public boolean hasCompleteSidebar() {
        return true;
    }

    private GenomicAnalysis analysis() {
        return patientReport.genomicAnalysis();
    }

    @Override
    public void render(@NotNull Document reportDocument) {
        reportDocument.add(TumorLocationAndTypeTable.createBiopsyLocationAndTumorLocation(patientReport.sampleReport()
                .primaryTumorLocationString(), patientReport.sampleReport().biopsyLocationString(), contentWidth()));
        reportDocument.add(new Paragraph());
        reportDocument.add(TumorLocationAndTypeTable.createTumorType(patientReport.sampleReport().primaryTumorTypeString(),
                contentWidth()));
        reportDocument.add(new Paragraph("\nThe information regarding 'primary tumor location', 'primary tumor type' and 'biopsy location'"
                + "  \nis based on information received from the originating hospital.").addStyle(ReportResources.subTextStyle()));

        renderClinicalConclusionText(reportDocument);
        renderSpecialRemarkText(reportDocument);
        renderTreatmentIndications(reportDocument);
        renderTumorCharacteristics(reportDocument);
        renderGenomicAlterations(reportDocument);
        renderPharmacogenetics(reportDocument);
        renderHla(reportDocument);
        renderGermlineText(reportDocument);
    }

    private void renderClinicalConclusionText(@NotNull Document reportDocument) {
        String text = patientReport.clinicalSummary();
        String clinicalConclusion = Strings.EMPTY;
        if (text == null) {
            String sentence = "An overview of all detected oncogenic DNA aberrations can be found in the report";

            if (!analysis().hasReliablePurity()) {
                clinicalConclusion = "Of note, WGS analysis indicated a very low abundance of genomic aberrations, which can be caused "
                        + "by a low tumor percentage in the received tumor material or due to genomic very stable/normal tumor type. "
                        + "As a consequence no reliable tumor purity assessment is possible and no information regarding "
                        + "mutation copy number and tVAF can be provided.\n" + sentence;
            } else if (analysis().impliedPurity() < ReportResources.PURITY_CUTOFF) {
                double impliedPurityPercentage =
                        MathUtil.mapPercentage(analysis().impliedPurity(), TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX);
                clinicalConclusion = "Due to the lower sensitivity (" + Formats.formatPercentage(impliedPurityPercentage) + ") "
                        + "of this test potential (subclonal) DNA aberrations might not have been detected using this test. " + ""
                        + "This result should therefore be considered with caution.\n" + sentence;
            }
        } else {
            clinicalConclusion = text;
        }

        if (!clinicalConclusion.isEmpty()) {
            Div div = createSectionStartDiv(contentWidth());
            div.add(new Paragraph("Summary of most relevant findings").addStyle(ReportResources.sectionTitleStyle()));

            div.add(new Paragraph(text).setWidth(contentWidth()).addStyle(ReportResources.bodyTextStyle()).setFixedLeading(11));
            div.add(new Paragraph("\nFurther interpretation of these results within the patient???s clinical context is required "
                    + "by a clinician with support of a molecular tumor board..").addStyle(ReportResources.subTextStyle()));

            reportDocument.add(div);
        }
    }

    private void renderSpecialRemarkText(@NotNull Document reportDocument) {
        String text = patientReport.specialRemark();

        if (!text.isEmpty()) {
            Div div = createSectionStartDiv(contentWidth());
            div.add(new Paragraph("Special Remark").addStyle(ReportResources.sectionTitleStyle()));

            div.add(new Paragraph(text).setWidth(contentWidth()).addStyle(ReportResources.bodyTextStyle()).setFixedLeading(11));

            reportDocument.add(div);
        }
    }

    private void renderTreatmentIndications(@NotNull Document reportDocument) {
        Div div = createSectionStartDiv(contentWidth());

        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
        table.setWidth(contentWidth());
        table.addCell(TableUtil.createLayoutCellSummary()
                .add(new Paragraph("Treatment options (tumor-type specific)").addStyle(ReportResources.sectionTitleStyle())));

        table.addCell(TableUtil.createLayoutCell(4, 2).setHeight(TABLE_SPACER_HEIGHT));

        int therapyEventCount = EvidenceItems.uniqueEventCount(analysis().tumorSpecificEvidence());
        table.addCell(createMiddleAlignedCell().add(new Paragraph("Number of alterations with therapy indication").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createTreatmentIndicationCell(therapyEventCount,
                EvidenceItems.onLabelTreatmentString(analysis().tumorSpecificEvidence()),
                "treatment(s)"));

        int trialEventCount = ClinicalTrials.uniqueEventCount(analysis().clinicalTrials());
        int trialCount = ClinicalTrials.uniqueTrialCount(analysis().clinicalTrials());
        table.addCell(createMiddleAlignedCell().add(new Paragraph("Number of alterations with clinical trial eligibility").addStyle(
                ReportResources.bodyTextStyle())));
        table.addCell(createStudyIndicationCell(trialEventCount, trialCount, "trial(s)"));
        div.add(table);

        reportDocument.add(div);
    }

    private void renderTumorCharacteristics(@NotNull Document reportDocument) {
        boolean hasReliablePurity = analysis().hasReliablePurity();

        Div div = createSectionStartDiv(contentWidth());

        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, .33f, .66f }));
        table.setWidth(contentWidth());
        table.addCell(TableUtil.createLayoutCell()
                .add(new Paragraph("Tumor characteristics").setVerticalAlignment(VerticalAlignment.TOP)
                        .addStyle(ReportResources.sectionTitleStyle())));
        table.addCell(TableUtil.createLayoutCell(1, 3).setHeight(TABLE_SPACER_HEIGHT));

        double impliedPurity = analysis().impliedPurity();
        double impliedPurityPercentage = MathUtil.mapPercentage(impliedPurity, TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX);
        renderTumorPurity(hasReliablePurity,
                Formats.formatPercentage(impliedPurityPercentage),
                impliedPurity,
                TumorPurity.RANGE_MIN,
                TumorPurity.RANGE_MAX,
                table);

        String cuppaPrediction = Strings.EMPTY;
        if (patientReport.molecularTissueOriginReporting() == null) {
            cuppaPrediction = Formats.NA_STRING;
        } else if (patientReport.molecularTissueOriginReporting() != null && patientReport.genomicAnalysis().hasReliablePurity()) {
            if (patientReport.molecularTissueOriginReporting().interpretLikelihood() == null) {
                cuppaPrediction = patientReport.molecularTissueOriginReporting().interpretCancerType();
            } else {
                cuppaPrediction =
                        patientReport.molecularTissueOriginReporting().interpretCancerType() + " (" + Formats.formatPercentageDigit(
                                patientReport.molecularTissueOriginReporting().interpretLikelihood()) + ")";
            }
        }

        Style dataStyleMolecularTissuePrediction =
                hasReliablePurity ? ReportResources.dataHighlightStyle() : ReportResources.dataHighlightNaStyle();

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Molecular tissue of origin prediction").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(cuppaPrediction).addStyle(dataStyleMolecularTissuePrediction)));

        Style dataStyle = hasReliablePurity ? ReportResources.dataHighlightStyle() : ReportResources.dataHighlightNaStyle();

        // TODO evaluate display
        String mutationalLoadString = hasReliablePurity ? analysis().tumorMutationalLoadStatus() + " (" + SINGLE_DECIMAL_FORMAT.format(
                analysis().tumorMutationalLoad()) + " mut/genome)" : Formats.NA_STRING;
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Tumor mutational load").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(mutationalLoadString).addStyle(dataStyle)));

        // TODO evaluate display
        String microSatelliteStabilityString = hasReliablePurity ? analysis().microsatelliteStatus() + " (" + DOUBLE_DECIMAL_FORMAT.format(
                analysis().microsatelliteIndelsPerMb()) + " indels/genome)" : Formats.NA_STRING;
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Microsatellite (in)stability").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(microSatelliteStabilityString).addStyle(dataStyle)));

        String hrdString;
        Style hrdStyle;

        // TODO evaluate display
        if (hasReliablePurity && (ChordStatus.HR_DEFICIENT == analysis().hrdStatus()
                || ChordStatus.HR_PROFICIENT == analysis().hrdStatus())) {
            hrdString = analysis().hrdStatus() + " (" + DOUBLE_DECIMAL_FORMAT.format(analysis().hrdValue()) + " signature)";
            hrdStyle = ReportResources.dataHighlightStyle();
        } else {
            hrdString = Formats.NA_STRING;
            hrdStyle = ReportResources.dataHighlightNaStyle();
        }

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("HR Status").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(hrdString).addStyle(hrdStyle)));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Virus").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createVirusInterpretationString(ViralPresence.virusInterpretationSummary(analysis().reportableViruses()),
                patientReport.sampleReport().reportViralPresence()));

        div.add(table);

        reportDocument.add(div);
    }

    @NotNull
    private static Cell createVirusInterpretationString(@NotNull Set<String> virus, boolean reportViralPresence) {
        String virusSummary;
        Style style;
        if (reportViralPresence && virus.size() == 0) {
            virusSummary = Formats.NONE_STRING;
            style = ReportResources.dataHighlightNaStyle();
        } else if (reportViralPresence && virus.size() > 0) {
            virusSummary = String.join(", ", virus);
            style = ReportResources.dataHighlightStyle();
        } else {
            virusSummary = Formats.NA_STRING;
            style = ReportResources.dataHighlightNaStyle();
        }

        return createMiddleAlignedCell(2).add(createHighlightParagraph(virusSummary)).addStyle(style);
    }

    private static void renderTumorPurity(boolean hasReliablePurity, @NotNull String valueLabel, double value, double min, double max,
            @NotNull Table table) {
        String label = "Tumor purity";
        table.addCell(createMiddleAlignedCell().add(new Paragraph(label).addStyle(ReportResources.bodyTextStyle())));

        if (hasReliablePurity) {
            table.addCell(createMiddleAlignedCell().add(createHighlightParagraph(valueLabel).addStyle(ReportResources.dataHighlightStyle())));
            table.addCell(createMiddleAlignedCell().add(createInlineBarChart(value, min, max)));
        } else {
            table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(Lims.PURITY_NOT_RELIABLE_STRING).addStyle(ReportResources.dataHighlightNaStyle())));
        }
    }

    private void renderGenomicAlterations(@NotNull Document report) {
        Div div = createSectionStartDiv(contentWidth());

        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
        table.setWidth(contentWidth());
        table.addCell(TableUtil.createLayoutCellSummary()
                .add(new Paragraph("Genomic alterations in cancer genes").addStyle(ReportResources.sectionTitleStyle())));
        table.addCell(TableUtil.createLayoutCell(1, 2).setHeight(TABLE_SPACER_HEIGHT));

        Set<String> driverVariantGenes = SomaticVariants.driverGenesWithVariant(analysis().reportableVariants());

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Genes with driver mutation").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(driverVariantGenes));

        int reportedVariants = SomaticVariants.countReportableVariants(analysis().reportableVariants());
        Style reportedVariantsStyle =
                (reportedVariants > 0) ? ReportResources.dataHighlightStyle() : ReportResources.dataHighlightNaStyle();
        table.addCell(createMiddleAlignedCell().add(new Paragraph("Number of reported variants").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell().add(createHighlightParagraph(String.valueOf(reportedVariants)).addStyle(
                reportedVariantsStyle)));

        Set<String> amplifiedGenes = GainsAndLosses.amplifiedGenes(analysis().gainsAndLosses());
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Amplified gene(s)").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(amplifiedGenes));

        Set<String> copyLossGenes = GainsAndLosses.lostGenes(analysis().gainsAndLosses());
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Deleted gene(s)").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(copyLossGenes));

        Set<String> disruptedGenes = HomozygousDisruptions.disruptedGenes(analysis().homozygousDisruptions());
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Homozygously disrupted genes").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(disruptedGenes));

        Set<String> fusionGenes = GeneFusions.uniqueGeneFusions(analysis().geneFusions());
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Gene fusions").addStyle(ReportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(fusionGenes));

        PurpleMicrosatelliteStatus microSatelliteStabilityString =
                analysis().hasReliablePurity() ? analysis().microsatelliteStatus() : PurpleMicrosatelliteStatus.UNKNOWN;
        if (microSatelliteStabilityString == PurpleMicrosatelliteStatus.MSI) {
            Set<String> genesDisplay = SomaticVariants.determineMSIGenes(analysis().reportableVariants(),
                    analysis().gainsAndLosses(),
                    analysis().homozygousDisruptions());
            table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                    .add(new Paragraph("Potential MMR genes").addStyle(ReportResources.bodyTextStyle())));
            table.addCell(createGeneSetCell(genesDisplay));
        }

        ChordStatus hrdStatus = analysis().hasReliablePurity() ? analysis().hrdStatus() : ChordStatus.UNKNOWN;
        if (hrdStatus == ChordStatus.HR_DEFICIENT) {
            Set<String> genesDisplay = SomaticVariants.determineHRDGenes(analysis().reportableVariants(),
                    analysis().gainsAndLosses(),
                    analysis().homozygousDisruptions());
            table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                    .add(new Paragraph("Potential HRD genes").addStyle(ReportResources.bodyTextStyle())));
            table.addCell(createGeneSetCell(genesDisplay));
        }

        div.add(table);

        report.add(div);
    }

    private void renderPharmacogenetics(@NotNull Document report) {
        Div div = createSectionStartDiv(contentWidth());
        String title = "Pharmacogenetics";

        if (patientReport.sampleReport().reportPharmogenetics()) {
            if (patientReport.pharmacogeneticsGenotypes().isEmpty()) {
                div.add(TableUtil.createNoneReportTable(title,
                        null,
                        TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY,
                        ReportResources.CONTENT_WIDTH_WIDE_SUMMARY));
            } else {
                Table contentTable = TableUtil.createReportContentTable(new float[] { 10, 10, 10 },
                        new Cell[] { TableUtil.createHeaderCell("Gene"), TableUtil.createHeaderCell("Number haplotypes"),
                                TableUtil.createHeaderCell("Function") },
                        ReportResources.CONTENT_WIDTH_WIDE_SUMMARY);

                Set<String> sortedPharmacogenetics = Sets.newTreeSet(patientReport.pharmacogeneticsGenotypes().keySet());
                for (String sortPharmacogenetics : sortedPharmacogenetics) {
                    List<PeachEntry> pharmacogeneticsGenotypeList = patientReport.pharmacogeneticsGenotypes().get(sortPharmacogenetics);

                    Set<String> function = Sets.newHashSet();
                    int count = pharmacogeneticsGenotypeList.size();

                    for (PeachEntry pharmacogeneticsGenotype : pharmacogeneticsGenotypeList) {
                        function.add(pharmacogeneticsGenotype.function());
                    }

                    contentTable.addCell(TableUtil.createContentCell(sortPharmacogenetics));
                    contentTable.addCell(TableUtil.createContentCell(Integer.toString(count)));
                    contentTable.addCell(TableUtil.createContentCell(concat(function)));
                }
                div.add(TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY));
            }
        } else {
            String noConsent = "This patient did not give his/her permission for reporting of pharmacogenomics results.";
            div.add(TableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY,
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY));
        }
        report.add(div);
    }

    private void renderHla(@NotNull Document report) {
        Div div = createSectionStartDiv(contentWidth());
        String title = "HLA Alleles";
        if (!patientReport.genomicAnalysis().hlaAlleles().hlaQC().equals("PASS")) {
            String noConsent = "The QC of the HLA types do not meet the QC cut-offs";
            div.add(TableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY,
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY));
        } else if (patientReport.genomicAnalysis().hlaAlleles().hlaAllelesReporting().isEmpty()) {
            div.add(TableUtil.createNoneReportTable(title,
                    null,
                    TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY,
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY));
        } else {
            Table table = TableUtil.createReportContentTable(new float[] { 15, 15, 15 },
                    new Cell[] { TableUtil.createHeaderCell("Gene"), TableUtil.createHeaderCell("Germline allele"),
                            TableUtil.createHeaderCell("Interpretation: presence in tumor") },
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY);

            Set<String> sortedAlleles = Sets.newTreeSet(patientReport.genomicAnalysis().hlaAlleles().hlaAllelesReporting().keySet());
            for (String sortAllele : sortedAlleles) {
                List<HlaReporting> allele = patientReport.genomicAnalysis().hlaAlleles().hlaAllelesReporting().get(sortAllele);

                Set<String> germlineAllele = Sets.newHashSet();
                Set<String> interpretation = Sets.newHashSet();

                for (HlaReporting hlaReporting : HLAAllele.sort(allele)) {
                    germlineAllele.add(hlaReporting.hlaAllele().germlineAllele());
                    interpretation.add(hlaReporting.interpretation());
                }
                table.addCell(TableUtil.createContentCell(sortAllele));
                table.addCell(TableUtil.createContentCell(concat(germlineAllele)));
                table.addCell(TableUtil.createContentCell(conclusionInterpretation(concat(interpretation))));
            }

            div.add(TableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY));
        }
        report.add(div);
    }

    private void renderGermlineText(@NotNull Document reportDocument) {
        String text = "Data concerning cancer predisposition genes may be requested by a clinical geneticist after the patient has "
                + "given informed consent.";

        Div div = createSectionStartDiv(contentWidth());
        div.add(new Paragraph("Germline results").addStyle(ReportResources.sectionTitleStyle()));

        div.add(new Paragraph(text).setWidth(contentWidth()).addStyle(ReportResources.bodyTextStyle()).setFixedLeading(11));

        reportDocument.add(div);

    }

    @NotNull
    public static String conclusionInterpretation(@NotNull String interpretation) {
        if (interpretation.contains("Yes, but mutation(s) detected")) {
            return "Present in tumor with mutations";
        } else if (interpretation.contains("Yes")) {
            return "Present in tumor";
        } else if (interpretation.contains("No")) {
            return "Not present in tumor";
        } else if (interpretation.contains("Unknown")) {
            return "Unknown";
        } else {
            return Strings.EMPTY;
        }
    }

    @NotNull
    public static String concat(@Nullable Iterable<String> strings) {
        if (strings == null) {
            return Strings.EMPTY;
        }

        StringJoiner joiner = new StringJoiner(" | ");
        for (String entry : strings) {
            joiner.add(entry);
        }
        return joiner.toString();
    }

    @NotNull
    private static Div createSectionStartDiv(float width) {
        return new Div().setKeepTogether(true).setWidth(width).add(LineDivider.createLineDivider(width));
    }

    @NotNull
    private static Cell createMiddleAlignedCell() {
        return createMiddleAlignedCell(1);
    }

    @NotNull
    private static Cell createMiddleAlignedCell(int colSpan) {
        return TableUtil.createLayoutCell(1, colSpan).setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    @NotNull
    private static Cell createGeneSetCell(@NotNull Set<String> genes) {
        String geneString = (genes.size() > 0) ? String.join(", ", genes) : Formats.NONE_STRING;

        Style style = (genes.size() > 0) ? ReportResources.dataHighlightStyle() : ReportResources.dataHighlightNaStyle();

        return createMiddleAlignedCell().add(createHighlightParagraph(geneString)).addStyle(style);
    }

    @NotNull
    private static Paragraph createHighlightParagraph(@NotNull String text) {
        return new Paragraph(text).setFixedLeading(14);
    }

    @NotNull
    private static Cell createTreatmentIndicationCell(int eventCount, @NotNull String treatmentCount, @NotNull String treatmentsName) {
        String treatmentText;
        Style style;
        if (eventCount > 0) {
            treatmentText = String.format("%d | %s %s", eventCount, treatmentCount, treatmentsName);
            style = ReportResources.dataHighlightStyle();
        } else {
            treatmentText = Formats.NONE_STRING;
            style = ReportResources.dataHighlightNaStyle();
        }

        return createMiddleAlignedCell().add(createHighlightParagraph(treatmentText)).addStyle(style);
    }

    @NotNull
    private static Cell createStudyIndicationCell(int eventCount, int treatmentCount, @NotNull String treatmentsName) {
        String treatmentText;
        Style style;
        if (eventCount > 0) {
            treatmentText = String.format("%d | %d %s", eventCount, treatmentCount, treatmentsName);
            style = ReportResources.dataHighlightStyle();
        } else {
            treatmentText = Formats.NONE_STRING;
            style = ReportResources.dataHighlightNaStyle();
        }

        return createMiddleAlignedCell().add(createHighlightParagraph(treatmentText)).addStyle(style);
    }

    @NotNull
    private static InlineBarChart createInlineBarChart(double value, double min, double max) {
        InlineBarChart chart = new InlineBarChart(value, min, max);
        chart.setWidth(41);
        chart.setHeight(6);
        return chart;
    }
}
