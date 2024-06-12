package com.hartwig.oncoact.util;

import java.util.Set;
import java.util.StringJoiner;

import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.Treatment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionabilityIntervation {

    @NotNull
    public static String therapyName(@Nullable ClinicalTrial clinicalTrial, @Nullable Treatment treatment) {
        boolean isClinicalTrial = clinicalTrial != null;
        boolean isTreatment = treatment != null;

        if (isClinicalTrial && isTreatment) {
            throw new IllegalStateException(
                    "An actionable event cannot be both a treatment: " + treatment + " and clinical trial: " + clinicalTrial);
        }

        if (isTreatment) {
            return treatment.name();
        } else {
            assert clinicalTrial != null;
            return setToField(clinicalTrial.therapyNames());
        }
    }

    @NotNull
    public static String setToField(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(",");
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }
}
