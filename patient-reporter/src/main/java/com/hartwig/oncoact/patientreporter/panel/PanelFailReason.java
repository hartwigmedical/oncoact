package com.hartwig.oncoact.patientreporter.panel;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.QsFormNumber;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PanelFailReason {

    PANEL_FAILURE("insufficient_dna_panel", QsFormNumber.FOR_345, "Insufficient quality of received biomaterial(s)", "The received biomaterial(s) did not meet the requirements that are needed for high quality Next Generation Sequencing.", "Sequencing could not be performed due to insufficient DNA.");

    @NotNull
    private final String identifier;
    @NotNull
    private final QsFormNumber qsFormNumber;

    @NotNull
    private final String reportReason;
    @NotNull
    private final String reportExplanation;
    @NotNull
    private final String reportExplanationDetail;

    PanelFailReason(@NotNull final String identifier, @NotNull final QsFormNumber qsFormNumber, @NotNull final String reportReason,
                    @NotNull final String reportExplanation, @NotNull final String reportExplanationDetail) {
        this.identifier = identifier;
        this.qsFormNumber = qsFormNumber;
        this.reportReason = reportReason;
        this.reportExplanation = reportExplanation;
        this.reportExplanationDetail = reportExplanationDetail;
    }

    @NotNull
    public String identifier() {
        return identifier;
    }

    @NotNull
    public String qcFormNumber() {
        return qsFormNumber.display();
    }

    @NotNull
    public String reportReason() {
        return reportReason;
    }

    @NotNull
    public String reportExplanation() {
        return reportExplanation;
    }

    @NotNull
    public String reportExplanationDetail() {
        return reportExplanationDetail;
    }

    @Nullable
    public static PanelFailReason fromIdentifier(@Nullable String identifier) {
        if (identifier == null) {
            return null;
        }

        for (PanelFailReason reason : PanelFailReason.values()) {
            if (reason.identifier().equals(identifier)) {
                return reason;
            }
        }

        return null;
    }

    @NotNull
    public static List<String> validIdentifiers() {
        List<String> identifiers = Lists.newArrayList();
        for (PanelFailReason reason : PanelFailReason.values()) {
            identifiers.add(reason.identifier);
        }
        return identifiers;
    }
}
