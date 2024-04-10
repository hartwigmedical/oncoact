package com.hartwig.oncoact.patientreporter.xml;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.model.*;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.xml.ImmutableKeyXML;
import com.hartwig.oncoact.xml.KeyXML;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class XMLFactory {

    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.##");
    private static final DecimalFormat NO_DECIMAL_FORMAT = ReportResources.decimalFormat("#");
    private static final DecimalFormat SINGLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.#");

    private XMLFactory() {
    }

    @NotNull
    public static ReportXML generateXMLData(@NotNull WgsReport report) {
        List<KeyXML> xml = Lists.newArrayList();

        Genomic genomic = report.genomic();
        TumorSample tumorSample = report.tumorSample();
        boolean hasReliablePurity = genomic.hasReliablePurity();

        xml.add(ImmutableKeyXML.builder().keyPath("VrbAanvrager").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("VrbAanvragerAnders").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("VrbOnderzoekNummers").valuePath(Map.of("value", Strings.EMPTY)).build());
        xml.add(ImmutableKeyXML.builder().keyPath("VrbProcedure").valuePath(Map.of("value", "wgs")).build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("RefNummerWgs")
                .valuePath(Map.of("value", tumorSample.reportingId().value()))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgs_reference_number")
                .valuePath(Map.of("value", tumorSample.reportingId().value()))
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
                .valuePath(Map.of("value", Formats.formatPercentageRoundWithoutPercent(genomic.purity())))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("WgsGemTuPloid")
                .valuePath(Map.of("value", genomic.averagePloidy()))
                .build());

        xml.add(ImmutableKeyXML.builder()
                .keyPath("WgsCupAnalyse")
                .valuePath(Map.of("value", report.summary().tumorCharacteristics().tissueOfOriginPrediction()))
                .build());

        String disclaimer = "- Further interpretation of these results within the patient’s clinical context is "
                + "required by a clinician with support of a molecular tumor board." + "\n";
        disclaimer += report.version().qsFormNumber().equals(QsFormNumber.FOR_209) ? "- Due to the lower tumor purity potential "
                + "(subclonal) DNA aberrations might not have been detected using this test. This result should therefore be "
                + "considered with caution. " + "\n" : Strings.EMPTY;
        disclaimer += report.summary().specialRemark();
        xml.add(ImmutableKeyXML.builder().keyPath("WgsDisclaimerTonen").valuePath(Map.of("value", disclaimer)).build());

        xml.add(ImmutableKeyXML.builder()
                .keyPath("WgsMolecInter")
                .valuePath(Map.of("value", report.summary().mostRelevantFindings()))
                .build());
        xml.add(ImmutableKeyXML.builder().keyPath("WgsKlinInter").valuePath(Map.of("value", Strings.EMPTY)).build());

        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]msscore")
                .valuePath(Map.of("value",
                        hasReliablePurity
                                ? DOUBLE_DECIMAL_FORMAT.format(genomic.profiles().microsatellite().value())
                                : Formats.NA_STRING))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]msstatus")
                .valuePath(Map.of("value", genomic.profiles().microsatellite().label()))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]tumuload")
                .valuePath(Map.of("value",
                        hasReliablePurity ? NO_DECIMAL_FORMAT.format(genomic.profiles().tumorMutationalLoad()) : Formats.NA_STRING))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]tutmb")
                .valuePath(Map.of("value",
                        hasReliablePurity
                                ? SINGLE_DECIMAL_FORMAT.format(genomic.profiles().tumorMutationalBurden().value())
                                : Formats.NA_STRING))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]horesco")
                .valuePath(Map.of("value",
                        genomic.profiles().homologousRecombinationDeficiency().label().equals(HomologousRecombinationDeficiencyStatus.CANNOT_BE_DETERMINED.label)
                                ? "N/A"
                                : DOUBLE_DECIMAL_FORMAT.format(genomic.profiles().homologousRecombinationDeficiency().value())))
                .build());
        xml.add(ImmutableKeyXML.builder()
                .keyPath("importwgs.wgsms.line[1]horestu")
                .valuePath(Map.of("value", genomic.profiles().homologousRecombinationDeficiency().label()))
                .build());

        addReportableVariantsToXML(genomic.variants(), xml);
        addGainLossesToXML(genomic.gainsLosses(), xml);
        addFusionToXML(genomic.geneFusions(), xml);
        addHomozygousDisruptionsToXML(genomic.homozygousDisruptions(), xml);
        addVirussesToXML(genomic.viralInsertions(), xml);

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

    public static void addGainLossesToXML(@NotNull List<ObservedGainsLosses> gainLosses,
                                          @NotNull List<KeyXML> xmlList) {
        int count = 1;
        for (ObservedGainsLosses gainLoss : gainLosses) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]chr")
                    .valuePath(Map.of("value", gainLoss.chromosome()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]region")
                    .valuePath(Map.of("value", gainLoss.region()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]gene")
                    .valuePath(Map.of("value", gainLoss.gene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]type")
                    .valuePath(Map.of("value", gainLoss.type().display))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]copies")
                    .valuePath(Map.of("value", gainLoss.maxCopies()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgscnv.line[" + count + "]charmco")
                    .valuePath(Map.of("value", gainLoss.chromosomeArmCopies()))
                    .build());
            count += 1;
        }
    }

    public static void addHomozygousDisruptionsToXML(@NotNull List<ObservedHomozygousDisruption> homozygousDisruptions,
                                                     @NotNull List<KeyXML> xmlList) {
        int count = 1;
        for (ObservedHomozygousDisruption homozygousDisruption : homozygousDisruptions) {
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
                    .valuePath(Map.of("value", homozygousDisruption.region()))
                    .build());
            count += 1;
        }
    }

    public static void addReportableVariantsToXML(@NotNull List<ObservedVariant> reportableVariants, @NotNull List<KeyXML> xmlList) {
        int count = 1;
        for (ObservedVariant reportableVariant : reportableVariants) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]name")
                    .valuePath(Map.of("value", reportableVariant.gene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]pos")
                    .valuePath(Map.of("value", reportableVariant.position()))
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
                    .valuePath(Map.of("value", reportableVariant.readDepth().alleleReadCount() + "/" + reportableVariant.readDepth().totalReadCount()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]copie")
                    .valuePath(Map.of("value", reportableVariant.copies()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]tvaf")
                    .valuePath(Map.of("value", reportableVariant.tVaf()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]biallc")
                    .valuePath(Map.of("value", reportableVariant.biallelic()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]hotsp")
                    .valuePath(Map.of("value", reportableVariant.hotspot().display))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsgene.line[" + count + "]driver")
                    .valuePath(Map.of("value", reportableVariant.driver().display))
                    .build());
            count += 1;
        }
    }

    public static void addVirussesToXML(@NotNull List<ObservedViralInsertion> annotatedVirusList, @NotNull List<KeyXML> xmlList) {
        int count = 1;
        for (ObservedViralInsertion virus : annotatedVirusList) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsvrs.line[" + count + "]name")
                    .valuePath(Map.of("value", virus.virus()))
                    .build());
            count += 1;
        }

    }

    public static void addFusionToXML(@NotNull List<ObservedGeneFusion> linxFusions, @NotNull List<KeyXML> xmlList) {
        int count = 1;
        for (ObservedGeneFusion fusion : linxFusions) {
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]name")
                    .valuePath(Map.of("value", fusion.fiveGene() + "_" + fusion.threeGene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f5gen")
                    .valuePath(Map.of("value", fusion.fiveGene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f5refid")
                    .valuePath(Map.of("value", fusion.fivePromiscuousTranscript()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f5exon")
                    .valuePath(Map.of("value", fusion.fivePromiscuousEnd()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f3gen")
                    .valuePath(Map.of("value", fusion.threeGene()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f3refid")
                    .valuePath(Map.of("value", fusion.threePromiscuousTranscript()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]f3exon")
                    .valuePath(Map.of("value", fusion.threePromiscuousStart()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]tuco")
                    .valuePath(Map.of("value", fusion.copies()))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]fufra")
                    .valuePath(Map.of("value", fusion.phasing().type))
                    .build());
            xmlList.add(ImmutableKeyXML.builder()
                    .keyPath("importwgs.wgsfusie.line[" + count + "]driver")
                    .valuePath(Map.of("value", fusion.driver().value))
                    .build());
            count += 1;
        }
    }
}