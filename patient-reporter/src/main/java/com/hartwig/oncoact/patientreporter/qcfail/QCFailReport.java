package com.hartwig.oncoact.patientreporter.qcfail;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.patientreporter.PatientReport;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class QCFailReport implements PatientReport {

    @Override
    @NotNull
    public abstract PatientReporterData lamaPatientData();

    @Override
    @NotNull
    public abstract String qsFormNumber();

    @NotNull
    public abstract QCFailReason reason();

    @NotNull
    public abstract String failExplanation();

    @Nullable
    public abstract String wgsPurityString();

    @Nullable
    public abstract Set<PurpleQCStatus> purpleQC();

    @Override
    @NotNull
    public abstract Optional<String> comments();

    @Override
    public abstract boolean isCorrectedReport();

    @Override
    public abstract boolean isCorrectedReportExtern();

    @Override
    @NotNull
    public abstract String signaturePath();

    @Override
    @NotNull
    public abstract String logoRVAPath();

    @Override
    @NotNull
    public abstract String logoCompanyPath();

    @NotNull
    @Override
    public abstract Map<String, List<PeachGenotype>> pharmacogeneticsGenotypes();

    @Nullable
    @Override
    public abstract HlaAllelesReportingData hlaAllelesReportingData();

    @NotNull
    @Override
    public abstract String reportDate();
}