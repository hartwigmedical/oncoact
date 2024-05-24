package com.hartwig.oncoact.patientreporter.cfreport.data;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public final class EvidenceItems {

    private static final String NONE = "None";
    private final ReportResources reportResources;

    public EvidenceItems(@NotNull ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    @NotNull
    public static String shortenTrialName(@NotNull String trialName) {

        if (trialName.length() > 150) {
            return StringUtils.substringBeforeLast(trialName.substring(0, 85), " ") + " ... " + StringUtils.substringAfter(trialName.substring(trialName.length() - 85), " ");
        } else {
            return trialName;
        }
    }

    @NotNull
    public Paragraph createLinksPublications(@NotNull Set<String> evidenceUrls) {
        Paragraph paragraphPublications = new Paragraph();
        int number = 0;
        for (String url : evidenceUrls) {
            if (!url.contains("google") && !url.isEmpty()) {
                //Google urls are filtered out
                number += 1;
                if (!paragraphPublications.isEmpty()) {
                    paragraphPublications.add(new Text(", "));
                }

                paragraphPublications.add(new Text(Integer.toString(number)).addStyle(reportResources.urlStyle())
                        .setAction(PdfAction.createURI(url))).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
            }
        }

        return paragraphPublications;
    }

    @NotNull
    public Paragraph createLinksSource(@NotNull Map<String, String> sourceUrls) {
        Paragraph paragraphSources = new Paragraph();

        for (Map.Entry<String, String> entry : sourceUrls.entrySet()) {
            if (!paragraphSources.isEmpty()) {
                paragraphSources.add(new Text(", "));
            }

            if (entry.getValue().isEmpty()) {
                paragraphSources.add(new Text(entry.getKey()).addStyle(reportResources.subTextStyle()));
            } else {
                paragraphSources.add(new Text(entry.getKey()).addStyle(reportResources.urlStyle())
                        .setAction(PdfAction.createURI(entry.getValue()))).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
            }
        }
        return paragraphSources;
    }

    @NotNull
    public Paragraph createSourceIclusion(@NotNull Map<String, String> sourceUrls) {
        Paragraph paragraphSources = new Paragraph();

        for (Map.Entry<String, String> entry : sourceUrls.entrySet()) {
            if (!paragraphSources.isEmpty()) {
                paragraphSources.add(new Text(", "));
            }

            if (entry.getValue().isEmpty()) {
                paragraphSources.add(new Text(entry.getKey()).addStyle(reportResources.subTextStyle()));
            } else {
                paragraphSources.add(new Text(entry.getKey()).addStyle(reportResources.subTextStyle()))
                        .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
            }
        }
        return paragraphSources;
    }

    @NotNull
    public Paragraph createClinicalTrialLink(@NotNull String nctId) {
        Paragraph paragraphSources = new Paragraph();
        String link = "https://clinicaltrials.gov/study/" + nctId;

        paragraphSources.add(new Text(nctId).addStyle(reportResources.urlStyle())
                .setAction(PdfAction.createURI(link))).setFixedLeading(ReportResources.BODY_TEXT_LEADING);


        return paragraphSources;
    }
}