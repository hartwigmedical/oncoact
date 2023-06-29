package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import org.jetbrains.annotations.NotNull;

public final class EvidenceItems {

    private static final String NONE = "None";

    private EvidenceItems() {
    }



    @NotNull
    public static Paragraph createLinksPublications(@NotNull Set<String> evidenceUrls) {
        Paragraph paragraphPublications = new Paragraph();
        int number = 0;
        for (String url : evidenceUrls) {
            if (!url.contains("google") && !url.isEmpty()) {
                //Google urls are filtered out
                number += 1;
                if (!paragraphPublications.isEmpty()) {
                    paragraphPublications.add(new Text(", "));
                }

                paragraphPublications.add(new Text(Integer.toString(number)).addStyle(ReportResources.urlStyle())
                        .setAction(PdfAction.createURI(url))).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
            }
        }

        return paragraphPublications;
    }

    @NotNull
    public static Paragraph createLinksSource(@NotNull Map<String, String> sourceUrls) {
        Paragraph paragraphSources = new Paragraph();

        for (Map.Entry<String, String> entry : sourceUrls.entrySet()) {
            if (!paragraphSources.isEmpty()) {
                paragraphSources.add(new Text(", "));
            }

            if (entry.getValue().isEmpty()) {
                paragraphSources.add(new Text(entry.getKey()).addStyle(ReportResources.subTextStyle()));
            } else {
                paragraphSources.add(new Text(entry.getKey()).addStyle(ReportResources.urlStyle())
                        .setAction(PdfAction.createURI(entry.getValue()))).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
            }
        }
        return paragraphSources;
    }
}