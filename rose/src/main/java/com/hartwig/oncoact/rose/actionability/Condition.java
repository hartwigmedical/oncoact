package com.hartwig.oncoact.rose.actionability;

import org.jetbrains.annotations.NotNull;

public enum Condition {
    ONLY_HIGH,
    ALWAYS,
    HIGH_NO_ACTIONABLE,
    OTHER;

    @NotNull
    static Condition toCondition(@NotNull String conditionInput) {
        for (Condition condition : Condition.values()) {
            if (conditionInput.equals(condition.toString())) {
                return condition;
            }
        }

        throw new IllegalStateException("Cannot resolve condition: " + conditionInput);
    }
}
