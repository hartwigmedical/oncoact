package com.hartwig.oncoact.graph;

public interface SchedulerDetails {

    SchedulerDetailType type();

    enum SchedulerDetailType {
        KUBERNETES,
        BASH,
    }
}
