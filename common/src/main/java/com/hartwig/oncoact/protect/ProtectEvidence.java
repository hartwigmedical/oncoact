package com.hartwig.oncoact.protect;

import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Treatment;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ProtectEvidence {

    @Nullable
    public abstract String gene();

    @Nullable
    public abstract String transcript();

    @Nullable
    public abstract Boolean isCanonical();

    @NotNull
    public abstract String event();

    @Nullable
    public abstract Boolean eventIsHighDriver();

    public abstract boolean germline();

    public abstract boolean reported();

    @Nullable
    public abstract ClinicalTrial clinicalTrial();

    @Nullable
    public abstract Boolean matchGender();

    @Nullable
    public abstract Treatment treatment();

    public abstract boolean onLabel();

    @NotNull
    public abstract EvidenceLevel level();

    @NotNull
    public abstract EvidenceDirection direction();

    @NotNull
    public abstract Set<KnowledgebaseSource> sources();

}