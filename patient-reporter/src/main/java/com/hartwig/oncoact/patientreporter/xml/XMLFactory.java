package com.hartwig.oncoact.patientreporter.xml;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Maps;
import com.hartwig.oncoact.common.chord.ChordStatus;
import com.hartwig.oncoact.common.linx.HomozygousDisruption;
import com.hartwig.oncoact.common.linx.LinxFusion;
import com.hartwig.oncoact.common.purple.loader.CnPerChromosomeArmData;
import com.hartwig.oncoact.common.purple.loader.GainLoss;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.virus.AnnotatedVirus;
import com.hartwig.oncoact.common.xml.ImmutableKeyXML;
import com.hartwig.oncoact.common.xml.KeyXML;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.cfreport.data.GainsAndLosses;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneFusions;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.HomozygousDisruptions;
import com.hartwig.oncoact.patientreporter.cfreport.data.SomaticVariants;
import com.hartwig.oncoact.util.DataUtil;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class XMLFactory {

    private XMLFactory() {
    }

    @NotNull
    public static ReportXML generateXMLData(@NotNull AnalysedPatientReport report) {
        boolean hasReliablePurity = report.genomicAnalysis().hasReliablePurity();
        Map<String, KeyXML> mapXml = Maps.newHashMap();
        mapXml.put("itemVrbAanvrager", ImmutableKeyXML.builder().keyPath("VrbAanvrager").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemVrbAanvragerAnders",
                ImmutableKeyXML.builder().keyPath("VrbAanvragerAnders").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemVrbOnderzoekNummers",
                ImmutableKeyXML.builder().keyPath("VrbOnderzoekNummers").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemVrbProcedure", ImmutableKeyXML.builder().keyPath("VrbProcedure").valuePath(Map.of("value", "wgs")).build());
        mapXml.put("itemRefNummerWgs",
                ImmutableKeyXML.builder()
                        .keyPath("RefNummerWgs")
                        .valuePath(Map.of("value",
                                !report.sampleReport().hospitalPatientId().isEmpty()
                                        ? report.sampleReport().hospitalPatientId()
                                        : report.sampleReport().tumorSampleId()))
                        .build());
        mapXml.put("importwgs.wgs_reference_number",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgs_reference_number")
                        .valuePath(Map.of("value",
                                !report.sampleReport().hospitalPatientId().isEmpty()
                                        ? report.sampleReport().hospitalPatientId()
                                        : report.sampleReport().tumorSampleId()))
                        .build());
        mapXml.put("itemWgsRedenAanvraag",
                ImmutableKeyXML.builder().keyPath("WgsRedenAanvraag").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemWgsGevrOndzTher",
                ImmutableKeyXML.builder().keyPath("WgsGevrOndzTher").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemWgsGevrOndzTherAnd",
                ImmutableKeyXML.builder().keyPath("WgsGevrOndzTherAnd").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemWgsGevrOndzDiffDiag",
                ImmutableKeyXML.builder().keyPath("WgsGevrOndzDiffDiag").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemWgsGevrOndzDiffDiagAnd",
                ImmutableKeyXML.builder().keyPath("WgsGevrOndzDiffDiagAnd").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemWgsPercNeoCellenEx",
                ImmutableKeyXML.builder().keyPath("WgsPercNeoCellenEx").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemWgsPercNeoCellenBeoord",
                ImmutableKeyXML.builder().keyPath("WgsPercNeoCellenBeoord").valuePath(Map.of("value", Strings.EMPTY)).build());
        mapXml.put("itemWgsPlatform",
                ImmutableKeyXML.builder().keyPath("WgsPlatform").valuePath(Map.of("value", "Illumina NovaSeq")).build());
        mapXml.put("itemWgsTumorPurity",
                ImmutableKeyXML.builder()
                        .keyPath("WgsTumorPurity")
                        .valuePath(Map.of("value", DataUtil.formatPercentage(report.genomicAnalysis().impliedPurity())))
                        .build());
        mapXml.put("itemWgsGemTuPloid",
                ImmutableKeyXML.builder()
                        .keyPath("WgsGemTuPloid")
                        .valuePath(Map.of("value", String.valueOf(report.genomicAnalysis().averageTumorPloidy())))
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
        mapXml.put("itemWgsCupAnalyse",
                ImmutableKeyXML.builder()
                        .keyPath("WgsCupAnalyse")
                        .valuePath(cupAnalyse == null ? null : Map.of("value", cupAnalyse))
                        .build());

        String disclaimer = Strings.EMPTY;
        disclaimer += report.genomicAnalysis().hasReliablePurity() ? Strings.EMPTY : "Due to the lower tumor purity potential "
                + "(subclonal) DNA aberrations might not have been detected using this test. This result should therefore be "
                + "considered with caution.";
        disclaimer += !report.specialRemark().isEmpty() ? report.specialRemark() : Strings.EMPTY;
        mapXml.put("itemWgsDisclaimerTonen",
                ImmutableKeyXML.builder().keyPath("WgsDisclaimerTonen").valuePath(Map.of("value", disclaimer)).build());

        mapXml.put("itemWgsMolecInter",
                ImmutableKeyXML.builder()
                        .keyPath("WgsMolecInter")
                        .valuePath(Map.of("value", report.clinicalSummary() != null ? report.clinicalSummary() : Strings.EMPTY))
                        .build());
        mapXml.put("itemWgsKlinInter", ImmutableKeyXML.builder().keyPath("WgsKlinInter").valuePath(Map.of("value", Strings.EMPTY)).build());

        addReportableVariantsToXML(report.genomicAnalysis().reportableVariants(), mapXml, hasReliablePurity);
        addGainLossesToXML(report.genomicAnalysis().gainsAndLosses(), report.genomicAnalysis().cnPerChromosome(), mapXml, hasReliablePurity);
        addFusionToXML(report.genomicAnalysis().geneFusions(), mapXml, hasReliablePurity);

        mapXml.put("importwgs.wgsms.line[1]msscore",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgsms.line[1]msscore")
                        .valuePath(Map.of("value", Double.toString(report.genomicAnalysis().microsatelliteIndelsPerMb())))
                        .build());
        mapXml.put("importwgs.wgsms.line[1]msstatus",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgsms.line[1]msstatus")
                        .valuePath(Map.of("value", report.genomicAnalysis().microsatelliteStatus().name()))
                        .build());
        mapXml.put("importwgs.wgsms.line[1]tumuload",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgsms.line[1]tumuload")
                        .valuePath(Map.of("value", Double.toString(report.genomicAnalysis().tumorMutationalLoad())))
                        .build());
        mapXml.put("importwgs.wgsms.line[1]tumulosta",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgsms.line[1]tumulosta")
                        .valuePath(Map.of("value", report.genomicAnalysis().tumorMutationalLoadStatus().name()))
                        .build());
        mapXml.put("importwgs.wgsms.line[1]tutmb",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgsms.line[1]tutmb")
                        .valuePath(Map.of("value", Double.toString(report.genomicAnalysis().tumorMutationalBurden())))
                        .build());
        mapXml.put("importwgs.wgsms.line[1]horesco",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgsms.line[1]horesco")
                        .valuePath(Map.of("value",
                                report.genomicAnalysis().hrdStatus().display().equals(ChordStatus.CANNOT_BE_DETERMINED.toString()) ? "N/A" : Double.toString(report.genomicAnalysis().hrdValue())))
                        .build());
        mapXml.put("importwgs.wgsms.line[1]horestu",
                ImmutableKeyXML.builder()
                        .keyPath("importwgs.wgsms.line[1]horestu")
                        .valuePath(Map.of("value", report.genomicAnalysis().hrdStatus().name()))
                        .build());

        addHomozygousDisruptionsToXML(report.genomicAnalysis().homozygousDisruptions(), mapXml);
        addVirussesToXML(report.genomicAnalysis().reportableViruses(), mapXml);

        TreeMap<String, KeyXML> sorted = new TreeMap<>();
        sorted.putAll(mapXml);

        List<ImportWGSXML> importWGSXML = Lists.newArrayList();
        importWGSXML.add(ImmutableImportWGSXML.builder().item(sorted.values()).build());

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

    public static void addGainLossesToXML(@NotNull List<GainLoss> gainLosses, @NotNull List<CnPerChromosomeArmData> chromosomeArmData,
            @NotNull Map<String, KeyXML> mapXml, boolean hasReliablePurity) {
        int count = 1;
        for (GainLoss gainLoss : GainsAndLosses.sort(gainLosses)) {
            mapXml.put("item[" + count + "importwgs.wgscnv.line[" + count + "]chr",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgscnv.line[" + count + "]chr")
                            .valuePath(Map.of("value", gainLoss.chromosome()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgscnv.line[" + count + "]region",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgscnv.line[" + count + "]region")
                            .valuePath(Map.of("value", gainLoss.chromosomeBand()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgscnv.line[" + count + "]gene",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgscnv.line[" + count + "]gene")
                            .valuePath(Map.of("value", gainLoss.gene()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgscnv.line[" + count + "]type",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgscnv.line[" + count + "]type")
                            .valuePath(Map.of("value", gainLoss.interpretation().name()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgscnv.line[" + count + "]copies",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgscnv.line[" + count + "]copies")
                            .valuePath(Map.of("value", hasReliablePurity ? String.valueOf(gainLoss.maxCopies()) : DataUtil.NA_STRING))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgscnv.line[" + count + "]charmco",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgscnv.line[" + count + "]charmco")
                            .valuePath(Map.of("value", GainsAndLosses.chromosomeArmCopyNumber(chromosomeArmData, gainLoss)))
                            .build());
            count += 1;
        }
    }

    public static void addHomozygousDisruptionsToXML(@NotNull List<HomozygousDisruption> homozygousDisruptions,
            @NotNull Map<String, KeyXML> mapXml) {
        int count = 1;
        for (HomozygousDisruption homozygousDisruption : HomozygousDisruptions.sort(homozygousDisruptions)) {
            mapXml.put("item[" + count + "importwgs.wgshzy.line[" + count + "]gen",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgshzy.line[" + count + "]gen")
                            .valuePath(Map.of("value", homozygousDisruption.gene()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgshzy.line[" + count + "]chr",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgshzy.line[" + count + "]chr")
                            .valuePath(Map.of("value", homozygousDisruption.chromosome()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgshzy.line[" + count + "]chrbd",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgshzy.line[" + count + "]chrbd")
                            .valuePath(Map.of("value", homozygousDisruption.chromosomeBand()))
                            .build());
            count += 1;
        }
    }

    public static void addReportableVariantsToXML(@NotNull List<ReportableVariant> reportableVariants,
            @NotNull Map<String, KeyXML> mapXml, boolean hasReliablePurity) {
        int count = 1;
        for (ReportableVariant reportableVariant : SomaticVariants.sort(reportableVariants)) {
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]name",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]name")
                            .valuePath(Map.of("value", reportableVariant.gene()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]pos",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]pos")
                            .valuePath(Map.of("value", reportableVariant.gDNA()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]var",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]var")
                            .valuePath(Map.of("value", reportableVariant.canonicalHgvsCodingImpact()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]prot",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]prot")
                            .valuePath(Map.of("value", reportableVariant.canonicalHgvsProteinImpact()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]readep",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]readep")
                            .valuePath(Map.of("value", reportableVariant.alleleReadCount() + "/" + reportableVariant.totalReadCount()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]copie",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]copie")
                            .valuePath(Map.of("value", SomaticVariants.copyNumberString(reportableVariant.totalCopyNumber(), hasReliablePurity)))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]tvaf",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]tvaf")
                            .valuePath(Map.of("value", reportableVariant.tVAF()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]biallc",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]biallc")
                            .valuePath(Map.of("value",
                                    reportableVariant.biallelic() == null
                                            ? Strings.EMPTY
                                            : Boolean.toString(reportableVariant.biallelic())))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]hotsp",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]hotsp")
                            .valuePath(Map.of("value", reportableVariant.hotspot().name()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsgene.line[" + count + "]driver",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsgene.line[" + count + "]driver")
                            .valuePath(Map.of("value", reportableVariant.driverLikelihoodInterpretation().name()))
                            .build());
            count += 1;
        }
    }

    public static void addVirussesToXML(@NotNull List<AnnotatedVirus> annotatedVirusList, @NotNull Map<String, KeyXML> mapXml) {
        int count = 1;
        for (AnnotatedVirus virus : annotatedVirusList) {
            mapXml.put("item[" + count + "importwgs.wgsvrs.line[" + count + "]name",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsvrs.line[" + count + "]name")
                            .valuePath(Map.of("value", virus.name()))
                            .build());
            count += 1;
        }
    }

    public static void addFusionToXML(@NotNull List<LinxFusion> linxFusions, @NotNull Map<String, KeyXML> mapXml, boolean hasReliablePurity) {
        int count = 1;
        for (LinxFusion fusion : GeneFusions.sort(linxFusions)) {
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]name",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]name")
                            .valuePath(Map.of("value", fusion.name()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]f5gen",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]f5gen")
                            .valuePath(Map.of("value", fusion.geneStart()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]f5refid",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]f5refid")
                            .valuePath(Map.of("value", fusion.geneTranscriptStart()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]f5exon",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]f5exon")
                            .valuePath(Map.of("value", fusion.geneContextStart()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]f3gen",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]f3gen")
                            .valuePath(Map.of("value", fusion.geneEnd()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]f3refid",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]f3refid")
                            .valuePath(Map.of("value", fusion.geneTranscriptEnd()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]f3exon",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]f3exon")
                            .valuePath(Map.of("value", fusion.geneContextEnd()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]tuco",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]tuco")
                            .valuePath(Map.of("value", GeneUtil.copyNumberToString(fusion.junctionCopyNumber(), hasReliablePurity)))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]fufra",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]fufra")
                            .valuePath(Map.of("value", fusion.phased().name()))
                            .build());
            mapXml.put("item[" + count + "importwgs.wgsfusie.line[" + count + "]driver",
                    ImmutableKeyXML.builder()
                            .keyPath("importwgs.wgsfusie.line[" + count + "]driver")
                            .valuePath(Map.of("value", fusion.likelihood().name()))
                            .build());
            count += 1;
        }
    }
}