package com.hartwig.oncoact.patientreporter.panel;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.QsFormNumber;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PanelFailReason {

    PANEL_FAILURE("insufficient_dna_panel", QsFormNumber.FOR_345);

    @NotNull
    private final String identifier;
    @NotNull
    private final QsFormNumber qsFormNumber;

    PanelFailReason(@NotNull final String identifier, @NotNull final QsFormNumber qsFormNumber) {
        this.identifier = identifier;
        this.qsFormNumber = qsFormNumber;
    }

    @NotNull
    public String identifier() {
        return identifier;
    }

    @NotNull
    public String qcFormNumber() {
        return qsFormNumber.display();
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
