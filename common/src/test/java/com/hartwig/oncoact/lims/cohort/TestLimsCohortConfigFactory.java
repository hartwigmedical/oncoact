package com.hartwig.oncoact.lims.cohort;

import org.jetbrains.annotations.NotNull;

public final class TestLimsCohortConfigFactory {

    private TestLimsCohortConfigFactory() {
    }

    @NotNull
    public static LimsCohortConfig createCPCTCohortConfig() {
        return createCohortConfig("CPCT", true, false, false, false, false, true, false, false, true, false, false);
    }

    @NotNull
    public static LimsCohortConfig createCOLOCohortConfig() {
        return createCohortConfig("COLO", false, true, false, true, true, true, false, false, true, false, false);
    }

    @NotNull
    public static LimsCohortConfig createCORECohortConfig() {
        return createCohortConfig("CORE", true, true, false, true, true, true, true, true, false, true, true);
    }

    @NotNull
    public static LimsCohortConfig createWIDECohortConfig() {
        return createCohortConfig("WIDE", true, true, true, true, true, true, false, true, true, false, true);
    }

    @NotNull
    public static LimsCohortConfig createCOREDBCohortConfig() {
        return createCohortConfig("COREDB", true, true, true, false, true, true, true, true, true, false, true);
    }

    @NotNull
    public static LimsCohortConfig createACTINCohortConfig() {
        return createCohortConfig("ACTIN", true, true, false, false, true, true, false, false, false, false, false);
    }

    @NotNull
    private static LimsCohortConfig createCohortConfig(@NotNull String cohortId, boolean sampleContainsHospitalCenterId,
            boolean reportGermline, boolean reportGermlineFlag, boolean reportConclusion, boolean reportViral, boolean reportPeach,
            boolean requireHospitalId, boolean requireHospitalPAId, boolean requireHospitalPersonsStudy,
            boolean requireHospitalPersonsRequester, boolean requireAdditionalInformationForSidePanel) {
        return ImmutableLimsCohortConfig.builder()
                .cohortId(cohortId)
                .sampleContainsHospitalCenterId(sampleContainsHospitalCenterId)
                .reportGermline(reportGermline)
                .reportGermlineFlag(reportGermlineFlag)
                .reportConclusion(reportConclusion)
                .reportViral(reportViral)
                .reportPeach(reportPeach)
                .requireHospitalId(requireHospitalId)
                .requireHospitalPAId(requireHospitalPAId)
                .requireHospitalPersonsStudy(requireHospitalPersonsStudy)
                .requireHospitalPersonsRequester(requireHospitalPersonsRequester)
                .requireAdditionalInformationForSidePanel(requireAdditionalInformationForSidePanel)
                .build();
    }
}
