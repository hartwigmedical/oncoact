package com.hartwig.oncoact.genome;

import java.util.Map;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public final class AminoAcids {

    private static final Map<String, String> TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER = Maps.newHashMap();

    private AminoAcids() {
    }

    static {
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Ala", "A"); // Alanine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Cys", "C"); // Cysteine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Asp", "D"); // Aspartic Acid
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Glu", "E"); // Glutamic Acid
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Phe", "F"); // Phenylalanine

        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Gly", "G"); // Glycine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("His", "H"); // Histidine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Ile", "I"); // Isoleucine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Lys", "K"); // Lysine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Leu", "L"); // Leucine

        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Met", "M"); // Methionine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Asn", "N"); // Asparagine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Pro", "P"); // Proline
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Gln", "Q"); // Glutamine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Arg", "R"); // Arginine

        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Ser", "S"); // Serine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Thr", "T"); // Threonine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Val", "V"); // Valine
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Trp", "W"); // Tryptophan
        TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.put("Tyr", "Y"); // Tyrosine
    }

    @NotNull
    public static String forceSingleLetterProteinAnnotation(@NotNull String proteinAnnotation) {
        String convertedProteinAnnotation = proteinAnnotation;
        for (Map.Entry<String, String> mapping : TRI_LETTER_AMINO_ACID_TO_SINGLE_LETTER.entrySet()) {
            convertedProteinAnnotation = convertedProteinAnnotation.replaceAll(mapping.getKey(), mapping.getValue());
        }
        return convertedProteinAnnotation;
    }
}
