package com.hartwig.oncoact.patientreporter.qcfail;

import com.hartwig.oncoact.patientreporter.ReportData;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class QCFailReportData implements ReportData {

    @NotNull
    public abstract Map<String, FailedDatabase> failedDatabaseMap();
}
