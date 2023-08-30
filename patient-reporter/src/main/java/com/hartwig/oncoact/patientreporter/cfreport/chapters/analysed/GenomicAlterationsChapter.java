package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.algo.InterpretPurpleGeneCopyNumbers;
import com.hartwig.oncoact.patientreporter.cfreport.MathUtil;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.InlineBarChart;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.GainsAndLosses;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneDisruptions;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneFusions;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.HLAAllele;
import com.hartwig.oncoact.patientreporter.cfreport.data.HomozygousDisruptions;
import com.hartwig.oncoact.patientreporter.cfreport.data.LohGenes;
import com.hartwig.oncoact.patientreporter.cfreport.data.Pharmacogenetics;
import com.hartwig.oncoact.patientreporter.cfreport.data.SomaticVariants;
import com.hartwig.oncoact.patientreporter.cfreport.data.TumorPurity;
import com.hartwig.oncoact.patientreporter.cfreport.data.ViralPresence;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class GenomicAlterationsChapter implements ReportChapter {

    // TODO Remove this toggle-off once we can remove position (blocked by DEV-810)
    private static final boolean DISPLAY_CLONAL_COLUMN = false;

    @NotNull
    private final AnalysedPatientReport patientReport;

    @NotNull
    private final ReportResources reportResources;

    private final TableUtil tableUtil;

    private final GeneFusions geneFusions;

    public GenomicAlterationsChapter(@NotNull final AnalysedPatientReport patientReport, @NotNull final ReportResources reportResources) {
        this.patientReport = patientReport;
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
        return "Genomic alteration details";
    }

    @Override
    public void render(@NotNull Document reportDocument) {
        GenomicAnalysis genomicAnalysis = patientReport.genomicAnalysis();
        boolean hasReliablePurity = genomicAnalysis.hasReliablePurity();

        reportDocument.add(createPloidyPloidyTable(genomicAnalysis.averageTumorPloidy(),
                genomicAnalysis.impliedPurity(),
                hasReliablePurity));

        reportDocument.add(createTumorVariantsTable(genomicAnalysis.reportableVariants(),
                genomicAnalysis.notifyGermlineStatusPerVariant(),
                hasReliablePurity));

        reportDocument.add(createGainsAndLossesTable(genomicAnalysis.gainsAndLosses(),
                hasReliablePurity,
                genomicAnalysis.cnPerChromosome()));
        reportDocument.add(createFusionsTable(genomicAnalysis.geneFusions(), hasReliablePurity));
        reportDocument.add(createHomozygousDisruptionsTable(genomicAnalysis.homozygousDisruptions()));

        if (genomicAnalysis.hrdStatus() == ChordStatus.HR_DEFICIENT) {
            reportDocument.add(createLOHTable(genomicAnalysis.suspectGeneCopyNumbersWithLOH(), "HRD"));
        }
        if (genomicAnalysis.microsatelliteStatus() == PurpleMicrosatelliteStatus.MSI) {
            reportDocument.add(createLOHTable(genomicAnalysis.suspectGeneCopyNumbersWithLOH(), "MSI"));
        }

        reportDocument.add(createDisruptionsTable(genomicAnalysis.geneDisruptions(), hasReliablePurity));
        reportDocument.add(createVirusTable(genomicAnalysis.reportableViruses()));
        reportDocument.add(createHlaTable(patientReport.hlaAllelesReportingData(), hasReliablePurity));
        reportDocument.add(createPharmacogeneticsGenotypesTable(patientReport.pharmacogeneticsGenotypes()));
    }

    @NotNull
    private Table createPloidyPloidyTable(double ploidy, double purity, boolean hasReliablePurity) {
        String title = "Tumor purity & ploidy";

        Table contentTable =
                TableUtil.createReportContentTable(new float[] { 90, 95, 50 }, new Cell[] {}, ReportResources.CONTENT_WIDTH_WIDE_SMALL);

        double impliedPurityPercentage = MathUtil.mapPercentage(purity, TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX);
        renderTumorPurity(hasReliablePurity,
                Formats.formatPercentage(impliedPurityPercentage),
                purity,
                TumorPurity.RANGE_MIN,
                TumorPurity.RANGE_MAX,
                contentTable);

        String copyNumber = GeneUtil.copyNumberToString(ploidy, hasReliablePurity);
        contentTable.addCell(tableUtil.createContentCell("Average tumor ploidy"));
        if (copyNumber.equals(Formats.NA_STRING)) {
            contentTable.addCell(tableUtil.createContentCell(copyNumber).setTextAlignment(TextAlignment.CENTER));
        } else {
            contentTable.addCell(tableUtil.createContentCellPurityPloidy(copyNumber).setTextAlignment(TextAlignment.CENTER));

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
    private Table createTumorVariantsTable(@NotNull List<ReportableVariant> reportableVariants,
            @NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant, boolean hasReliablePurity) {
        String title = "Tumor specific variants";
        if (reportableVariants.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable;
        if (DISPLAY_CLONAL_COLUMN) {
            contentTable = TableUtil.createReportContentTable(new float[] { 60, 70, 150, 60, 40, 30, 60, 60, 50, 50 },
                    new Cell[] { tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Position"),
                            tableUtil.createHeaderCell("Variant"),
                            tableUtil.createHeaderCell("Read depth").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("tVAF").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Biallelic").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Hotspot").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Clonal").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER) },
                    ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            contentTable = TableUtil.createReportContentTable(new float[] { 60, 70, 150, 60, 40, 30, 60, 60, 50 },
                    new Cell[] { tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Position"),
                            tableUtil.createHeaderCell("Variant"),
                            tableUtil.createHeaderCell("Read depth").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("tVAF").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Biallelic").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Hotspot").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER) },
                    ReportResources.CONTENT_WIDTH_WIDE);
        }

        for (ReportableVariant variant : SomaticVariants.sort(reportableVariants)) {
            List<String> annotationList = SomaticVariants.determineVariantAnnotations(variant.canonicalHgvsCodingImpact(),
                    variant.canonicalHgvsProteinImpact(),
                    variant.otherImpactClinical());

            Cell annotationCell = new Cell();
            for (String annotation : annotationList) {
                annotationCell.add(new Paragraph(annotation));
            }
            int annotationSize = annotationList.size();

            contentTable.addCell(tableUtil.createContentCellRowSpan(SomaticVariants.geneDisplayString(variant,
                    notifyGermlineStatusPerVariant.get(variant),
                    variant.canonicalEffect()), annotationSize));
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.gDNA(), annotationSize));
            contentTable.addCell(tableUtil.createContentCellRowSpan(annotationCell, annotationSize));

            contentTable.addCell(tableUtil.createContentCellRowSpan(new Paragraph(
                    variant.alleleReadCount() + " / ").setFont(reportResources.fontBold())
                    .add(new Text(String.valueOf(variant.totalReadCount())).setFont(reportResources.fontRegular()))
                    .setTextAlignment(TextAlignment.CENTER), annotationSize));
            contentTable.addCell(tableUtil.createContentCellRowSpan(GeneUtil.roundCopyNumber(variant.totalCopyNumber(), hasReliablePurity),
                    annotationSize).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCellRowSpan(SomaticVariants.tVAFString(variant.tVAF(),
                    hasReliablePurity,
                    variant.totalCopyNumber()), annotationSize).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCellRowSpan(SomaticVariants.biallelicString(variant.biallelic(), hasReliablePurity),
                    annotationSize).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCellRowSpan(SomaticVariants.hotspotString(variant.hotspot()), annotationSize)
                    .setTextAlignment(TextAlignment.CENTER));
            if (DISPLAY_CLONAL_COLUMN) {
                contentTable.addCell(tableUtil.createContentCellRowSpan(SomaticVariants.clonalString(variant.clonalLikelihood()),
                        annotationSize).setTextAlignment(TextAlignment.CENTER));
            }
            contentTable.addCell(tableUtil.createContentCellRowSpan(variant.driverLikelihoodInterpretation().display(), annotationSize))
                    .setTextAlignment(TextAlignment.CENTER);
        }

        contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                .add(new Paragraph("\nVariant annotation is by default based on the canonical transcript. In case another"
                        + " transcript is more commonly used in routine practice, this annotation is provided in addition.").addStyle(
                        reportResources.subTextStyle())));

        if (SomaticVariants.hasNotifiableGermlineVariant(notifyGermlineStatusPerVariant)) {
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n# Marked variant(s) are also present in the germline of the patient. Referral to a genetic "
                            + "specialist should be advised.").addStyle(reportResources.subTextStyle())));
        }

        if (SomaticVariants.hasPhasedVariant(reportableVariants)) {
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n+ Marked protein (p.) annotation is based on multiple phased variants.").addStyle(reportResources.subTextStyle())));
        }

        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createGainsAndLossesTable(@NotNull List<PurpleGainLoss> gainsAndLosses, boolean hasReliablePurity,
            @NotNull List<CnPerChromosomeArmData> cnPerChromosome) {
        String title = "Tumor specific gains & losses";
        if (gainsAndLosses.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 80, 80, 100, 80, 60, 60, 150 },
                new Cell[] { tableUtil.createHeaderCell("Chromosome"), tableUtil.createHeaderCell("Region"),
                        tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Type"), tableUtil.createHeaderCell("min copies"),
                        tableUtil.createHeaderCell("max copies"),
                        tableUtil.createHeaderCell("Chromosome arm copies").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        List<PurpleGainLoss> sortedGainsAndLosses = GainsAndLosses.sort(gainsAndLosses);
        for (PurpleGainLoss gainLoss : sortedGainsAndLosses) {
            contentTable.addCell(tableUtil.createContentCell(gainLoss.chromosome()));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.chromosomeBand()));
            contentTable.addCell(tableUtil.createContentCell(gainLoss.gene()));
            contentTable.addCell(tableUtil.createContentCell(GainsAndLosses.interpretation(gainLoss)));
            contentTable.addCell(tableUtil.createContentCell(GeneUtil.roundCopyNumber(gainLoss.minCopies(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(GeneUtil.roundCopyNumber(gainLoss.maxCopies(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, gainLoss))
                    .setTextAlignment(TextAlignment.CENTER));
        }

        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createHomozygousDisruptionsTable(@NotNull List<HomozygousDisruption> homozygousDisruptions) {
        String title = "Tumor specific homozygous disruptions";
        String subtitle = "Complete loss of wild type allele";
        if (homozygousDisruptions.isEmpty()) {
            return tableUtil.createNoneReportTable(title, subtitle, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 80, 80, 100 },
                new Cell[] { tableUtil.createHeaderCell("Chromosome"), tableUtil.createHeaderCell("Region"),
                        tableUtil.createHeaderCell("Gene") },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (HomozygousDisruption homozygousDisruption : HomozygousDisruptions.sort(homozygousDisruptions)) {
            contentTable.addCell(tableUtil.createContentCell(homozygousDisruption.chromosome()));
            contentTable.addCell(tableUtil.createContentCell(homozygousDisruption.chromosomeBand()));
            contentTable.addCell(tableUtil.createContentCell(homozygousDisruption.gene()));
        }

        return tableUtil.createWrappingReportTable(title, subtitle, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createLOHTable(@NotNull List<InterpretPurpleGeneCopyNumbers> suspectGeneCopyNumbersWithLOH, @NotNull String signature) {
        String title = Strings.EMPTY;
        if (signature.equals("HRD")) {
            title = "Interesting LOH events in case of HRD";
        } else if (signature.equals("MSI")) {
            title = "Interesting LOH events in case of MSI";
        }

        if (suspectGeneCopyNumbersWithLOH.isEmpty() || title.equals(Strings.EMPTY)) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table table = TableUtil.createReportContentTable(new float[] { 1, 1, 1, 1 },
                new Cell[] { tableUtil.createHeaderCell("Location"), tableUtil.createHeaderCell("Gene"),
                        tableUtil.createHeaderCell("Tumor minor allele copies").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Tumor copies").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (InterpretPurpleGeneCopyNumbers LOHgenes : LohGenes.sort(suspectGeneCopyNumbersWithLOH)) {
            table.addCell(tableUtil.createContentCell(LOHgenes.chromosome() + LOHgenes.chromosomeBand()));
            table.addCell(tableUtil.createContentCell(LOHgenes.geneName()));
            table.addCell(tableUtil.createContentCell(GeneUtil.roundCopyNumber(LOHgenes.minMinorAlleleCopyNumber()))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(tableUtil.createContentCell(GeneUtil.roundCopyNumber(LOHgenes.minCopyNumber()))
                    .setTextAlignment(TextAlignment.CENTER));
        }

        return tableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createFusionsTable(@NotNull List<LinxFusion> fusions, boolean hasReliablePurity) {
        String title = "Tumor specific gene fusions";
        if (fusions.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 80, 70, 80, 80, 40, 40, 40, 65, 40 },
                new Cell[] { tableUtil.createHeaderCell("Fusion"),
                        tableUtil.createHeaderCell("Type").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("5' Transcript"), tableUtil.createHeaderCell("3' Transcript"),
                        tableUtil.createHeaderCell("5' End"), tableUtil.createHeaderCell("3' Start"),
                        tableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Phasing").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (LinxFusion fusion : GeneFusions.sort(fusions)) {
            contentTable.addCell(tableUtil.createContentCell(GeneFusions.name(fusion)));
            contentTable.addCell(tableUtil.createContentCell(GeneFusions.type(fusion)).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(geneFusions.fusionContentType(fusion.reportedType(), fusion.geneStart(), fusion.geneTranscriptStart()));
            contentTable.addCell(geneFusions.fusionContentType(fusion.reportedType(), fusion.geneEnd(), fusion.geneTranscriptEnd()));
            contentTable.addCell(tableUtil.createContentCell(fusion.geneContextStart()));
            contentTable.addCell(tableUtil.createContentCell(fusion.geneContextEnd()));
            contentTable.addCell(tableUtil.createContentCell(GeneUtil.roundCopyNumber(fusion.junctionCopyNumber(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(GeneFusions.phased(fusion)).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(GeneFusions.likelihood(fusion)).setTextAlignment(TextAlignment.CENTER));
        }

        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createDisruptionsTable(@NotNull List<GeneDisruption> disruptions, boolean hasReliablePurity) {
        String title = "Tumor specific gene disruptions";
        if (disruptions.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 60, 50, 100, 50, 80, 85, 85 },
                new Cell[] { tableUtil.createHeaderCell("Location"), tableUtil.createHeaderCell("Gene"),
                        tableUtil.createHeaderCell("Disrupted range"),
                        tableUtil.createHeaderCell("Type").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Cluster ID").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Disrupted copies").setTextAlignment(TextAlignment.CENTER),
                        tableUtil.createHeaderCell("Undisrupted copies").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (GeneDisruption disruption : GeneDisruptions.sort(disruptions)) {
            contentTable.addCell(tableUtil.createContentCell(disruption.location()));
            contentTable.addCell(tableUtil.createContentCell(disruption.gene()));
            contentTable.addCell(tableUtil.createContentCell(disruption.range()));
            contentTable.addCell(tableUtil.createContentCell(disruption.type())).setTextAlignment(TextAlignment.CENTER);
            contentTable.addCell(tableUtil.createContentCell(String.valueOf(disruption.clusterId()))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(GeneUtil.roundCopyNumber(disruption.junctionCopyNumber(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(tableUtil.createContentCell(GeneUtil.roundCopyNumber(disruption.undisruptedCopyNumber(),
                    hasReliablePurity)).setTextAlignment(TextAlignment.CENTER));
        }
        return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createHlaTable(@NotNull HlaAllelesReportingData lilac, boolean hasReliablePurity) {
        String title = "HLA Alleles";
        Table table = TableUtil.createReportContentTable(new float[] { 10, 10, 10, 10, 10, 10 },
                new Cell[] { tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Germline allele"),
                        tableUtil.createHeaderCell("Germline copies"), tableUtil.createHeaderCell("Tumor copies"),
                        tableUtil.createHeaderCell("Number somatic mutations*"),
                        tableUtil.createHeaderCell("Interpretation: presence in tumor") },
                ReportResources.CONTENT_WIDTH_WIDE);
        if (!lilac.hlaQC().equals("PASS")) {
            String noConsent = "The QC of the HLA types do not meet the QC cut-offs";
            return tableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN,
                    ReportResources.CONTENT_WIDTH_WIDE);
        } else if (lilac.hlaAllelesReporting().isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Set<String> sortedAlleles = Sets.newTreeSet(lilac.hlaAllelesReporting().keySet());
            for (String sortAllele : sortedAlleles) {
                List<HlaReporting> allele = lilac.hlaAllelesReporting().get(sortAllele);
                table.addCell(tableUtil.createContentCell(sortAllele));

                Table tableGermlineAllele = new Table(new float[] { 1 });
                Table tableGermlineCopies = new Table(new float[] { 1 });
                Table tableTumorCopies = new Table(new float[] { 1 });
                Table tableSomaticMutations = new Table(new float[] { 1 });
                Table tablePresenceInTumor = new Table(new float[] { 1 });

                for (HlaReporting hlaAlleleReporting : HLAAllele.sort(allele)) {
                    tableGermlineAllele.addCell(tableUtil.createTransparentCell(hlaAlleleReporting.hlaAllele().germlineAllele()));
                    tableGermlineCopies.addCell(tableUtil.createTransparentCell(GeneUtil.roundCopyNumber(hlaAlleleReporting.germlineCopies(),
                            hasReliablePurity)));
                    tableTumorCopies.addCell(tableUtil.createTransparentCell(GeneUtil.roundCopyNumber(hlaAlleleReporting.tumorCopies(),
                            hasReliablePurity)));
                    tableSomaticMutations.addCell(tableUtil.createTransparentCell(hlaAlleleReporting.somaticMutations()));
                    tablePresenceInTumor.addCell(tableUtil.createTransparentCell(hlaAlleleReporting.interpretation()));
                }

                table.addCell(tableUtil.createContentCell(tableGermlineAllele));
                table.addCell(tableUtil.createContentCell(tableGermlineCopies));
                table.addCell(tableUtil.createContentCell(tableTumorCopies));
                table.addCell(tableUtil.createContentCell(tableSomaticMutations));
                table.addCell(tableUtil.createContentCell(tablePresenceInTumor));
            }
        }

        table.addCell(TableUtil.createLayoutCell(1, table.getNumberOfColumns())
                .add(new Paragraph("\n *When phasing is unclear the mutation will be counted in both alleles as 0.5. Copy number of"
                        + " detected mutations can be found in the somatic variant table.").addStyle(reportResources.subTextStyle()
                        .setTextAlignment(TextAlignment.CENTER))));
        return tableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createVirusTable(@NotNull List<AnnotatedVirus> viruses) {
        String title = "Tumor specific viral insertions";

        if (viruses.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Table contentTable = TableUtil.createReportContentTable(new float[] { 150, 160, 100, 40 },
                    new Cell[] { tableUtil.createHeaderCell("Virus"),
                            tableUtil.createHeaderCell("Number of detected integration sites").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Viral coverage").setTextAlignment(TextAlignment.CENTER),
                            tableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER) },
                    ReportResources.CONTENT_WIDTH_WIDE);

            for (AnnotatedVirus virus : viruses) {
                contentTable.addCell(tableUtil.createContentCell(ViralPresence.interpretVirusName(virus.name(),
                        virus.interpretation(),
                        virus.virusDriverLikelihoodType())));
                contentTable.addCell(tableUtil.createContentCell(ViralPresence.integrations(virus)).setTextAlignment(TextAlignment.CENTER));
                contentTable.addCell(tableUtil.createContentCell(ViralPresence.percentageCovered(virus))
                        .setTextAlignment(TextAlignment.CENTER));
                contentTable.addCell(tableUtil.createContentCell(ViralPresence.driverLikelihood(virus))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        }
    }

    @NotNull
    private Table createPharmacogeneticsGenotypesTable(@NotNull Map<String, List<PeachGenotype>> pharmacogeneticsMap) {
        String title = "Pharmacogenetics";

        if (pharmacogeneticsMap.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Table contentTable = TableUtil.createReportContentTable(new float[] { 60, 60, 60, 100, 60 },
                    new Cell[] { tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Genotype"),
                            tableUtil.createHeaderCell("Function"), tableUtil.createHeaderCell("Linked drugs"),
                            tableUtil.createHeaderCell("Source") },
                    ReportResources.CONTENT_WIDTH_WIDE);

            Set<String> sortedPharmacogenetics = Sets.newTreeSet(pharmacogeneticsMap.keySet());
            for (String sortPharmacogenetics : sortedPharmacogenetics) {
                List<PeachGenotype> pharmacogeneticsGenotypeList = pharmacogeneticsMap.get(sortPharmacogenetics);
                contentTable.addCell(tableUtil.createContentCell(sortPharmacogenetics.equals("UGT1A1")
                        ? sortPharmacogenetics + "#"
                        : sortPharmacogenetics));

                Table tableGenotype = new Table(new float[] { 1 });
                Table tableFunction = new Table(new float[] { 1 });
                Table tableLinkedDrugs = new Table(new float[] { 1 });
                Table tableSource = new Table(new float[] { 1 });

                for (PeachGenotype pharmacogeneticsGenotype : pharmacogeneticsGenotypeList) {
                    tableGenotype.addCell(tableUtil.createTransparentCell(pharmacogeneticsGenotype.haplotype()));
                    tableFunction.addCell(tableUtil.createTransparentCell(pharmacogeneticsGenotype.function()));
                    tableLinkedDrugs.addCell(tableUtil.createTransparentCell(pharmacogeneticsGenotype.linkedDrugs()));
                    tableSource.addCell(tableUtil.createTransparentCell(new Paragraph(Pharmacogenetics.sourceName(pharmacogeneticsGenotype.urlPrescriptionInfo())).addStyle(
                                    reportResources.dataHighlightLinksStyle()))
                            .setAction(PdfAction.createURI(Pharmacogenetics.url(pharmacogeneticsGenotype.urlPrescriptionInfo()))));
                }

                contentTable.addCell(tableUtil.createContentCell(tableGenotype));
                contentTable.addCell(tableUtil.createContentCell(tableFunction));
                contentTable.addCell(tableUtil.createContentCell(tableLinkedDrugs));
                contentTable.addCell(tableUtil.createContentCell(tableSource));
            }
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n #Note that we do not separately call the *36 allele. Dutch clinical "
                            + "guidelines consider the *36 allele to be clinically equivalent to the *1 allele.").addStyle(reportResources.subTextStyle()
                            .setTextAlignment(TextAlignment.CENTER))));

            return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        }
    }
}