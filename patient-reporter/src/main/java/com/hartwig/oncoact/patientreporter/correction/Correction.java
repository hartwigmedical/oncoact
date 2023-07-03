package com.hartwig.oncoact.patientreporter.correction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.oncoact.patientreporter.ImmutableCorrection;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCorrection.class)
@JsonSerialize(as = ImmutableCorrection.class)
public interface Correction {
    static ImmutableCorrection.Builder builder() {
        return ImmutableCorrection.builder();
    }

    String specialRemark();

    String comments();

    boolean isCorrectedReport();

    boolean isCorrectedReportExtern();

    @NotNull
    static Correction read(@NotNull String correctionPathName) throws IOException {
        BufferedReader lamaFileReader = new BufferedReader(new FileReader(correctionPathName));
        return new ObjectMapper().readValue(lamaFileReader, ImmutableCorrection.class);
    }
}
