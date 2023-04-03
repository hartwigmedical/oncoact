package com.hartwig.oncoact.kubernetes;

import com.hartwig.oncoact.graph.SchedulerDetails;

import org.immutables.value.Value;

@Value.Immutable
public interface KubernetesVolume extends SchedulerDetails {

    @Override
    default SchedulerDetailType type() {
        return SchedulerDetailType.KUBERNETES;
    }

    String mountPath();
}
