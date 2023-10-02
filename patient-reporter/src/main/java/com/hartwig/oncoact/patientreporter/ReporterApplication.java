package com.hartwig.oncoact.patientreporter;

import java.io.IOException;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

public class ReporterApplication {
    public static final String PANEL = "panel";

    public static void main(@NotNull String[] args) throws IOException {
        if (Arrays.asList(args).contains("-" + PANEL)) {
            PanelReporterApplication.main(args);
        } else {
            PatientReporterApplication.main(args);
        }
    }
}
