package com.hartwig.oncoact.patientreporter;

import java.io.IOException;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

public class ReporterApplication {
    public static final String PANEL = "panel";

    public static void main(@NotNull String[] args) throws IOException {
        if (Stream.of(args).anyMatch(arg -> arg.endsWith(PANEL))) {
            PanelReporterApplication.main(args);
        } else {
            PatientReporterApplication.main(args);
        }
    }
}
