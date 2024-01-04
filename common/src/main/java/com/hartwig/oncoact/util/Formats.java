package com.hartwig.oncoact.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Formats {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter FILENAME_DT_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#'%'");
    private static final DecimalFormat PERCENTAGE_FORMAT_WITHOUT_PERCENT = new DecimalFormat("#");
    private static final DecimalFormat PERCENTAGE_FORMAT_WITH_DIGIT =
            new DecimalFormat("#.#'%'", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    public static final String NONE_STRING = "NONE";
    public static final String NA_STRING = "N/A";

    private Formats() {
    }

    @NotNull
    public static String formatPercentageDigit(double percentage) {
        return PERCENTAGE_FORMAT_WITH_DIGIT.format(percentage * 100);
    }

    @NotNull
    public static String formatPercentageRound(double percentage) {
        return PERCENTAGE_FORMAT.format(percentage * 100);
    }

    @NotNull
    public static String formatPercentageRoundWithoutPercent(double percentage) {
        return PERCENTAGE_FORMAT_WITHOUT_PERCENT.format(percentage * 100);
    }

    @NotNull
    public static String formatPercentage(double percentage) {
        return PERCENTAGE_FORMAT.format(percentage);
    }

    @NotNull
    public static String formatDate(@Nullable LocalDate date) {
        return date != null ? DATE_TIME_FORMATTER.format(date) : NA_STRING;
    }

    @NotNull
    public static String convertToFileDate(@NotNull String date) {
        return FILENAME_DT_FORMATTER.format(LocalDate.from(DATE_TIME_FORMATTER.parse(date)));
    }

    @NotNull
    public static String formatNullableString(@Nullable String string) {
        return string != null ? string : NA_STRING;
    }
}
