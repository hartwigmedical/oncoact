package com.hartwig.oncoact.variant;

import javax.annotation.Nullable;

import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class AltTranscriptReportableInfo {

    @NotNull
    public abstract String transName();

    @NotNull
    public abstract String hgvsCoding();

    @NotNull
    public abstract String hgvsProtein();

    @NotNull
    public abstract String effects();

    @NotNull
    public abstract PurpleCodingEffect codingEffect();

}
