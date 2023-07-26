package com.hartwig.oncoact.patientreporter.qcfail;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.QsFormNumber;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum QCFailReason {
    HARTWIG_PROCESSING_ISSUE("hartwig_processing_issue", QCFailType.TECHNICAL_FAILURE, false, QsFormNumber.FOR_102, "Processing failure", "Whole Genome Sequencing could not be successfully performed on the received biomaterial(s) due to a processing issue."),
    ISOLATION_FAIL("isolation_fail", QCFailType.LOW_QUALITY_BIOPSY, true, QsFormNumber.FOR_083, "Insufficient quality of received biomaterial(s)", "Whole Genome Sequencing could not be successfully performed on the received biomaterial(s) because not enough DNA was present after DNA isolation. This is likely due to poor quality of the received biomaterials(s)."),
    TCP_SHALLOW_FAIL("tcp_shallow_fail", QCFailType.LOW_QUALITY_BIOPSY, false, QsFormNumber.FOR_100, "Insufficient quality of received biomaterial(s)","Whole Genome Sequencing could not be successfully performed on the received biomaterial(s) because shallow sequencing analysis showed the tumor cell percentage was too low."),
    PREPARATION_FAIL("preparation_fail", QCFailType.LOW_QUALITY_BIOPSY, true, QsFormNumber.FOR_100, "Insufficient quality of received biomaterial(s)", "Whole Genome Sequencing could not be successfully performed on the received biomaterial(s) because not enough DNA was present after sample preparation. This is likely due to poor quality of the received biomaterials(s)."),
    HARTWIG_TUMOR_PROCESSING_ISSUE("hartwig_tumor_processing_issue", QCFailType.LOW_QUALITY_BIOPSY, false, QsFormNumber.FOR_082, "Processing failure of tumor analysis", "Whole Genome Sequencing could not be successfully performed on the received tumor biomaterial due to a processing issue."),
    PIPELINE_FAIL("pipeline_fail", QCFailType.LOW_QUALITY_BIOPSY, false, QsFormNumber.FOR_082, "Insufficient quality of received biomaterial(s)", "Whole Genome Sequencing could not be successfully performed on the received biomaterial(s), this is likely due to poor quality of the received biomaterials(s)."),
    TCP_WGS_FAIL("tcp_wgs_fail",  QCFailType.LOW_QUALITY_BIOPSY, false, QsFormNumber.FOR_082, "Insufficient quality of received biomaterial(s)", "Whole Genome Sequencing could not be successfully performed on the received biomaterial(s) because sequecning analysis showed the tumor cell percentage was too low.");

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
