package com.hartwig.oncoact.patientreporter.qcfail;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.QsFormNumber;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum QCFailReason {

    WGS_PROCESSING_ISSUE("wgs_processing_issue",
            QCFailType.TECHNICAL_FAILURE,
            false,
            QsFormNumber.FOR_082,
            "Processing failure",
            "Whole Genome Sequencing could not be successfully performed on the \n"
                    + "received biomaterial(s) due to a lab technical issue."),
    WGS_ISOLATION_FAIL("wgs_isolation_fail",
            QCFailType.LOW_QUALITY_BIOPSY,
            false,
            QsFormNumber.FOR_082,
            "Insufficient quality of received biomaterial(s)",
            "Whole Genome Sequencing could not be successfully performed on the \n"
                    + "received biomaterial(s) because the DNA yield was insufficient after DNA isolation. \n"
                    + "This is likely due to poor quality or too small amounts of the received biomaterial(s).  "),
    WGS_TCP_SHALLOW_FAIL("wgs_tcp_shallow_fail",
            QCFailType.LOW_QUALITY_BIOPSY,
            false,
            QsFormNumber.FOR_082,
            "Insufficient quality of received biomaterial(s)",
            "Whole Genome Sequencing could not be successfully performed on the \n"
                    + "received biomaterial(s) because shallow sequencing analysis showed the tumor cell \n percentage was too low."),
    WGS_PREPARATION_FAIL("wgs_preparation_fail",
            QCFailType.LOW_QUALITY_BIOPSY,
            false,
            QsFormNumber.FOR_082,
            "Insufficient quality of received biomaterial(s)",
            "Whole Genome Sequencing could not be successfully performed on the \n"
                    + "received biomaterial(s) because the quality control of the sample preparation failed. \n"
                    + "This is likely due to poor quality of the received biomaterial(s)."),
    WGS_TUMOR_PROCESSING_ISSUE("wgs_tumor_processing_issue",
            QCFailType.LOW_QUALITY_BIOPSY,
            true,
            QsFormNumber.FOR_083,
            "Processing failure of tumor analysis",
            "Whole Genome Sequencing could not be successfully performed on the \n"
                    + "received tumor biomaterial due to a data processing issue."),
    WGS_PIPELINE_FAIL("wgs_pipeline_fail",
            QCFailType.LOW_QUALITY_BIOPSY,
            false,
            QsFormNumber.FOR_082,
            "Insufficient quality of received biomaterial(s)",
            "Whole Genome Sequencing could not be successfully performed on the \n"
                    + "received biomaterial(s), this is likely due to poor quality of the received biomaterial(s)."),
    WGS_TCP_FAIL("wgs_tcp_fail",
            QCFailType.LOW_QUALITY_BIOPSY,
            true,
            QsFormNumber.FOR_083,
            "Insufficient quality of received biomaterial(s)",
            "Whole Genome Sequencing could not be successfully performed on the \n"
                    + "received biomaterial(s) because sequencing analysis showed the tumor cell \n percentage was too low.");

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

    QCFailReason(@NotNull final String identifier, @NotNull final QCFailType type, final boolean deepWGSDataAvailable,
            @NotNull QsFormNumber qsFormNumber, @NotNull final String reportReason, @NotNull final String reportExplanation) {
        this.identifier = identifier;
        this.type = type;
        this.deepWGSDataAvailable = deepWGSDataAvailable;
        this.qsFormNumber = qsFormNumber;
        this.reportReason = reportReason;
        this.reportExplanation = reportExplanation;
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
