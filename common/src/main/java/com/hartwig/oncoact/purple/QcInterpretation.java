package com.hartwig.oncoact.purple;

import com.hartwig.hmftools.datamodel.purple.PurpleFit;
import com.hartwig.hmftools.datamodel.purple.PurpleFittedPurityMethod;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;

import org.jetbrains.annotations.NotNull;

public class QcInterpretation {
    public static boolean containsTumorCells(@NotNull PurpleFit purpleFit) {
        return purpleFit.fittedPurityMethod() != PurpleFittedPurityMethod.NO_TUMOR
                && !purpleFit.qc().status().contains(PurpleQCStatus.FAIL_NO_TUMOR);
    }

    public static boolean hasSufficientQuality(@NotNull PurpleFit purpleFit) {
        return purpleFit.qc().status().size() == 1 &&  purpleFit.qc().status().contains(PurpleQCStatus.PASS);
    }
}
