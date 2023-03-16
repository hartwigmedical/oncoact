package com.hartwig.oncoact.database.dao;

import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.reporting.AnalysedPatientReport;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.List;

class ReportingDAO {
    private static final int DB_BATCH_INSERT_SIZE = 1000;

    @NotNull
    private final DSLContext context;

    ReportingDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    void write(AnalysedPatientReport analysedPatientReport) {
    }
}
