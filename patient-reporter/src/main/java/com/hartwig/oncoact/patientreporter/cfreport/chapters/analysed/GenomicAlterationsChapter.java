package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.patientreporter.cfreport.MathUtil;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.InlineBarChart;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneFusions;
import com.hartwig.oncoact.patientreporter.cfreport.data.TumorPurity;
import com.hartwig.oncoact.patientreporter.model.*;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class GenomicAlterationsChapter implements ReportChapter {

    @NotNull
    private final WgsReport wgsReport;

    @NotNull
    private final ReportResources reportResources;

    private final TableUtil tableUtil;

    private final GeneFusions geneFusions;

    public GenomicAlterationsChapter(@NotNull final WgsReport wgsReport, @NotNull final ReportResources reportResources) {
        this.wgsReport = wgsReport;
        this.reportResources = reportResources;
        this.tableUtil = new TableUtil(reportResources);
        this.geneFusions = new GeneFusions(reportResources);
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @Override
    @NotNull
    public String name() {
        return "Genomic events";
    }

    @Override
    public void render(@NotNull Document reportDocument) {
        Genomic genomic = wgsReport.genomic();
        boolean hasReliablePurity = genomic.hasReliablePurity();

        reportDocument.add(createPloidyPloidyTable(genomic.averagePloidy(),
                genomic.purity(),
                hasReliablePurity));

        reportDocument.add(createTumorVariantsTable(genomic.variants()));

        reportDocument.add(createGainsAndLossesTable(genomic.gainsLosses()));
        reportDocument.add(createFusionsTable(genomic.geneFusions()));
        reportDocument.add(createHomozygousDisruptionsTable(genomic.homozygousDisruptions()));

        if (genomic.profiles().homologousRecombinationDeficiency().status() == HomologousRecombinationDeficiencyStatus.HR_DEFICIENT) {
            reportDocument.add(createLOHTable(genomic.lohEventsHrd(), "HRD"));
        }
        if (genomic.profiles().microsatellite().status() == MicrosatelliteStatus.MSI) {
            reportDocument.add(createLOHTable(genomic.lohEventsMsi(), "MSI"));
        }

        reportDocument.add(createDisruptionsTable(genomic.geneDisruptions()));
        reportDocument.add(createVirusTable(genomic.viralInsertions()));
        reportDocument.add(createPharmacogeneticsGenotypesTable(genomic.pharmacogenetics()));
        reportDocument.add(createHlaTable(genomic.hlaAlleles(), genomic.hlaQc()));
    }

    @NotNull
    private Table createPloidyPloidyTable(String ploidy, double purity, boolean hasReliablePurity) {
        String title = "Tumor purity & ploidy";

        Table contentTable =
                TableUtil.createReportContentTable(new float[]{90, 95, 50}, new Cell[]{}, ReportResources.CONTENT_WIDTH_WIDE_SMALL);

        double impliedPurityPercentage = MathUtil.mapPercentage(purity, TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX);
        renderTumorPurity(hasReliablePurity,
                Formats.formatPercentage(impliedPurityPercentage),
                purity,
                TumorPurity.RANGE_MIN,
                TumorPurity.RANGE_MAX,
                contentTable);

        contentTable.addCell(tableUtil.createContentCell("Average tumor ploidy"));
        if (ploidy.equals(Formats.NA_STRING)) {
            contentTable.addCell(tableUtil.createContentCell(ploidy).setTextAlignment(TextAlignment.CENTER));
        } else {
            contentTable.addCell(tableUtil.createContentCellPurityPloidy(ploidy).setTextAlignment(TextAlignment.CENTER));

        }
        contentTable.addCell(tableUtil.createContentCell(Strings.EMPTY));

        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static InlineBarChart createInlineBarChart(double value, double min, double max) {
        InlineBarChart chart = new InlineBarChart(value, min, max);
        chart.setWidth(41);
        chart.setHeight(6);
        return chart;
    }

    private void renderTumorPurity(boolean hasReliablePurity, @NotNull String valueLabel, double value, double min, double max,
                                   @NotNull Table table) {

        String label = "Tumor purity";
        table.addCell(tableUtil.createContentCell(label));

        if (hasReliablePurity) {
            table.addCell(tableUtil.createContentCellPurityPloidy(valueLabel).setTextAlignment(TextAlignment.CENTER));
            table.addCell(tableUtil.createContentCell(createInlineBarChart(value, min, max))
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER));
        } else {
            table.addCell(tableUtil.createContentCell("N/A"));
            table.addCell(tableUtil.createContentCell(Strings.EMPTY));
        }
    }

    @NotNull
    private Table createTumorVariantsTable(@NotNull List<ObservedVariant> reportableVariants) {
        String title = "Tumor observed variants";
        if (reportableVariants.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[]{60, 70, 150, 60, 40, 30, 60, 60, 50},
                new Cell[]{tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Position"),
                        tableUtil.createHeaderCell("Variant"),
                        tableUtil.createHeaderCell("Read depth").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("tVAF").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Biallelic").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Hotspot").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER)},
                ReportResources.CONTENT_WIDTH_WIDE);


        Set<Boolean> hasNotifiableGermlineVariant = Sets.newHashSet();
        Set<Boolean> hasPhasedVariant = Sets.newTreeSet();

        for (ObservedVariant variant : reportableVariants) {
            List<String> annotationList = variant.variant();

            Cell annotationCell = new Cell();
            for (String annotation : annotationList) {
                annotationCell.add(new Paragraph(annotation));
            }
            int annotationSize = annotationList.size();

            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.gene(), annotationSize));
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.position(), annotationSize));
            contentTable.addCell(tableUtil.createContentCellRowSpan(annotationCell, annotationSize));

            contentTable.addCell(tableUtil.createContentCellRowSpan(new Paragraph(
                    variant.readDepth().alleleReadCount() + " / ").setFont(reportResources.fontBold())
                    .add(new Text(String.valueOf(variant.readDepth().totalReadCount())).setFont(reportResources.fontRegular()))
                    .setTextAlignment(TextAlignment.CENTER), annotationSize));
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.copies(), annotationSize).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.tVaf(), annotationSize).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.biallelic(),
                    annotationSize).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.hotspot().display, annotationSize)
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.driver().display,
                    annotationSize)).setTextAlignment(TextAlignment.CENTER);
            hasNotifiableGermlineVariant.add(variant.hasNotifiableGermlineVariant());
            hasPhasedVariant.add(variant.hasPhasedVariant());
        }

        contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                .add(new Paragraph("\nVariant annotation is by default based on the canonical transcript. In case another transcript "
                        + "is more commonly used in routine practice, this annotation is also provided.").addStyle(reportResources.subTextStyle()
                        .setTextAlignment(TextAlignment.LEFT))));

        if (hasNotifiableGermlineVariant.contains(true)) {
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n# Marked variant(s) are also present in the germline of the patient. Referral to a genetic "
                            + "specialist should be advised.").addStyle(reportResources.subTextStyle()
                            .setTextAlignment(TextAlignment.LEFT))));
        }

        if (hasPhasedVariant.contains(true)) {
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n+ Marked protein (p.) annotation is based on multiple phased variants.").addStyle(reportResources.subTextStyle()
                            .setTextAlignment(TextAlignment.LEFT))));
        }

        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createGainsAndLossesTable(@NotNull List<ObservedGainsLosses> gainsAndLosses) {
        String title = "Tumor observed gains & losses";
        if (gainsAndLosses.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[]{80, 80, 100, 80, 60, 60, 150},
                new Cell[]{tableUtil.createHeaderCell("Chromosome"), tableUtil.createHeaderCell("Region"),
                        tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Type"), tableUtil.createHeaderCell("min copies"),
                        tableUtil.createHeaderCell("max copies"),
                        tableUtil.createHeaderCell("Chromosome arm copies").setTextAlignment(TextAlignment.CENTER)},
                ReportResources.CONTENT_WIDTH_WIDE);

        for (ObservedGainsLosses gainLoss : gainsAndLosses) {
            contentTable.addCell(tableUtil.createContentCell(gainLoss.chromosome()));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.region()));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.gene()));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.type().display));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.minCopies()).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.maxCopies()).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.chromosomeArmCopies()).setTextAlignment(TextAlignment.CENTER));
        }

        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createHomozygousDisruptionsTable(@NotNull List<ObservedHomozygousDisruption> homozygousDisruptions) {
        String title = "Tumor observed homozygous disruptions";
        String subtitle = "Complete loss of wild type allele";
        if (homozygousDisruptions.isEmpty()) {
            return tableUtil.createNoneReportTable(title, subtitle, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[]{80, 80, 100},
                new Cell[]{tableUtil.createHeaderCell("Chromosome"), tableUtil.createHeaderCell("Region"),
                        tableUtil.createHeaderCell("Gene")},
                ReportResources.CONTENT_WIDTH_WIDE);

        for (ObservedHomozygousDisruption homozygousDisruption : homozygousDisruptions) {
            contentTable.addCell(tableUtil.createContentCell(homozygousDisruption.chromosome()));
            contentTable.addCell(tableUtil.createContentCell(homozygousDisruption.region()));
            contentTable.addCell(tableUtil.createContentCell(homozygousDisruption.gene()));
        }

        return tableUtil.createWrappingReportTable(title, subtitle, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createLOHTable(@NotNull List<LohEvent> lohEvents, @NotNull String signature) {
        String title = Strings.EMPTY;
        if (signature.equals("HRD")) {
            title = "Interesting LOH events in case of HRD";
        } else if (signature.equals("MSI")) {
            title = "Interesting LOH events in case of MSI";
        }

        if (lohEvents.isEmpty() || title.equals(Strings.EMPTY)) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table table = TableUtil.createReportContentTable(new float[]{1, 1, 1, 1},
                new Cell[]{tableUtil.createHeaderCell("Location"), tableUtil.createHeaderCell("Gene"),
                        tableUtil.createHeaderCell("Tumor minor allele copies").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Tumor copies").setTextAlignment(TextAlignment.CENTER)},
                ReportResources.CONTENT_WIDTH_WIDE);

        for (LohEvent lohEvent : lohEvents) {
            table.addCell(tableUtil.createContentCell(lohEvent.location()));
            table.addCell(tableUtil.createContentCell(lohEvent.gene()));
            table.addCell(tableUtil.createContentCell(lohEvent.tumorMinorAlleleCopies())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(tableUtil.createContentCell(lohEvent.tumorCopies())
                    .setTextAlignment(TextAlignment.CENTER));
        }

        return tableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createFusionsTable(@NotNull List<ObservedGeneFusion> fusions) {
        String title = "Tumor observed gene fusions";
        if (fusions.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[]{80, 70, 80, 80, 40, 40, 40, 65, 40},
                new Cell[]{tableUtil.createHeaderCell("Fusion"),
                        tableUtil.createHeaderCell("Type").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("5' Transcript"), tableUtil.createHeaderCell("3' Transcript"),
                        tableUtil.createHeaderCell("5' End"), tableUtil.createHeaderCell("3' Start"),
                        tableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Phasing").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER)},
                ReportResources.CONTENT_WIDTH_WIDE);

        for (ObservedGeneFusion fusion : fusions) {
            contentTable.addCell(tableUtil.createContentCell(fusion.fiveGene() + " - " + fusion.threeGene()));
            contentTable.addCell(tableUtil.createContentCell(fusion.type().type).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(geneFusions.fusionContentType(fusion.type(), fusion.fiveGene(), fusion.fivePromiscuousTranscript()));
            contentTable.addCell(geneFusions.fusionContentType(fusion.type(), fusion.threeGene(), fusion.threePromiscuousTranscript()));
            contentTable.addCell(tableUtil.createContentCell(fusion.threePromiscuousStart()));
            contentTable.addCell(tableUtil.createContentCell(fusion.fivePromiscuousEnd()));
            contentTable.addCell(tableUtil.createContentCell(fusion.copies())
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(fusion.phasing().type).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(fusion.driver().value).setTextAlignment(TextAlignment.CENTER));
        }

        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createDisruptionsTable(@NotNull List<ObservedGeneDisruption> disruptions) {
        String title = "Tumor observed gene disruptions";
        if (disruptions.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[]{60, 50, 100, 50, 80, 85, 85},
                new Cell[]{tableUtil.createHeaderCell("Location"), tableUtil.createHeaderCell("Gene"),
                        tableUtil.createHeaderCell("Disrupted range"),
                        tableUtil.createHeaderCell("Type").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Cluster ID").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Disrupted copies").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Undisrupted copies").setTextAlignment(TextAlignment.CENTER)},
                ReportResources.CONTENT_WIDTH_WIDE);

        for (ObservedGeneDisruption disruption : disruptions) {
            contentTable.addCell(tableUtil.createContentCell(disruption.location()));
            contentTable.addCell(tableUtil.createContentCell(disruption.gene()));
            contentTable.addCell(tableUtil.createContentCell(disruption.location()));
            contentTable.addCell(tableUtil.createContentCell(disruption.disruptionType())).setTextAlignment(TextAlignment.CENTER);
            contentTable.addCell(tableUtil.createContentCell(String.valueOf(disruption.clusterId()))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(disruption.disruptedCopies())
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(disruption.undisruptedCopies()).setTextAlignment(TextAlignment.CENTER));
        }
        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createHlaTable(@NotNull List<HlaAllele> hlaAlleles, @NotNull String hlaQc) {
        String title = "HLA Alleles";
        Table table = TableUtil.createReportContentTable(new float[]{10, 10, 10, 10, 10, 10},
                new Cell[]{tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Germline allele"),
                        tableUtil.createHeaderCell("Germline copies"), tableUtil.createHeaderCell("Tumor copies"),
                        tableUtil.createHeaderCell("Number somatic mutations*"),
                        tableUtil.createHeaderCell("Interpretation: presence in tumor")},
                ReportResources.CONTENT_WIDTH_WIDE);
        if (!hlaQc.equals("PASS")) {
            String noConsent = "The QC of the HLA types do not meet the QC cut-offs";
            return tableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN,
                    ReportResources.CONTENT_WIDTH_WIDE);
        } else if (hlaAlleles.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            //TODO check correctness layout
            for (HlaAllele hlaAllele : hlaAlleles) {
                table.addCell(tableUtil.createTransparentCell(hlaAllele.gene()));
                table.addCell(tableUtil.createTransparentCell(hlaAllele.germlineAllele()));
                table.addCell(tableUtil.createTransparentCell(hlaAllele.germlineCopies()));
                table.addCell(tableUtil.createTransparentCell(hlaAllele.tumorCopies()));
                table.addCell(tableUtil.createTransparentCell(hlaAllele.numberSomaticMutations()));
                table.addCell(tableUtil.createTransparentCell(hlaAllele.interpretationPresenceInTumor()));
            }
        }

        table.addCell(TableUtil.createLayoutCell(1, table.getNumberOfColumns())
                .add(new Paragraph("\n *When phasing is unclear, the mutation will be counted in both alleles as 0.5."
                        + " Copy number of detected mutations can be found in the tumor observed variants table.").addStyle(reportResources.subTextStyle()
                        .setTextAlignment(TextAlignment.CENTER))));
        return tableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createVirusTable(@NotNull List<ObservedViralInsertion> viruses) {
        String title = "Tumor observed viral insertions";

        if (viruses.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Table contentTable = TableUtil.createReportContentTable(new float[]{150, 160, 100, 40},
                    new Cell[]{tableUtil.createHeaderCell("Virus"),
                            tableUtil.createHeaderCell("Number of detected integration sites").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Viral coverage").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER)},
                    ReportResources.CONTENT_WIDTH_WIDE);

            for (ObservedViralInsertion virus : viruses) {
                contentTable.addCell(tableUtil.createContentCell(virus.virus()));
                contentTable.addCell(tableUtil.createContentCell(virus.detectedIntegrationSites()).setTextAlignment(TextAlignment.CENTER));
                contentTable.addCell(tableUtil.createContentCell(Double.toString(virus.viralCoveragePercentage()))
                        .setTextAlignment(TextAlignment.CENTER));
                contentTable.addCell(tableUtil.createContentCell(virus.virusDriverInterpretation().display)
                        .setTextAlignment(TextAlignment.CENTER));
            }

            return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        }
    }

    @NotNull
    private Table createPharmacogeneticsGenotypesTable(@NotNull List<com.hartwig.oncoact.patientreporter.model.Pharmacogenetics> pharmacogeneticsList) {
        String title = "Pharmacogenetics";

        if (pharmacogeneticsList.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Table contentTable = TableUtil.createReportContentTable(new float[]{60, 60, 60, 100, 60},
                    new Cell[]{tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Genotype"),
                            tableUtil.createHeaderCell("Function"), tableUtil.createHeaderCell("Linked drugs"),
                            tableUtil.createHeaderCell("Source")},
                    ReportResources.CONTENT_WIDTH_WIDE);

            //TODO check correctness layout
            for (com.hartwig.oncoact.patientreporter.model.Pharmacogenetics pharmacogeneticsGenotype : pharmacogeneticsList) {
                contentTable.addCell(tableUtil.createContentCell(pharmacogeneticsGenotype.gene()));
                contentTable.addCell(tableUtil.createTransparentCell(pharmacogeneticsGenotype.genotype()));
                contentTable.addCell(tableUtil.createTransparentCell(pharmacogeneticsGenotype.function()));
                contentTable.addCell(tableUtil.createTransparentCell(pharmacogeneticsGenotype.linkedDrugs()));
                contentTable.addCell(tableUtil.createTransparentCell(new Paragraph(pharmacogeneticsGenotype.source().name()).addStyle(
                                reportResources.dataHighlightLinksStyle()))
                        .setAction(PdfAction.createURI(pharmacogeneticsGenotype.source().url())));
            }

            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n #Note that we do not separately call the *36 allele. Dutch clinical "
                            + "guidelines consider the *36 allele to be clinically equivalent to the *1 allele.").addStyle(reportResources.subTextStyle()
                            .setTextAlignment(TextAlignment.LEFT))));

            return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        }
    }
}