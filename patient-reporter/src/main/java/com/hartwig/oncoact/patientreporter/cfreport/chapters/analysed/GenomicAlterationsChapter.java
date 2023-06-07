package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.lims.Lims;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.oncoact.patientreporter.SampleReport;
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
    private final SampleReport sampleReport;

    public GenomicAlterationsChapter(@NotNull final AnalysedPatientReport patientReport, @NotNull final SampleReport sampleReport) {
        this.patientReport = patientReport;
        this.sampleReport = sampleReport;
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
        reportDocument.add(createVirusTable(genomicAnalysis.reportableViruses(), sampleReport.reportViralPresence()));
        reportDocument.add(createHlaTable(patientReport.hlaAllelesReportingData(), hasReliablePurity));
        reportDocument.add(createPharmacogeneticsGenotypesTable(patientReport.pharmacogeneticsGenotypes(),
                sampleReport.reportPharmogenetics()));
    }

    @NotNull
    private static Table createPloidyPloidyTable(double ploidy, double purity, boolean hasReliablePurity) {
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
        contentTable.addCell(TableUtil.createContentCell("Average tumor ploidy"));
        if (copyNumber.equals(Formats.NA_STRING)) {
            contentTable.addCell(TableUtil.createContentCell(copyNumber).setTextAlignment(TextAlignment.CENTER));
        } else {
            contentTable.addCell(TableUtil.createContentCellPurityPloidy(copyNumber).setTextAlignment(TextAlignment.CENTER));

        }
        contentTable.addCell(TableUtil.createContentCell(Strings.EMPTY));

        return TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static InlineBarChart createInlineBarChart(double value, double min, double max) {
        InlineBarChart chart = new InlineBarChart(value, min, max);
        chart.setWidth(41);
        chart.setHeight(6);
        return chart;
    }

    private static void renderTumorPurity(boolean hasReliablePurity, @NotNull String valueLabel, double value, double min, double max,
            @NotNull Table table) {

        String label = "Tumor purity";
        table.addCell(TableUtil.createContentCell(label));

        if (hasReliablePurity) {
            table.addCell(TableUtil.createContentCellPurityPloidy(valueLabel).setTextAlignment(TextAlignment.CENTER));
            table.addCell(TableUtil.createContentCell(createInlineBarChart(value, min, max))
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER));
        } else {
            table.addCell(TableUtil.createContentCell(Lims.PURITY_NOT_RELIABLE_STRING));
            table.addCell(TableUtil.createContentCell(Strings.EMPTY));
        }
    }

    @NotNull
    private static Table createTumorVariantsTable(@NotNull List<ReportableVariant> reportableVariants,
            @NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant, boolean hasReliablePurity) {
        String title = "Tumor specific variants";
        if (reportableVariants.isEmpty()) {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable;
        if (DISPLAY_CLONAL_COLUMN) {
            contentTable = TableUtil.createReportContentTable(new float[] { 60, 70, 80, 70, 60, 40, 30, 60, 60, 50, 50 },
                    new Cell[] { TableUtil.createHeaderCell("Gene"), TableUtil.createHeaderCell("Position"),
                            TableUtil.createHeaderCell("Variant"), TableUtil.createHeaderCell("Protein"),
                            TableUtil.createHeaderCell("Read depth").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("tVAF").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Biallelic").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Hotspot").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Clonal").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER) },
                    ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            contentTable = TableUtil.createReportContentTable(new float[] { 60, 70, 80, 70, 60, 40, 30, 60, 60, 50 },
                    new Cell[] { TableUtil.createHeaderCell("Gene"), TableUtil.createHeaderCell("Position"),
                            TableUtil.createHeaderCell("Variant"), TableUtil.createHeaderCell("Protein"),
                            TableUtil.createHeaderCell("Read depth").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("tVAF").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Biallelic").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Hotspot").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER) },
                    ReportResources.CONTENT_WIDTH_WIDE);
        }

        for (ReportableVariant variant : SomaticVariants.sort(reportableVariants)) {
            contentTable.addCell(TableUtil.createContentCell(SomaticVariants.geneDisplayString(variant,
                    notifyGermlineStatusPerVariant.get(variant))));
            contentTable.addCell(TableUtil.createContentCell(variant.gDNA()));
            contentTable.addCell(TableUtil.createContentCell(SomaticVariants.variantDisplayString(variant.localPhaseSet(), variant.canonicalHgvsCodingImpact())));
            contentTable.addCell(TableUtil.createContentCell(SomaticVariants.proteinAnnotationDisplayString(variant.canonicalHgvsProteinImpact(),
                    variant.canonicalEffect())));
            contentTable.addCell(TableUtil.createContentCell(new Paragraph(
                    variant.alleleReadCount() + " / ").setFont(ReportResources.fontBold())
                    .add(new Text(String.valueOf(variant.totalReadCount())).setFont(ReportResources.fontRegular()))
                    .setTextAlignment(TextAlignment.CENTER)));
            contentTable.addCell(TableUtil.createContentCell(SomaticVariants.copyNumberString(variant.totalCopyNumber(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(SomaticVariants.tVAFString(variant.tVAF(),
                    hasReliablePurity,
                    variant.totalCopyNumber())).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(SomaticVariants.biallelicString(variant.biallelic(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(SomaticVariants.hotspotString(variant.hotspot()))
                    .setTextAlignment(TextAlignment.CENTER));
            if (DISPLAY_CLONAL_COLUMN) {
                contentTable.addCell(TableUtil.createContentCell(SomaticVariants.clonalString(variant.clonalLikelihood()))
                        .setTextAlignment(TextAlignment.CENTER));
            }
            contentTable.addCell(TableUtil.createContentCell(variant.driverLikelihoodInterpretation().display()))
                    .setTextAlignment(TextAlignment.CENTER);
        }

        if (SomaticVariants.hasNotifiableGermlineVariant(notifyGermlineStatusPerVariant)) {
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n# Marked variant(s) are also present in the germline of the patient. Referral to a genetic "
                            + "specialist should be advised.").addStyle(ReportResources.subTextStyle())));
        }

        if (SomaticVariants.hasPhasedVariant(reportableVariants)) {
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n+ Marked protein (p.) annotation is based on multiple phased variants.").addStyle(ReportResources.subTextStyle())));
        }

        if (SomaticVariants.hasVariantsInCis(reportableVariants)) {
            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n= Marked variants are present in cis").addStyle(ReportResources.subTextStyle())));
        }


        return TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static Table createGainsAndLossesTable(@NotNull List<PurpleGainLoss> gainsAndLosses, boolean hasReliablePurity,
            @NotNull List<CnPerChromosomeArmData> cnPerChromosome) {
        String title = "Tumor specific gains & losses";
        if (gainsAndLosses.isEmpty()) {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 80, 80, 100, 80, 60, 60, 150 },
                new Cell[] { TableUtil.createHeaderCell("Chromosome"), TableUtil.createHeaderCell("Region"),
                        TableUtil.createHeaderCell("Gene"), TableUtil.createHeaderCell("Type"), TableUtil.createHeaderCell("min copies"),
                        TableUtil.createHeaderCell("max copies"),
                        TableUtil.createHeaderCell("Chromosome arm copies").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        List<PurpleGainLoss> sortedGainsAndLosses = GainsAndLosses.sort(gainsAndLosses);
        for (PurpleGainLoss gainLoss : sortedGainsAndLosses) {
            contentTable.addCell(TableUtil.createContentCell(gainLoss.chromosome()));
            contentTable.addCell(TableUtil.createContentCell(gainLoss.chromosomeBand()));
            contentTable.addCell(TableUtil.createContentCell(gainLoss.gene()));
            contentTable.addCell(TableUtil.createContentCell(GainsAndLosses.interpretation(gainLoss)));
            contentTable.addCell(TableUtil.createContentCell(GainsAndLosses.copyNumberString(gainLoss.minCopies(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(GainsAndLosses.copyNumberString(gainLoss.maxCopies(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, gainLoss))
                    .setTextAlignment(TextAlignment.CENTER));
        }

        return TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static Table createHomozygousDisruptionsTable(@NotNull List<HomozygousDisruption> homozygousDisruptions) {
        String title = "Tumor specific homozygous disruptions";
        String subtitle = "Complete loss of wild type allele";
        if (homozygousDisruptions.isEmpty()) {
            return TableUtil.createNoneReportTable(title, subtitle, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 80, 80, 100 },
                new Cell[] { TableUtil.createHeaderCell("Chromosome"), TableUtil.createHeaderCell("Region"),
                        TableUtil.createHeaderCell("Gene") },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (HomozygousDisruption homozygousDisruption : HomozygousDisruptions.sort(homozygousDisruptions)) {
            contentTable.addCell(TableUtil.createContentCell(homozygousDisruption.chromosome()));
            contentTable.addCell(TableUtil.createContentCell(homozygousDisruption.chromosomeBand()));
            contentTable.addCell(TableUtil.createContentCell(homozygousDisruption.gene()));
        }

        return TableUtil.createWrappingReportTable(title, subtitle, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static Table createLOHTable(@NotNull List<InterpretPurpleGeneCopyNumbers> suspectGeneCopyNumbersWithLOH, @NotNull String signature) {
        String title = Strings.EMPTY;
        if (signature.equals("HRD")) {
            title = "Interesting LOH events in case of HRD";
        } else if (signature.equals("MSI")) {
            title = "Interesting LOH events in case of MSI";
        }

        if (suspectGeneCopyNumbersWithLOH.isEmpty() || title.equals(Strings.EMPTY)) {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table table = TableUtil.createReportContentTable(new float[] { 1, 1, 1, 1 },
                new Cell[] { TableUtil.createHeaderCell("Location"), TableUtil.createHeaderCell("Gene"),
                        TableUtil.createHeaderCell("Tumor minor allele copies").setTextAlignment(TextAlignment.CENTER),
                        TableUtil.createHeaderCell("Tumor copies").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (InterpretPurpleGeneCopyNumbers LOHgenes : LohGenes.sort(suspectGeneCopyNumbersWithLOH)) {
            table.addCell(TableUtil.createContentCell(LOHgenes.chromosome() + LOHgenes.chromosomeBand()));
            table.addCell(TableUtil.createContentCell(LOHgenes.geneName()));
            table.addCell(TableUtil.createContentCell(LohGenes.round(LOHgenes.minMinorAlleleCopyNumber())).setTextAlignment(TextAlignment.CENTER));
            table.addCell(TableUtil.createContentCell(LohGenes.round(LOHgenes.minCopyNumber())).setTextAlignment(TextAlignment.CENTER));
        }

        return TableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static Table createFusionsTable(@NotNull List<LinxFusion> fusions, boolean hasReliablePurity) {
        String title = "Tumor specific gene fusions";
        if (fusions.isEmpty()) {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 80, 70, 80, 80, 40, 40, 40, 65, 40 },
                new Cell[] { TableUtil.createHeaderCell("Fusion"),
                        TableUtil.createHeaderCell("Type").setTextAlignment(TextAlignment.CENTER),
                        TableUtil.createHeaderCell("5' Transcript"), TableUtil.createHeaderCell("3' Transcript"),
                        TableUtil.createHeaderCell("5' End"), TableUtil.createHeaderCell("3' Start"),
                        TableUtil.createHeaderCell("Copies").setTextAlignment(TextAlignment.CENTER),
                        TableUtil.createHeaderCell("Phasing").setTextAlignment(TextAlignment.CENTER),
                        TableUtil.createHeaderCell("Driver").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (LinxFusion fusion : GeneFusions.sort(fusions)) {
            contentTable.addCell(TableUtil.createContentCell(GeneFusions.name(fusion)));
            contentTable.addCell(TableUtil.createContentCell(GeneFusions.type(fusion)).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(GeneFusions.fusionContentType(fusion.reportedType(), fusion.geneStart(), fusion.geneTranscriptStart()));
            contentTable.addCell(GeneFusions.fusionContentType(fusion.reportedType(), fusion.geneEnd(), fusion.geneTranscriptEnd()));
            contentTable.addCell(TableUtil.createContentCell(fusion.geneContextStart()));
            contentTable.addCell(TableUtil.createContentCell(fusion.geneContextEnd()));
            contentTable.addCell(TableUtil.createContentCell(GeneFusions.copyNumberString(fusion.junctionCopyNumber(), hasReliablePurity))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(GeneFusions.phased(fusion)).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(GeneFusions.likelihood(fusion)).setTextAlignment(TextAlignment.CENTER));
        }

        return TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static Table createDisruptionsTable(@NotNull List<GeneDisruption> disruptions, boolean hasReliablePurity) {
        String title = "Tumor specific gene disruptions";
        if (disruptions.isEmpty()) {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        }

        Table contentTable = TableUtil.createReportContentTable(new float[] { 60, 50, 100, 50, 80, 85, 85 },
                new Cell[] { TableUtil.createHeaderCell("Location"), TableUtil.createHeaderCell("Gene"),
                        TableUtil.createHeaderCell("Disrupted range"),
                        TableUtil.createHeaderCell("Type").setTextAlignment(TextAlignment.CENTER),
                        TableUtil.createHeaderCell("Cluster ID").setTextAlignment(TextAlignment.CENTER),
                        TableUtil.createHeaderCell("Disrupted copies").setTextAlignment(TextAlignment.CENTER),
                        TableUtil.createHeaderCell("Undisrupted copies").setTextAlignment(TextAlignment.CENTER) },
                ReportResources.CONTENT_WIDTH_WIDE);

        for (GeneDisruption disruption : GeneDisruptions.sort(disruptions)) {
            contentTable.addCell(TableUtil.createContentCell(disruption.location()));
            contentTable.addCell(TableUtil.createContentCell(disruption.gene()));
            contentTable.addCell(TableUtil.createContentCell(disruption.range()));
            contentTable.addCell(TableUtil.createContentCell(disruption.type())).setTextAlignment(TextAlignment.CENTER);
            contentTable.addCell(TableUtil.createContentCell(String.valueOf(disruption.clusterId()))
                    .setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(GeneDisruptions.copyNumberString(disruption.junctionCopyNumber(),
                    hasReliablePurity)).setTextAlignment(TextAlignment.CENTER));
            contentTable.addCell(TableUtil.createContentCell(GeneUtil.copyNumberToString(disruption.undisruptedCopyNumber(),
                    hasReliablePurity)).setTextAlignment(TextAlignment.CENTER));
        }
        return TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static Table createHlaTable(@NotNull HlaAllelesReportingData lilac, boolean hasReliablePurity) {
        String title = "HLA Alleles";
        Table table = TableUtil.createReportContentTable(new float[] { 10, 10, 10, 10, 10, 10 },
                new Cell[] { TableUtil.createHeaderCell("Gene"), TableUtil.createHeaderCell("Germline allele"),
                        TableUtil.createHeaderCell("Germline copies"), TableUtil.createHeaderCell("Tumor copies"),
                        TableUtil.createHeaderCell("Number somatic mutations*"),
                        TableUtil.createHeaderCell("Interpretation: presence in tumor") },
                ReportResources.CONTENT_WIDTH_WIDE);
        if (!lilac.hlaQC().equals("PASS")) {
            String noConsent = "The QC of the HLA types do not meet the QC cut-offs";
            return TableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN,
                    ReportResources.CONTENT_WIDTH_WIDE);
        } else if (lilac.hlaAllelesReporting().isEmpty()) {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Set<String> sortedAlleles = Sets.newTreeSet(lilac.hlaAllelesReporting().keySet());
            for (String sortAllele : sortedAlleles) {
                List<HlaReporting> allele = lilac.hlaAllelesReporting().get(sortAllele);
                table.addCell(TableUtil.createContentCell(sortAllele));

                Table tableGermlineAllele = new Table(new float[] { 1 });
                Table tableGermlineCopies = new Table(new float[] { 1 });
                Table tableTumorCopies = new Table(new float[] { 1 });
                Table tableSomaticMutations = new Table(new float[] { 1 });
                Table tablePresenceInTumor = new Table(new float[] { 1 });

                for (HlaReporting hlaAlleleReporting : HLAAllele.sort(allele)) {
                    tableGermlineAllele.addCell(TableUtil.createTransparentCell(hlaAlleleReporting.hlaAllele().germlineAllele()));
                    tableGermlineCopies.addCell(TableUtil.createTransparentCell(HLAAllele.copyNumberStringGermline(hlaAlleleReporting.germlineCopies(),
                            hasReliablePurity)));
                    tableTumorCopies.addCell(TableUtil.createTransparentCell(HLAAllele.copyNumberStringTumor(hlaAlleleReporting.tumorCopies(),
                            hasReliablePurity)));
                    tableSomaticMutations.addCell(TableUtil.createTransparentCell(hlaAlleleReporting.somaticMutations()));
                    tablePresenceInTumor.addCell(TableUtil.createTransparentCell(hlaAlleleReporting.interpretation()));
                }

                table.addCell(TableUtil.createContentCell(tableGermlineAllele));
                table.addCell(TableUtil.createContentCell(tableGermlineCopies));
                table.addCell(TableUtil.createContentCell(tableTumorCopies));
                table.addCell(TableUtil.createContentCell(tableSomaticMutations));
                table.addCell(TableUtil.createContentCell(tablePresenceInTumor));
            }
        }

        table.addCell(TableUtil.createLayoutCell(1, table.getNumberOfColumns())
                .add(new Paragraph("\n *When phasing is unclear the mutation will be counted in both alleles as 0.5. Copy number of"
                        + " detected mutations can be found in the somatic variant table.").addStyle(ReportResources.subTextStyle()
                        .setTextAlignment(TextAlignment.CENTER))));
        return TableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private static Table createVirusTable(@NotNull List<AnnotatedVirus> viruses, boolean reportViralPresence) {
        String title = "Tumor specific viral insertions";

        if (!reportViralPresence) {
            String noConsent = "This patient did not give his/her permission for reporting of virus results.";
            return TableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN,
                    ReportResources.CONTENT_WIDTH_WIDE);
        } else if (viruses.isEmpty()) {
            return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Table contentTable = TableUtil.createReportContentTable(new float[] { 150, 160, 100, 40 },
                    new Cell[] { TableUtil.createHeaderCell("Virus"),
                            TableUtil.createHeaderCell("Number of detected integration sites").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Viral coverage").setTextAlignment(TextAlignment.CENTER),
                            TableUtil.createHeaderCell("Risk").setTextAlignment(TextAlignment.CENTER) },
                    ReportResources.CONTENT_WIDTH_WIDE);

            for (AnnotatedVirus virus : viruses) {
                contentTable.addCell(TableUtil.createContentCell(virus.name()));
                contentTable.addCell(TableUtil.createContentCell(ViralPresence.integrations(virus)).setTextAlignment(TextAlignment.CENTER));
                contentTable.addCell(TableUtil.createContentCell(ViralPresence.percentageCovered(virus))
                        .setTextAlignment(TextAlignment.CENTER));
                contentTable.addCell(TableUtil.createContentCell(ViralPresence.driverLikelihood(virus))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            return TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        }
    }

    @NotNull
    private static Table createPharmacogeneticsGenotypesTable(@NotNull Map<String, List<PeachGenotype>> pharmacogeneticsMap,
            boolean reportPharmacogenetics) {
        String title = "Pharmacogenetics";

        if (reportPharmacogenetics) {
            if (pharmacogeneticsMap.isEmpty()) {
                return TableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
            } else {
                Table contentTable = TableUtil.createReportContentTable(new float[] { 60, 60, 60, 100, 60 },
                        new Cell[] { TableUtil.createHeaderCell("Gene"), TableUtil.createHeaderCell("Genotype"),
                                TableUtil.createHeaderCell("Function"), TableUtil.createHeaderCell("Linked drugs"),
                                TableUtil.createHeaderCell("Source") },
                        ReportResources.CONTENT_WIDTH_WIDE);

                Set<String> sortedPharmacogenetics = Sets.newTreeSet(pharmacogeneticsMap.keySet());
                for (String sortPharmacogenetics : sortedPharmacogenetics) {
                    List<PeachGenotype> pharmacogeneticsGenotypeList = pharmacogeneticsMap.get(sortPharmacogenetics);
                    contentTable.addCell(TableUtil.createContentCell(sortPharmacogenetics.equals("UGT1A1") ? sortPharmacogenetics + "#" : sortPharmacogenetics));

                    Table tableGenotype = new Table(new float[] { 1 });
                    Table tableFunction = new Table(new float[] { 1 });
                    Table tableLinkedDrugs = new Table(new float[] { 1 });
                    Table tableSource = new Table(new float[] { 1 });

                    for (PeachGenotype pharmacogeneticsGenotype : pharmacogeneticsGenotypeList) {
                        tableGenotype.addCell(TableUtil.createTransparentCell(pharmacogeneticsGenotype.haplotype()));
                        tableFunction.addCell(TableUtil.createTransparentCell(pharmacogeneticsGenotype.function()));
                        tableLinkedDrugs.addCell(TableUtil.createTransparentCell(pharmacogeneticsGenotype.linkedDrugs()));
                        tableSource.addCell(TableUtil.createTransparentCell(new Paragraph(Pharmacogenetics.sourceName(
                                        pharmacogeneticsGenotype.urlPrescriptionInfo())).addStyle(ReportResources.dataHighlightLinksStyle()))
                                .setAction(PdfAction.createURI(Pharmacogenetics.url(pharmacogeneticsGenotype.urlPrescriptionInfo()))));
                    }

                    contentTable.addCell(TableUtil.createContentCell(tableGenotype));
                    contentTable.addCell(TableUtil.createContentCell(tableFunction));
                    contentTable.addCell(TableUtil.createContentCell(tableLinkedDrugs));
                    contentTable.addCell(TableUtil.createContentCell(tableSource));
                }
                contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                        .add(new Paragraph("\n #Note that we do not separately call the *36 allele. Dutch clinical " +
                                "guidelines consider the *36 allele to be clinically equivalent to the *1 allele.").addStyle(ReportResources.subTextStyle()
                                .setTextAlignment(TextAlignment.CENTER))));

                return TableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
            }
        } else {
            String noConsent = "This patient did not give his/her permission for reporting of pharmacogenomics results.";
            return TableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN,
                    ReportResources.CONTENT_WIDTH_WIDE);
        }
    }
}