package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.cuppa.MolecularTissueOriginReporting;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.patientreporter.PatientReport;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class AnalysedPatientReport implements PatientReport {
    static ImmutableAnalysedPatientReport.Builder builder() {
        return ImmutableAnalysedPatientReport.builder();
    }

    @Override
    @NotNull
    public abstract PatientReporterData lamaPatientData();

    @NotNull
    @Override
    public abstract String qsFormNumber();

    @Nullable
    public abstract String clinicalSummary();

    @NotNull
    public abstract String specialRemark();

    @NotNull
    public abstract GenomicAnalysis genomicAnalysis();

    @Nullable
    public abstract MolecularTissueOriginReporting molecularTissueOriginReporting();

    @Nullable
    public abstract String molecularTissueOriginPlotPath();

    @NotNull
    public abstract String circosPlotPath();

    @NotNull
    @Override
    public abstract Map<String, List<PeachGenotype>> pharmacogeneticsGenotypes();

    @NotNull
    @Override
    public abstract HlaAllelesReportingData hlaAllelesReportingData();

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

    @Override
    @NotNull
    public abstract String reportDate();
}