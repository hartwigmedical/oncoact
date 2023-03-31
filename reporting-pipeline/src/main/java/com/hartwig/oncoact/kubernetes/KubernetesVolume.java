package com.hartwig.oncoact.kubernetes;

import com.hartwig.oncoact.graph.ExecutionDetails;

public interface KubernetesVolume extends ExecutionDetails {

    @Override
    default ExecutionDetailType type() {
        return ExecutionDetailType.KUBERNETES;
    }

    String mountPath();
}
