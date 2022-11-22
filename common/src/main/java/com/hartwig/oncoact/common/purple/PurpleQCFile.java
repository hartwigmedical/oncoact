package com.hartwig.oncoact.common.purple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.genome.chromosome.GermlineAberration;
import com.hartwig.oncoact.common.utils.FileReaderUtils;

import org.jetbrains.annotations.NotNull;

public final class PurpleQCFile
{
    private static final DecimalFormat FORMAT = PurpleCommon.decimalFormat("0.0000");

    private static final String EXTENSION = ".purple.qc";

    @NotNull
    public static String generateFilename(@NotNull final String basePath, @NotNull final String sample)
    {
        return basePath + File.separator + sample + EXTENSION;
    }

    @NotNull
    public static PurpleQC read(@NotNull final String filename) throws IOException
    {
        return fromLines(Files.readAllLines(new File(filename).toPath()));
    }

    public static void write(@NotNull final String fileName, @NotNull final PurpleQC check) throws IOException
    {
        Files.write(new File(fileName).toPath(), toLines(check));
    }

    private static final String QC_STATUS = "QCStatus";
    private static final String METHOD = "Method";
    private static final String CN_SEGMENTS = "CopyNumberSegments";
    private static final String UNSUPPORTED_CN_SEGMENTS = "UnsupportedCopyNumberSegments";
    private static final String PURITY = "Purity";
    private static final String AMBER_GENDER = "AmberGender";
    private static final String COBALT_GENDER = "CobaltGender";
    private static final String DELETED_GENES = "DeletedGenes";
    private static final String CONTAMINATION = "Contamination";
    private static final String GERMLINE_ABERRATIONS = "GermlineAberrations";
    private static final String AMBER_MEAN_DEPTH = "AmberMeanDepth";

    private static PurpleQC fromLines(@NotNull final List<String> lines)
    {
        final ImmutablePurpleQC.Builder builder = ImmutablePurpleQC.builder();

        String qcStatusValues = FileReaderUtils.getValue(lines, QC_STATUS, "", PurpleCommon.DELIMITER);
        Set<PurpleQCStatus> statusSet = PurpleQCStatus.fromString(qcStatusValues);

        builder.method(FittedPurityMethod.valueOf(FileReaderUtils.getValue(lines, METHOD, FittedPurityMethod.NORMAL.toString(), PurpleCommon.DELIMITER)))
                .status(statusSet)
                .copyNumberSegments(Integer.parseInt(FileReaderUtils.getValue(lines, CN_SEGMENTS, "0", PurpleCommon.DELIMITER)))
                .unsupportedCopyNumberSegments(Integer.parseInt(FileReaderUtils.getValue(lines, UNSUPPORTED_CN_SEGMENTS, "0", PurpleCommon.DELIMITER)))
                .purity(Double.parseDouble(FileReaderUtils.getValue(lines, PURITY, "0", PurpleCommon.DELIMITER)))
                .amberGender(Gender.valueOf(FileReaderUtils.getValue(lines, AMBER_GENDER, null, PurpleCommon.DELIMITER)))
                .cobaltGender(Gender.valueOf(FileReaderUtils.getValue(lines, COBALT_GENDER, null, PurpleCommon.DELIMITER)))
                .deletedGenes(Integer.parseInt(FileReaderUtils.getValue(lines, DELETED_GENES, "0", PurpleCommon.DELIMITER)))
                .contamination(Double.parseDouble(FileReaderUtils.getValue(lines, CONTAMINATION, "0", PurpleCommon.DELIMITER)))
                .germlineAberrations(GermlineAberration.fromString(FileReaderUtils.getValue(
                        lines, GERMLINE_ABERRATIONS, GermlineAberration.NONE.toString(), PurpleCommon.DELIMITER)))
                .amberMeanDepth(Integer.parseInt(FileReaderUtils.getValue(lines, AMBER_MEAN_DEPTH, "0", PurpleCommon.DELIMITER)));

        return builder.build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull final PurpleQC check)
    {
        final List<String> result = Lists.newArrayList();

        result.add(QC_STATUS + PurpleCommon.DELIMITER + check);
        result.add(METHOD + PurpleCommon.DELIMITER + check.method());
        result.add(CN_SEGMENTS + PurpleCommon.DELIMITER + check.copyNumberSegments());
        result.add(UNSUPPORTED_CN_SEGMENTS + PurpleCommon.DELIMITER + check.unsupportedCopyNumberSegments());
        result.add(PURITY + PurpleCommon.DELIMITER + FORMAT.format(check.purity()));
        result.add(AMBER_GENDER + PurpleCommon.DELIMITER + check.amberGender());
        result.add(COBALT_GENDER + PurpleCommon.DELIMITER + check.cobaltGender());
        result.add(DELETED_GENES + PurpleCommon.DELIMITER + check.deletedGenes());
        result.add(CONTAMINATION + PurpleCommon.DELIMITER + check.contamination());
        result.add(GERMLINE_ABERRATIONS + PurpleCommon.DELIMITER + GermlineAberration.toString(check.germlineAberrations()));
        result.add(AMBER_MEAN_DEPTH + PurpleCommon.DELIMITER + check.amberMeanDepth());
        return result;
    }
}
