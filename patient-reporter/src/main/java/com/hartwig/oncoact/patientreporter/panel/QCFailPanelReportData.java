package com.hartwig.oncoact.patientreporter.panel;

import com.hartwig.oncoact.patientreporter.PanelData;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class QCFailPanelReportData implements PanelData {

}