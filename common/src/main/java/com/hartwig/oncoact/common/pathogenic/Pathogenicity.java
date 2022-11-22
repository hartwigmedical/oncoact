package com.hartwig.oncoact.common.pathogenic;

import org.jetbrains.annotations.NotNull;

public enum Pathogenicity {
    PATHOGENIC(true),
    LIKELY_PATHOGENIC(true),
    BENIGN(false),
    LIKELY_BENIGN(false),
    BENIGN_BLACKLIST(false),
    CONFLICTING(false),
    UNKNOWN(false);

    private final boolean pathogenic;

    Pathogenicity(final boolean pathogenic) {
        this.pathogenic = pathogenic;
    }

    public boolean isPathogenic() {
        return pathogenic;
    }

    @NotNull
    public static Pathogenicity fromClinvarAnnotation(@NotNull final String clnSig, @NotNull final String clnSigConf) {

        /*
         * CLINVAR_PATHOGENIC - At least 1 interpretation of 'PATHOGENIC' and none ‘BENIGN’ or ‘LIKELY_BENIGN’
         * CLINVAR_LIKELY_PATHOGENIC - No interpretation of PATHOGENIC, but at least 1 interpretation of 'LIKELY_PATHOGENIC'
         *          and none ‘BENIGN’ or ‘LIKELY_BENIGN’
         * CLINVAR_CONFLICTING - Variant has both likely 'BENIGN'/'LIKELY_BENIGN' and 'PATHOGENIC'/'LIKELY_PATHOGENIC' interpretations
         * CLINVAR_LIKELY_BENIGN - No interpretation of 'BENIGN' and at least 1 interpretation of 'LIKELY_BENIGN' and
         *          none ‘PATHOGENIC’ or ‘LIKELY_PATHOGENIC’
         * CLINVAR_BENIGN - At least 1 interpretation of 'BENIGN' and none ‘PATHOGENIC’ or ‘LIKELY_PATHOGENIC’
         */

        if (clnSig.isEmpty()) {
            return UNKNOWN;
        }

        if (hasPathogenic(clnSig)) {
            return PATHOGENIC;
        }

        if (hasLikelyPathogenic(clnSig)) {
            return LIKELY_PATHOGENIC;
        }

        if (hasBenign(clnSig)) {
            return BENIGN;
        }

        if (hasLikelyBenign(clnSig)) {
            return LIKELY_BENIGN;
        }

        if (isConflicting(clnSig)) {
            if (hasPathogenic(clnSigConf) && !hasBenign(clnSigConf) && !hasLikelyBenign(clnSigConf)) {
                return PATHOGENIC;
            }

            if (hasLikelyPathogenic(clnSigConf) && !hasPathogenic(clnSigConf) && !hasBenign(clnSigConf) && !hasLikelyBenign(clnSigConf)) {
                return LIKELY_PATHOGENIC;
            }

            if (hasBenign(clnSigConf) && !hasPathogenic(clnSigConf) && !hasLikelyPathogenic(clnSigConf)) {
                return BENIGN;
            }

            if (hasLikelyBenign(clnSigConf) && !hasPathogenic(clnSigConf) && !hasLikelyPathogenic(clnSigConf) && !hasBenign(clnSigConf)) {
                return LIKELY_BENIGN;
            }

            if ((hasPathogenic(clnSigConf) || hasLikelyPathogenic(clnSigConf)) && (hasBenign(clnSigConf) || hasLikelyBenign(clnSigConf))) {
                return CONFLICTING;
            }
        }

        return UNKNOWN;
    }

    static final String CLINVAR_STR_PATHOGENIC = "Pathogenic";
    static final String CLINVAR_STR_LIKELY_PATHOGENIC = "Likely_pathogenic";
    static final String CLINVAR_STR_BENIGN = "Benign";
    static final String CLINVAR_STR_LIKELY_BENIGN = "Likely_benign";
    static final String CLINVAR_STR_CONFLICTING = "Conflicting";

    private static boolean hasPathogenic(@NotNull final String clinvarStr) {
        return clinvarStr.contains(CLINVAR_STR_PATHOGENIC);
    }

    private static boolean hasLikelyPathogenic(@NotNull final String clinvarStr) {
        return clinvarStr.contains(CLINVAR_STR_LIKELY_PATHOGENIC);
    }

    private static boolean hasBenign(@NotNull final String clinvarStr) {
        return clinvarStr.contains(CLINVAR_STR_BENIGN);
    }

    private static boolean hasLikelyBenign(@NotNull final String clinvarStr) {
        return clinvarStr.contains(CLINVAR_STR_LIKELY_BENIGN);
    }

    private static boolean isConflicting(@NotNull final String clinvarStr) {
        return clinvarStr.contains(CLINVAR_STR_CONFLICTING);
    }
}
