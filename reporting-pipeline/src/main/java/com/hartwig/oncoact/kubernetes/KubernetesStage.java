package com.hartwig.oncoact.kubernetes;

import com.hartwig.oncoact.graph.ExecutionDetails;

import org.immutables.value.Value;

@Value.Immutable
public interface KubernetesStage extends ExecutionDetails {

    @Override
    default ExecutionDetailType type() {
        return ExecutionDetailType.KUBERNETES;
    }

    String imageVersion();

    String imageName();

    static ImmutableKubernetesStage.Builder builder() {
        return ImmutableKubernetesStage.builder();
    }
}
