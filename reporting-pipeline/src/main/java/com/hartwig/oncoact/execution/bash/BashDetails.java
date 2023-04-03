package com.hartwig.oncoact.execution.bash;

import java.util.List;

import com.hartwig.oncoact.execution.bash.command.BashCommand;
import com.hartwig.oncoact.graph.SchedulerDetails;

import org.immutables.value.Value;

@Value.Immutable
public interface BashDetails extends SchedulerDetails {

    BashCommand command();

    @Override
    default SchedulerDetailType type() {
        return SchedulerDetailType.BASH;
    }

    static ImmutableBashDetails.Builder builder() {
        return ImmutableBashDetails.builder();
    }
}
