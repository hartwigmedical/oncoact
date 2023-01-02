package com.hartwig.oncoact.genome;

final class Codons {

    public static final char[] DNA_BASES = {'G', 'A', 'T', 'C'};

    public static final String START_CODON = "ATG";

    public static final String STOP_CODON_1 = "TAA";
    public static final String STOP_CODON_2 = "TAG";
    public static final String STOP_CODON_3 = "TGA";

    public static final char UNKNOWN = '.';

    private static final char[] sCodonLookup =
            { 'K', 'N', 'N', 'K', 'I', 'I', 'I', 'M', 'T', 'T', 'T', 'T', 'R', 'S', 'S', 'R', 'X', 'Y', 'Y', 'X', 'L', 'F', 'F', 'L', 'S',
                    'S', 'S', 'S', 'X', 'C', 'C', 'W', 'Q', 'H', 'H', 'Q', 'L', 'L', 'L', 'L', 'P', 'P', 'P', 'P', 'R', 'R', 'R', 'R', 'E',
                    'D', 'D', 'E', 'V', 'V', 'V', 'V', 'A', 'A', 'A', 'A', 'G', 'G', 'G', 'G' };


    public static boolean isStopCodon(final String codon) {
        return codon.equals(STOP_CODON_1) || codon.equals(STOP_CODON_2) || codon.equals(STOP_CODON_3);
    }

    public static char codonToAminoAcid(final String codon) {
        return codonToAminoAcid(codon, 0);
    }

    public static char codonToAminoAcid(final String dna, int index) {
        try {
            // convert the 3 bases into a look up index
            int lookupIndex = baseToInt(dna.charAt(index)) * 16 + baseToInt(dna.charAt(index + 1)) * 4 + baseToInt(dna.charAt(index + 2));
            if (lookupIndex < sCodonLookup.length) {
                return sCodonLookup[lookupIndex];
            }
        } catch (IllegalArgumentException ignored) {
        }
        return UNKNOWN;
    }

    // note: this is used only for the lookup table. The ordering must be
    // kept this way otherwise the lookup table needs to be recreated
    private static int baseToInt(char base) {
        switch (base) {
            case 'A':
                return 0;
            case 'T':
                return 1;
            case 'C':
                return 2;
            case 'G':
                return 3;
        }
        throw new IllegalArgumentException("unknown base: " + base);
    }
}
