package com.hartwig.oncoact.lims.cohort;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LimsCohortConfig {

    @NotNull
    public abstract String cohortId(); //done

    public abstract boolean sampleContainsHospitalCenterId(); //done

    public abstract boolean reportGermline();

    public abstract boolean reportGermlineFlag();

    public abstract boolean reportConclusion(); //done

    public abstract boolean reportViral(); //done

    public abstract boolean reportPeach(); //done

    public abstract boolean requireHospitalId(); //done

    public abstract boolean requireHospitalPAId(); //done

    public abstract boolean requireHospitalPersonsStudy(); //done

    public abstract boolean requireHospitalPersonsRequester(); //done

    public abstract boolean requireAdditionalInformationForSidePanel(); //done
}
