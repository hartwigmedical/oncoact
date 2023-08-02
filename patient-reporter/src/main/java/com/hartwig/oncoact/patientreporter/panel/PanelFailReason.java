package com.hartwig.oncoact.patientreporter.panel;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.QsFormNumber;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PanelFailReason {

    PANEL_FAILURE("insufficient_dna_panel", QsFormNumber.FOR_345, "Insufficient quality of received biomaterial(s)",
            "Next Generation Sequencing could not be successfully performed on the received biomaterial(s) because not enough " +
                    "DNA was present after DNA isolation. This is likely due to poor quality of the received biomaterials(s).");
    @NotNull
    private final String identifier;
    @NotNull
    private final QsFormNumber qsFormNumber;

    @NotNull
    private final String reportReason;
    @NotNull
    private final String reportExplanation;

    PanelFailReason(@NotNull final String identifier, @NotNull final QsFormNumber qsFormNumber, @NotNull final String reportReason,
                    @NotNull final String reportExplanation) {
        this.identifier = identifier;
        this.qsFormNumber = qsFormNumber;
        this.reportReason = reportReason;
        this.reportExplanation = reportExplanation;
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
