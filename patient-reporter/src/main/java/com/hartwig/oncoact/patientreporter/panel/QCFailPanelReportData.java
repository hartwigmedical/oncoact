package com.hartwig.oncoact.patientreporter.panel;

import com.hartwig.oncoact.patientreporter.PanelData;

import com.hartwig.oncoact.patientreporter.qcfail.FailedDatabase;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class QCFailPanelReportData implements PanelData {

    @NotNull
    public abstract Map<String, FailedDatabase> failedDatabaseMap();

}