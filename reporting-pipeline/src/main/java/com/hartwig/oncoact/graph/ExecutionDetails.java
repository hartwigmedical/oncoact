package com.hartwig.oncoact.graph;

public interface ExecutionDetails {

    ExecutionDetailType type();

    enum ExecutionDetailType {
        KUBERNETES,
    }
}
