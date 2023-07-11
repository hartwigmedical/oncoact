package com.hartwig.oncoact.patientreporter.correction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
@JsonDeserialize(as = ImmutableCorrection.class)
@JsonSerialize(as = ImmutableCorrection.class)
public interface Correction {
    static ImmutableCorrection.Builder builder() {
        return ImmutableCorrection.builder();
    }

    @NotNull
    static Correction read(@NotNull String correctionPathName) throws IOException {
        BufferedReader lamaFileReader = new BufferedReader(new FileReader(correctionPathName));
        return new ObjectMapper().readValue(lamaFileReader, ImmutableCorrection.class);
    }

    @Nullable
    String specialRemark();

    @Nullable
    String comments();

    @Nullable
    Boolean isCorrectedReport();

    @Nullable
    Boolean isCorrectedReportExtern();
}
