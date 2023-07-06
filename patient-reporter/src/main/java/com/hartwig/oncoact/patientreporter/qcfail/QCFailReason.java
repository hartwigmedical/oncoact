package com.hartwig.oncoact.patientreporter.qcfail;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.QsFormNumber;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum QCFailReason {
    TECHNICAL_FAILURE("technical_failure", QCFailType.TECHNICAL_FAILURE, false, QsFormNumber.FOR_102, "Technical failure", "Whole Genome Sequencing could not be successfully performed on the received biomaterial(s) due to technical problems.", null),
    SUFFICIENT_TCP_QC_FAILURE("sufficient_tcp_qc_failure", QCFailType.LOW_QUALITY_BIOPSY, true, QsFormNumber.FOR_083, "Insufficient quality of received biomaterial(s)", "The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", "The tumor percentage based on molecular estimation was above the minimal of 20% tumor cells but could not be further analyzed due to insufficient quality."),
    INSUFFICIENT_TCP_SHALLOW_WGS("insufficient_tcp_shallow_wgs", QCFailType.LOW_QUALITY_BIOPSY, false, QsFormNumber.FOR_100, "Insufficient quality of received biomaterial(s)", "The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", "The tumor percentage based on molecular estimation was below the minimal of 20% tumor cells and could not be further analyzed."),
    INSUFFICIENT_TCP_DEEP_WGS("insufficient_tcp_deep_wgs", QCFailType.LOW_QUALITY_BIOPSY, true, QsFormNumber.FOR_100, "Insufficient quality of received biomaterial(s)", "The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", "The tumor percentage based on molecular estimation was below the minimal of 20% tumor cells and could not be further analyzed."),
    INSUFFICIENT_DNA("insufficient_dna", QCFailType.LOW_QUALITY_BIOPSY, false, QsFormNumber.FOR_082, "Insufficient quality of received biomaterial(s)", "The received biomaterial(s) did not meet the requirements that are needed for high quality Whole Genome Sequencing.", "Sequencing could not be performed due to insufficient DNA.");

    @NotNull
    private final String identifier;
    @NotNull
    private final QCFailType type;
    private final boolean deepWGSDataAvailable;
    @NotNull
    private final QsFormNumber qsFormNumber;
    @NotNull
    private final String reportReason;
    @NotNull
    private final String reportExplanation;
    @Nullable
    private final String reportExplanationDetail;

    QCFailReason(@NotNull final String identifier, @NotNull final QCFailType type, final boolean deepWGSDataAvailable,
                 @NotNull QsFormNumber qsFormNumber, @NotNull final String reportReason, @NotNull final String reportExplanation, @Nullable final String reportExplanationDetail) {
        this.identifier = identifier;
        this.type = type;
        this.deepWGSDataAvailable = deepWGSDataAvailable;
        this.qsFormNumber = qsFormNumber;
        this.reportReason = reportReason;
        this.reportExplanation = reportExplanation;
        this.reportExplanationDetail = reportExplanationDetail;
    }

    @NotNull
    public String identifier() {
        return identifier;
    }

    @NotNull
    public QCFailType type() {
        return type;
    }

    public boolean isDeepWGSDataAvailable() {
        return deepWGSDataAvailable;
    }

    @NotNull
    public String qcFormNumber() {
        return qsFormNumber.display();
    }

    @NotNull
    public String reportReason() {
        return reportReason;
    }

    @NotNull
    public String reportExplanation() {
        return reportExplanation;
    }

    @Nullable
    public String reportExplanationDetail() {
        return reportExplanationDetail;
    }

    @Nullable
    public static QCFailReason fromIdentifier(@Nullable String identifier) {
        if (identifier == null) {
            return null;
        }

        for (QCFailReason reason : QCFailReason.values()) {
            if (reason.identifier().equals(identifier)) {
                return reason;
            }
        }

        return null;
    }

    @NotNull
    public static List<String> validIdentifiers() {
        List<String> identifiers = Lists.newArrayList();
        for (QCFailReason reason : QCFailReason.values()) {
            identifiers.add(reason.identifier);
        }
        return identifiers;
    }
}
