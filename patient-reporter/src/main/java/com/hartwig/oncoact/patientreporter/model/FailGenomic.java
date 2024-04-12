package com.hartwig.oncoact.patientreporter.model;

import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class FailGenomic {

    @Nullable
    public abstract String purityString();

    @Nullable
    public abstract Set<PurpleQCStatus> purpleQC();

    @NotNull
    public abstract List<Pharmacogenetics> pharmacogenetics();

    @Nullable
    public abstract List<HlaAlleleFail> hlaAlleles();

    @NotNull
    public abstract String hlaQc();

    public static ImmutableFailGenomic.Builder builder() {
        return ImmutableFailGenomic.builder();
    }
}