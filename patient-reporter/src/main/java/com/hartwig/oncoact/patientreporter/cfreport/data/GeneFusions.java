package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.orange.linx.LinxFusion;
import com.hartwig.oncoact.orange.linx.LinxFusionDriverLikelihood;
import com.hartwig.oncoact.orange.linx.LinxFusionType;
import com.hartwig.oncoact.patientreporter.algo.CurationFunction;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

import org.jetbrains.annotations.NotNull;

public final class GeneFusions {

    private GeneFusions() {
    }

    @NotNull
    public static Cell fusionContentType(@NotNull LinxFusionType type, @NotNull String geneName, @NotNull String transcript) {
        if (type.equals(LinxFusionType.IG_PROMISCUOUS) || type.equals(LinxFusionType.IG_KNOWN_PAIR)) {
            if (geneName.startsWith("@IG")) {
                return TableUtil.createContentCell(new Paragraph(transcript));
            } else {
                return TableUtil.createContentCell(new Paragraph(transcript))
                        .addStyle(ReportResources.dataHighlightLinksStyle())
                        .setAction(PdfAction.createURI(GeneFusions.transcriptUrl(transcript)));
            }
        } else {
            return TableUtil.createContentCell(new Paragraph(transcript))
                    .addStyle(ReportResources.dataHighlightLinksStyle())
                    .setAction(PdfAction.createURI(GeneFusions.transcriptUrl(transcript)));
        }
    }

    @NotNull
    public static List<LinxFusion> sort(@NotNull List<LinxFusion> fusions) {
        return fusions.stream().sorted((fusion1, fusion2) -> {
            if (fusion1.driverLikelihood() == fusion2.driverLikelihood()) {
                if (fusion1.geneStart().equals(fusion2.geneStart())) {
                    return fusion1.geneEnd().compareTo(fusion2.geneEnd());
                } else {
                    return fusion1.geneStart().compareTo(fusion2.geneStart());
                }
            } else {
                return fusion1.driverLikelihood() == LinxFusionDriverLikelihood.HIGH ? -1 : 1;
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    public static Set<String> uniqueGeneFusions(@NotNull List<LinxFusion> fusions) {
        Set<String> genes = Sets.newTreeSet();
        for (LinxFusion fusion : fusions) {
            genes.add(name(fusion));
        }
        return genes;
    }

    @NotNull
    public static String name(@NotNull LinxFusion fusion) {
        return CurationFunction.curateGeneNamePdf(fusion.geneStart()) + " - " + CurationFunction.curateGeneNamePdf(fusion.geneEnd());
    }

    @NotNull
    public static String transcriptUrl(@NotNull String transcriptField) {
        return "http://grch37.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=" + transcriptField;
    }

    @NotNull
    public static String displayStr(@NotNull LinxFusionType type) {
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
}
