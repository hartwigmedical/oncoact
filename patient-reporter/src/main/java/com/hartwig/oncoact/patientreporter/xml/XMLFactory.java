package com.hartwig.oncoact.patientreporter.xml;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.ctc.wstx.util.DataUtil;
import com.google.common.collect.Lists;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.orange.chord.ChordStatus;
import com.hartwig.oncoact.orange.linx.LinxFusion;
import com.hartwig.oncoact.orange.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.orange.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.virus.VirusInterpreterEntry;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.data.GainsAndLosses;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneFusions;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.HomozygousDisruptions;
import com.hartwig.oncoact.patientreporter.cfreport.data.SomaticVariants;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.xml.ImmutableKeyXML;
import com.hartwig.oncoact.xml.KeyXML;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class XMLFactory {

    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.##");
    private static final DecimalFormat NO_DECIMAL_FORMAT = ReportResources.decimalFormat("#");
    private static final DecimalFormat SINGLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.#");


    private XMLFactory() {
    }

    @NotNull
    public static ReportXML generateXMLData(@NotNull AnalysedPatientReport report) {
        List<KeyXML> xml = Lists.newArrayList();

        boolean hasReliablePurity = report.genomicAnalysis().hasReliablePurity();
        xml.add(ImmutableKeyXML.builder().keyPath("VrbAanvrager").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("VrbAanvragerAnders").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("VrbOnderzoekNummers").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("VrbProcedure").valuePath(Map.of("value", "wgs")).build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("RefNummerWgs")
                .valuePath(Map.of("value",
                        !report.sampleReport().hospitalPatientId().isEmpty()
                                ? report.sampleReport().hospitalPatientId()
                                : report.sampleReport().tumorSampleId()))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgs_reference_number")
                .valuePath(Map.of("value",
                        !report.sampleReport().hospitalPatientId().isEmpty()
                                ? report.sampleReport().hospitalPatientId()
                                : report.sampleReport().tumorSampleId()))
                .build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsRedenAanvraag").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsGevrOndzTher").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsGevrOndzTherAnd").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsGevrOndzDiffDiag").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsGevrOndzDiffDiagAnd").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsPercNeoCellenEx").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsPercNeoCellenBeoord").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsPlatform").valuePath(Map.of("value", "Illumina NovaSeq")).build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("WgsTumorPurity")
                .valuePath(Map.of("value", Formats.formatPercentageRound(report.genomicAnalysis().impliedPurity())))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("WgsGemTuPloid")
                .valuePath(Map.of("value", GeneUtil.copyNumberToString(report.genomicAnalysis().averageTumorPloidy(), hasReliablePurity)))
                .build());

        String cupAnalyse = null;
        if (report.molecularTissueOriginReporting() == null) {
            cupAnalyse = null;
        } else if (report.molecularTissueOriginReporting().interpretLikelihood() == null) {
            cupAnalyse = report.molecularTissueOriginReporting().interpretCancerType();
        } else {
            cupAnalyse = report.molecularTissueOriginReporting().interpretCancerType() + " (" + report.molecularTissueOriginReporting()
                    .interpretLikelihood() + ")";
        }
        xml.add(ImmutableKeyXML.builder()
                .keyPath("WgsCupAnalyse")
                .valuePath(cupAnalyse == null ? null : Map.of("value", cupAnalyse))
                .build());

        String disclaimer = Strings.EMPTY;
        disclaimer += report.genomicAnalysis().hasReliablePurity() ? Strings.EMPTY : "Due to the lower tumor purity potential "
                + "(subclonal) DNA aberrations might not have been detected using this test. This result should therefore be "
                + "considered with caution.";
        disclaimer += !report.specialRemark().isEmpty() ? report.specialRemark() : Strings.EMPTY;
        xml.add(ImmutableKeyXML.builder().keyPath("WgsDisclaimerTonen").valuePath(Map.of("value", disclaimer)).build());

        xml.add(ImmutableKeyXML.builder()
                .keyPath("WgsMolecInter")
                .valuePath(Map.of("value", report.clinicalSummary() != null ? report.clinicalSummary() : Strings.EMPTY))
                .build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsKlinInter").valuePath(Map.of("value", Strings.EMPTY)).build());

        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]msscore")
                .valuePath(Map.of("value", hasReliablePurity ? DOUBLE_DECIMAL_FORMAT.format(report.genomicAnalysis().microsatelliteIndelsPerMb()) : Formats.NA_STRING))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]msstatus")
                .valuePath(Map.of("value", report.genomicAnalysis().microsatelliteStatus().name()))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]tumuload")
                .valuePath(Map.of("value", hasReliablePurity ? NO_DECIMAL_FORMAT.format(report.genomicAnalysis().tumorMutationalLoad()) : Formats.NA_STRING))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]tumulosta")
                .valuePath(Map.of("value", report.genomicAnalysis().tumorMutationalLoadStatus().name()))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]tutmb")
                .valuePath(Map.of("value", hasReliablePurity ? SINGLE_DECIMAL_FORMAT.format(report.genomicAnalysis().tumorMutationalBurden()) : Formats.NA_STRING))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]horesco")
                .valuePath(Map.of("value",
                        report.genomicAnalysis().hrdStatus() == ChordStatus.CANNOT_BE_DETERMINED ? "N/A" : DOUBLE_DECIMAL_FORMAT.format(report.genomicAnalysis().hrdValue())))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]horestu")
                .valuePath(Map.of("value", report.genomicAnalysis().hrdStatus().name()))
                .build());


        addReportableVariantsToXML(report.genomicAnalysis().reportableVariants(), xml, hasReliablePurity);
        addGainLossesToXML(report.genomicAnalysis().gainsAndLosses(), report.genomicAnalysis().cnPerChromosome(), xml, hasReliablePurity);
        addFusionToXML(report.genomicAnalysis().geneFusions(), xml, hasReliablePurity);
        addHomozygousDisruptionsToXML(report.genomicAnalysis().homozygousDisruptions(), xml);
        addVirussesToXML(report.genomicAnalysis().reportableViruses(), xml);

        List<ImportWGSXML> importWGSXML = Lists.newArrayList();
        importWGSXML.add(ImmutableImportWGSXML.builder().item(xml).build());

        return ImmutableReportXML.builder()
                .protocol(ImmutableXMLProtocol.builder()
                        .meta(ImmutableProtocolNameXML.builder().protocolName("Moleculairebepalingen").build())
                        .content(ImmutableContentXML.builder()
                                .rubriek(ImmutableRubriekXML.builder()
                                        .jsonSession(ImmutableSessionXML.builder().importWGSNew(importWGSXML).build())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    public static void addGainLossesToXML(@NotNull List<PurpleGainLoss> gainLosses, @NotNull List<CnPerChromosomeArmData> chromosomeArmData,
                                          @NotNull List<KeyXML> xmlList, boolean hasReliablePurity) {
        int count = 1;
        for (PurpleGainLoss gainLoss : GainsAndLosses.sort(gainLosses)) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]chr")
                    .valuePath(Map.of("value", gainLoss.chromosome()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]region")
                    .valuePath(Map.of("value", gainLoss.chromosomeBand()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]gene")
                    .valuePath(Map.of("value", gainLoss.gene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]type")
                    .valuePath(Map.of("value", gainLoss.interpretation().name()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]copies")
                    .valuePath(Map.of("value", hasReliablePurity ? String.valueOf(gainLoss.maxCopies()) : Formats.NA_STRING))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]charmco")
                    .valuePath(Map.of("value", GainsAndLosses.chromosomeArmCopyNumber(chromosomeArmData, gainLoss)))
                    .build());
            count += 1;
        }
    }

    public static void addHomozygousDisruptionsToXML(@NotNull List<LinxHomozygousDisruption> homozygousDisruptions,
                                                     @NotNull List<KeyXML> xmlList) {
        int count = 1;
        for (LinxHomozygousDisruption homozygousDisruption : HomozygousDisruptions.sort(homozygousDisruptions)) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgshzy.line[" + count + "]gen")
                    .valuePath(Map.of("value", homozygousDisruption.gene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgshzy.line[" + count + "]chr")
                    .valuePath(Map.of("value", homozygousDisruption.chromosome()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgshzy.line[" + count + "]chrbd")
                    .valuePath(Map.of("value", homozygousDisruption.chromosomeBand()))
                    .build());
            count += 1;
        }
    }

    public static void addReportableVariantsToXML(@NotNull List<ReportableVariant> reportableVariants,
                                                  @NotNull List<KeyXML> xmlList, boolean hasReliablePurity) {
        int count = 1;
        for (ReportableVariant reportableVariant : SomaticVariants.sort(reportableVariants)) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]name")
                    .valuePath(Map.of("value", reportableVariant.gene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]pos")
                    .valuePath(Map.of("value", reportableVariant.gDNA()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]var")
                    .valuePath(Map.of("value", reportableVariant.canonicalHgvsCodingImpact()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]prot")
                    .valuePath(Map.of("value", reportableVariant.canonicalHgvsProteinImpact()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]readep")
                    .valuePath(Map.of("value", reportableVariant.alleleReadCount() + "/" + reportableVariant.totalReadCount()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]copie")
                    .valuePath(Map.of("value", SomaticVariants.copyNumberString(reportableVariant.totalCopyNumber(), hasReliablePurity)))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]tvaf")
                    .valuePath(Map.of("value", reportableVariant.tVAF()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]biallc")
                    .valuePath(Map.of("value", Boolean.toString(reportableVariant.biallelic())))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]hotsp")
                    .valuePath(Map.of("value", reportableVariant.hotspot().name()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]driver")
                    .valuePath(Map.of("value", reportableVariant.driverLikelihoodInterpretation().name()))
                    .build());
            count += 1;
        }
    }

    public static void addVirussesToXML(@NotNull List<VirusInterpreterEntry> annotatedVirusList, @NotNull List<KeyXML> xmlList) {
        int count = 1;
        for (VirusInterpreterEntry virus : annotatedVirusList) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsvrs.line[" + count + "]name")
                    .valuePath(Map.of("value", virus.name()))
                    .build());
            count += 1;
        }

    }

    public static void addFusionToXML(@NotNull List<LinxFusion> linxFusions, @NotNull List<KeyXML> xmlList, boolean hasReliablePurity) {
        int count = 1;
        for (LinxFusion fusion : GeneFusions.sort(linxFusions)) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]name")
                    .valuePath(Map.of("value", fusion.name()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f5gen")
                    .valuePath(Map.of("value", fusion.geneStart()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f5refid")
                    .valuePath(Map.of("value", fusion.geneTranscriptStart()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f5exon")
                    .valuePath(Map.of("value", fusion.geneContextStart()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f3gen")
                    .valuePath(Map.of("value", fusion.geneEnd()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f3refid")
                    .valuePath(Map.of("value", fusion.geneTranscriptEnd()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f3exon")
                    .valuePath(Map.of("value", fusion.geneContextEnd()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]tuco")
                    .valuePath(Map.of("value", GeneUtil.copyNumberToString(fusion.junctionCopyNumber(), hasReliablePurity)))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]fufra")
                    .valuePath(Map.of("value", fusion.phased().name()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]driver")
                    .valuePath(Map.of("value", fusion.driverLikelihood().name()))
                    .build());
            count += 1;
        }
    }
}