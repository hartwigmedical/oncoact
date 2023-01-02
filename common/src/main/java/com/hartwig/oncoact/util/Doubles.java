package com.hartwig.oncoact.util;

public final class Doubles {

    private static final double EPSILON = 1e-10;

    private Doubles() {
    }

    public static boolean equal(double first, double second) {
        return Math.abs(first - second) < EPSILON;
    }

    public static boolean greaterThan(double value, double reference) {
        return value - reference > EPSILON;
    }

    public static boolean positive(double value) {
        return greaterThan(value, 0);
    }

}
