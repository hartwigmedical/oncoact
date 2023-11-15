package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.MathUtil;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.InlineBarChart;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
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

    @NotNull
    private final AnalysedPatientReport patientReport;
    @NotNull
    private final ReportResources reportResources;
    private final TableUtil tableUtil;
    private final TumorLocationAndTypeTable tumorLocationAndTypeTable;

    public SummaryChapter(@NotNull final AnalysedPatientReport patientReport, @NotNull final ReportResources reportResources) {
        this.patientReport = patientReport;
        this.reportResources = reportResources;
        this.tableUtil = new TableUtil(reportResources);
        this.tumorLocationAndTypeTable = new TumorLocationAndTypeTable(reportResources);
    }

    @NotNull
    @Override
    public String pdfTitle() {
        if (patientReport.isCorrectedReport()) {
            return "OncoAct tumor NGS panel report (Corrected)";
        } else {
            return "OncoAct tumor NGS panel report";
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

        reportDocument.add(tumorLocationAndTypeTable.createTumorLocation(patientReport.lamaPatientData().getPrimaryTumorType(),
                contentWidth()));
        reportDocument.add(tumorLocationAndTypeTable.disclaimerTextTumorLocationBiopsyLocation().addStyle(reportResources.subTextStyle()));

        renderClinicalConclusionText(reportDocument);
        renderSpecialRemarkText(reportDocument);

        renderGermline(reportDocument);
        renderTumorCharacteristics(reportDocument);
        renderGenomicAlterations(reportDocument);
    }

    private void renderClinicalConclusionText(@NotNull Document reportDocument) {
        String text = patientReport.clinicalSummary();
        String clinicalConclusion = Strings.EMPTY;
        String sentence = "An overview of all detected cancer associated DNA aberrations can be found in the report.";

        if (text == null) {
            if (!analysis().hasReliablePurity()) {
                clinicalConclusion = "Of note, WGS analysis indicated a very low abundance of genomic aberrations, which can be caused "
                        + "by a low tumor percentage in the received tumor material or due to genomic very stable/normal tumor type. "
                        + "As a consequence no reliable tumor purity assessment is possible and no information regarding "
                        + "mutation copy number and tVAF can be provided.\n" + sentence;
            } else if (analysis().impliedPurity() < ReportResources.PURITY_CUTOFF) {
                double impliedPurityPercentage =
                        MathUtil.mapPercentage(analysis().impliedPurity(), TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX);
                clinicalConclusion = "Due to the lower sensitivity (" + Formats.formatPercentage(impliedPurityPercentage) + ") "
                        + "of this test potential (subclonal) DNA aberrations might not have been detected using this test. "
                        + "This result should therefore be considered with caution.\n" + sentence;
            }
        } else {
            clinicalConclusion = text + sentence;
        }

        if (!clinicalConclusion.isEmpty()) {
            Div div = createSectionStartDiv(contentWidth());
            div.add(new Paragraph("Summary of most relevant findings").addStyle(reportResources.sectionTitleStyle()));

            div.add(new Paragraph(clinicalConclusion).setWidth(contentWidth())
                    .addStyle(reportResources.bodyTextStyle())
                    .setFixedLeading(11));
            div.add(new Paragraph("\nFurther interpretation of these results within the patient’s clinical context is required "
                    + "by a clinician with support of a molecular tumor board.").addStyle(reportResources.subTextStyle()));

            reportDocument.add(div);
        }
    }

    private void renderSpecialRemarkText(@NotNull Document reportDocument) {
        String text = patientReport.specialRemark();

        if (!text.isEmpty()) {
            Div div = createSectionStartDiv(contentWidth());
            div.add(new Paragraph("Special Remark").addStyle(reportResources.sectionTitleStyle()));

            div.add(new Paragraph(text).setWidth(contentWidth()).addStyle(reportResources.bodyTextStyle()).setFixedLeading(11));

            reportDocument.add(div);
        }
    }

    private void renderTumorCharacteristics(@NotNull Document reportDocument) {
        boolean hasReliablePurity = analysis().hasReliablePurity();

        Div div = createSectionStartDiv(contentWidth());

        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, .33f, .66f }));
        table.setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_LEFT);
        table.addCell(TableUtil.createLayoutCell()
                .add(new Paragraph("Tumor characteristics").setVerticalAlignment(VerticalAlignment.TOP)
                        .addStyle(reportResources.sectionTitleStyle())));
        table.addCell(TableUtil.createLayoutCell(1, 3).setHeight(TABLE_SPACER_HEIGHT));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Molecular tissue of origin prediction").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(Formats.NA_STRING).addStyle(reportResources.dataHighlightNaStyle())));

        Style dataStyle = hasReliablePurity ? reportResources.dataHighlightStyle() : reportResources.dataHighlightNaStyle();

        String eligible = analysis().tumorMutationalBurdenStatus().display();
        String mutationalBurdenString = hasReliablePurity
                ? eligible + " (" + SINGLE_DECIMAL_FORMAT.format(analysis().tumorMutationalBurden()) + ")"
                : Formats.NA_STRING;

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Tumor mutational burden status").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(mutationalBurdenString).addStyle(dataStyle)));

        String microSatelliteStabilityString = hasReliablePurity ? analysis().microsatelliteStatus().name() + " ("
                + SINGLE_DECIMAL_FORMAT.format(analysis().microsatelliteIndelsPerMb()) + ")" : Formats.NA_STRING;
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Microsatellite status").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(microSatelliteStabilityString).addStyle(dataStyle)));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("HR Status").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph("Not validated").addStyle(reportResources.dataHighlightNaStyle())));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Virus").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(Formats.NA_STRING).addStyle(reportResources.dataHighlightNaStyle())));

        div.add(table);

        reportDocument.add(div);
    }

    private void renderGenomicAlterations(@NotNull Document reportDocument) {
        Div div = createSectionStartDiv(contentWidth());

        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
        table.setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_LEFT);
        table.addCell(TableUtil.createLayoutCellSummary()
                .add(new Paragraph("Genomic alterations in cancer genes").addStyle(reportResources.sectionTitleStyle())));
        table.addCell(TableUtil.createLayoutCell(1, 2).setHeight(TABLE_SPACER_HEIGHT));

        Set<String> driverVariantGenes = SomaticVariants.driverGenesWithVariant(analysis().reportableVariants());

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Genes with driver mutation").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(driverVariantGenes));

        Set<String> amplifiedGenes = GainsAndLosses.amplifiedGenes(analysis().gainsAndLosses());
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Amplified gene(s)").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(amplifiedGenes));

        Set<String> copyLossGenes = GainsAndLosses.lostGenes(analysis().gainsAndLosses());
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Deleted gene(s)").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(copyLossGenes));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Homozygously disrupted genes").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(Formats.NA_STRING).addStyle(reportResources.dataHighlightNaStyle())));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Gene fusions").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(Formats.NA_STRING).addStyle(reportResources.dataHighlightNaStyle())));

        PurpleMicrosatelliteStatus microSatelliteStabilityString =
                analysis().hasReliablePurity() ? analysis().microsatelliteStatus() : PurpleMicrosatelliteStatus.UNKNOWN;
        if (microSatelliteStabilityString == PurpleMicrosatelliteStatus.MSI) {
            Set<String> genesDisplay = SomaticVariants.determineMSIGenes(analysis().reportableVariants(),
                    analysis().gainsAndLosses(),
                    analysis().homozygousDisruptions());
            table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                    .add(new Paragraph("Potential MMR genes").addStyle(reportResources.bodyTextStyle())));
            table.addCell(createGeneSetCell(genesDisplay));
        }

        ChordStatus hrdStatus = analysis().hasReliablePurity() ? analysis().hrdStatus() : ChordStatus.UNKNOWN;
        if (hrdStatus == ChordStatus.HR_DEFICIENT) {
            Set<String> genesDisplay = SomaticVariants.determineHRDGenes(analysis().reportableVariants(),
                    analysis().gainsAndLosses(),
                    analysis().homozygousDisruptions());
            table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                    .add(new Paragraph("Potential HRD genes").addStyle(reportResources.bodyTextStyle())));
            table.addCell(createGeneSetCell(genesDisplay));
        }

        div.add(table);
        reportDocument.add(div);
    }

    private void renderGermline(@NotNull Document reportDocument) {
        int width = 180;
        int leftPosition = 400;
        int bottomPosition = 70;

        Div div = new Div();

        div.add(renderPharmacogeneticsText());
        div.add(new Paragraph(Strings.EMPTY)).setFontSize(2);
        div.add(renderHlaText());
        div.add(renderGermlineText());

        reportDocument.add(div.setFixedPosition(leftPosition, bottomPosition, width));

    }

    private Div renderPharmacogeneticsText() {
        String title = "Pharmacogenetics";
        String text = Formats.NA_STRING;

        Div div = createSectionStartDivWithoutLineDivider(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);
        div.add(new Paragraph(title).addStyle(reportResources.sectionTitleStyle()));

        return div.add(new Paragraph(text).setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT)
                .addStyle(reportResources.bodyTextStyle())
                .setFixedLeading(11));

    }

    private Div renderHlaText() {
        String title = "HLA Alleles";
        String text = "Not validated";

        Div div = createSectionStartDivWithoutLineDivider(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);
        div.add(new Paragraph(title).addStyle(reportResources.sectionTitleStyle()));

        return div.add(new Paragraph(text).setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT)
                .addStyle(reportResources.bodyTextStyle())
                .setFixedLeading(11));
    }

    private Div renderGermlineText() {
        String text = "Not available for tumor-only panel data.";

        Div div = createSectionStartDivWithoutLineDivider(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);
        div.add(new Paragraph("Germline results").addStyle(reportResources.sectionTitleStyle()));

        return div.add(new Paragraph(text).setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT)
                .addStyle(reportResources.bodyTextStyle())
                .setFixedLeading(11));
    }

    @NotNull
    private static Div createSectionStartDiv(float width) {
        return new Div().setKeepTogether(true).setWidth(width).add(LineDivider.createLineDivider(width));
    }

    @NotNull
    private static Div createSectionStartDivWithoutLineDivider(float width) {
        return new Div().setKeepTogether(true).setWidth(width);
    }

    @NotNull
    private Cell createMiddleAlignedCell() {
        return createMiddleAlignedCell(1);
    }

    @NotNull
    private Cell createMiddleAlignedCell(int colSpan) {
        return TableUtil.createLayoutCell(1, colSpan).setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    @NotNull
    private Cell createGeneSetCell(@NotNull Set<String> genes) {
        String geneString = (genes.size() > 0) ? String.join(", ", genes) : Formats.NONE_STRING;

        Style style = (genes.size() > 0) ? reportResources.dataHighlightStyle() : reportResources.dataHighlightNaStyle();

        return createMiddleAlignedCell().add(createHighlightParagraph(geneString)).addStyle(style);
    }

    @NotNull
    private static Paragraph createHighlightParagraph(@NotNull String text) {
        return new Paragraph(text).setFixedLeading(14);
    }

    @NotNull
    private static InlineBarChart createInlineBarChart(double value, double min, double max) {
        InlineBarChart chart = new InlineBarChart(value, min, max);
        chart.setWidth(41);
        chart.setHeight(6);
        return chart;
    }
}
}
