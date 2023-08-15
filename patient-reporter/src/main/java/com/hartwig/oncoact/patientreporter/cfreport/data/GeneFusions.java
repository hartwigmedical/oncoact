package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.FusionLikelihoodType;
import com.hartwig.hmftools.datamodel.linx.LinxFusionType;
import com.hartwig.oncoact.patientreporter.algo.CurationFunctions;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

import org.jetbrains.annotations.NotNull;

public final class GeneFusions {
    
    private final TableUtil tableUtil;
    @NotNull
    private final ReportResources reportResources;

    public GeneFusions(@NotNull ReportResources reportResources) {
        this.reportResources = reportResources;
        this.tableUtil = new TableUtil(reportResources);
    }

    @NotNull
    public static List<LinxFusion> sort(@NotNull List<LinxFusion> fusions) {
        return fusions.stream().sorted((fusion1, fusion2) -> {
            if (fusion1.likelihood() == fusion2.likelihood()) {
                if (fusion1.geneStart().equals(fusion2.geneStart())) {
                    return fusion1.geneEnd().compareTo(fusion2.geneEnd());
                } else {
                    return fusion1.geneStart().compareTo(fusion2.geneStart());
                }
            } else {
                return fusion1.likelihood() == FusionLikelihoodType.HIGH ? -1 : 1;
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    public static Set<String> uniqueGeneFusions(@NotNull Iterable<LinxFusion> fusions) {
        Set<String> genes = Sets.newTreeSet();
        for (LinxFusion fusion : fusions) {
            if (fusion.likelihood() == FusionLikelihoodType.HIGH) {
                genes.add(name(fusion));
            }
        }
        return genes;
    }

    @NotNull
    public Cell fusionContentType(@NotNull LinxFusionType type, @NotNull String geneName, @NotNull String transcript) {
        if (type.equals(LinxFusionType.IG_PROMISCUOUS) || type.equals(LinxFusionType.IG_KNOWN_PAIR)) {
            if (geneName.startsWith("@IG")) {
                return tableUtil.createContentCell(new Paragraph(transcript));
            } else {
                return tableUtil.createContentCell(new Paragraph(transcript))
                        .addStyle(reportResources.dataHighlightLinksStyle())
                        .setAction(PdfAction.createURI(GeneFusions.transcriptUrl(transcript)));
            }
        } else {
            return tableUtil.createContentCell(new Paragraph(transcript))
                    .addStyle(reportResources.dataHighlightLinksStyle())
                    .setAction(PdfAction.createURI(GeneFusions.transcriptUrl(transcript)));
        }
    }

    @NotNull
    public static String name(@NotNull LinxFusion fusion) {
        return CurationFunctions.curateGeneNamePdf(fusion.geneStart()) + " - " + CurationFunctions.curateGeneNamePdf(fusion.geneEnd());
    }

    @NotNull
    public static String transcriptUrl(@NotNull String transcript) {
        return "http://grch37.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=" + transcript;
    }

    @NotNull
    public static String type(@NotNull LinxFusion fusion) {
        LinxFusionType type = fusion.reportedType();
        if (type.equals(LinxFusionType.NONE)) {
            return "None";
        } else if (type.equals(LinxFusionType.KNOWN_PAIR)) {
            return "Known pair";
        } else if (type.equals(LinxFusionType.PROMISCUOUS_5)) {
            return "5' Promiscuous";
        } else if (type.equals(LinxFusionType.PROMISCUOUS_3)) {
            return "3' Promiscuous";
        } else if (type.equals(LinxFusionType.IG_KNOWN_PAIR)) {
            return "IG known pair";
        } else if (type.equals(LinxFusionType.IG_PROMISCUOUS)) {
            return "IG promiscuous";
        } else if (type.equals(LinxFusionType.EXON_DEL_DUP)) {
            return "Exon del dup";
        } else if (type.equals(LinxFusionType.PROMISCUOUS_BOTH)) {
            return "5' and 3' Promiscuous";
        } else {
            return type.toString();
        }
    }

    @NotNull
    public static String phased(@NotNull LinxFusion fusion) {
        switch (fusion.phased()) {
            case INFRAME: {
                return "Inframe";
            }
            case SKIPPED_EXONS: {
                return "Skipped exons";
            }
            case OUT_OF_FRAME: {
                return "Out of frame";
            }
            default: {
                return "Invalid";
            }
        }
    }

    @NotNull
    public static String likelihood(@NotNull LinxFusion fusion) {
        switch (fusion.likelihood()) {
            case HIGH: {
                return "High";
            }
            case LOW: {
                return "Low";
            }
            case NA: {
                return "NA";
            }
            default: {
                return "Invalid";
            }
        }
    }
}
