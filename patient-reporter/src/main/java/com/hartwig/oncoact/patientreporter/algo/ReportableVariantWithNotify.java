package com.hartwig.oncoact.patientreporter.algo;

import com.hartwig.oncoact.variant.ReportableVariant;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
abstract class ReportableVariantWithNotify {

    @NotNull
    public abstract ReportableVariant variant();

    public abstract boolean notifyVariant();
}
