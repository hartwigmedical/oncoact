package com.hartwig.oncoact.patientreporter;

import java.time.LocalDate;

import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.util.Formats;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class SampleReport {

    @NotNull
    public abstract SampleMetadata sampleMetadata();

    @NotNull
    public abstract String tumorReceivedSampleId();

    @Nullable
    public abstract String referenceReceivedSampleId();

    @NotNull
    public abstract Boolean germlineReportingLevel();

    @Nullable
    public abstract LocalDate refArrivalDate();

    @Nullable
    public abstract LocalDate tumorArrivalDate();

    @Nullable
    @Value.Derived
    public String refSampleBarcode() {
        return sampleMetadata().refSampleBarcode();
    }

    @NotNull
    @Value.Derived
    public String tumorSampleId() {
        return sampleMetadata().tumorSampleId();
    }

    @NotNull
    @Value.Derived
    public String tumorSampleBarcode() {
        return sampleMetadata().tumorSampleBarcode();
    }

    @NotNull
    @Value.Derived
    public String sampleNameForReport() {
        return sampleMetadata().sampleNameForReport();
    }

    @Nullable
    @Value.Derived
    public String earliestArrivalDate() {
        LocalDate refDate = refArrivalDate();
        LocalDate sampleDate = tumorArrivalDate();

        if (sampleDate == null) {
            return null;
        } else if (refDate == null || sampleDate.isBefore(refDate)) {
            return Formats.formatDate(sampleDate);
        } else {
            return Formats.formatDate(refDate);
        }
    }
}