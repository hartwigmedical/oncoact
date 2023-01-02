package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hartwig.oncoact.cuppa.MolecularTissueOriginReporting;
import com.hartwig.oncoact.orange.peach.PeachEntry;
import com.hartwig.oncoact.patientreporter.PatientReport;
import com.hartwig.oncoact.patientreporter.SampleReport;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class AnalysedPatientReport implements PatientReport {

    @Override
    @NotNull
    public abstract SampleReport sampleReport();

    @NotNull
    @Override
    public abstract String qsFormNumber();

    @Nullable
    public abstract String clinicalSummary();

    @NotNull
    public abstract String specialRemark();

    @Nullable
    public abstract String pipelineVersion();

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
    public abstract Map<String, List<PeachEntry>> pharmacogeneticsGenotypes();

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