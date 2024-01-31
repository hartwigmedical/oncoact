package com.hartwig.oncoact.rose.conclusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.chord.ChordRecord;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.cuppa.CuppaPrediction;
import com.hartwig.hmftools.datamodel.hla.ImmutableLilacRecord;
import com.hartwig.hmftools.datamodel.hla.LilacAllele;
import com.hartwig.hmftools.datamodel.hla.LilacRecord;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.ImmutableLinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxFusionType;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptModelTestFactory;
import com.hartwig.oncoact.copynumber.CopyNumberInterpretation;
import com.hartwig.oncoact.copynumber.PurpleGainLossData;
import com.hartwig.oncoact.drivergene.DriverCategory;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.drivergene.TestDriverGeneFactory;
import com.hartwig.oncoact.orange.TestOrangeFactory;
import com.hartwig.oncoact.orange.chord.TestChordFactory;
import com.hartwig.oncoact.orange.cuppa.TestCuppaFactory;
import com.hartwig.oncoact.orange.lilac.TestLilacFactory;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.orange.virus.TestVirusInterpreterFactory;
import com.hartwig.oncoact.rose.ImmutableRoseData;
import com.hartwig.oncoact.rose.RoseData;
import com.hartwig.oncoact.rose.actionability.ActionabilityEntry;
import com.hartwig.oncoact.rose.actionability.ActionabilityKey;
import com.hartwig.oncoact.rose.actionability.Condition;
import com.hartwig.oncoact.rose.actionability.ImmutableActionabilityEntry;
import com.hartwig.oncoact.rose.actionability.ImmutableActionabilityKey;
import com.hartwig.oncoact.rose.actionability.TypeAlteration;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ConclusionAlgoTest {

    @Test
    public void runsOnTestData() {
        ImmutableRoseData.Builder builder = ImmutableRoseData.builder();
        RoseData minimal = builder.orange(TestOrangeFactory.createMinimalTestOrangeRecord())
                .clinicalTranscriptsModel(ClinicalTranscriptModelTestFactory.createEmpty())
                .build();
        assertNotNull(ConclusionAlgo.generateConclusion(minimal));

        RoseData proper = builder.orange(TestOrangeFactory.createProperTestOrangeRecord()).build();
        assertNotNull(ConclusionAlgo.generateConclusion(proper));
    }

    @Test
    public void canGenerateCUPPAConclusion() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("CUPPA", TypeAlteration.CUPPA, "CUPPA", Condition.OTHER, "Molecular Tissue of Origin classifier: XXXX.");

        CuppaPrediction cuppaPrediction = TestCuppaFactory.builder().cancerType("Melanoma").likelihood(0.996).build();

        ConclusionAlgo.generateCUPPAConclusion(conclusion, cuppaPrediction, actionabilityMap);

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- Molecular Tissue of Origin classifier: Melanoma (likelihood: 99.6%).");
    }

    @Test
    public void canGenerateCUPPAConclusionInconclusive() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = create("CUPPA_INCONCLUSIVE",
                TypeAlteration.CUPPA_INCONCLUSIVE,
                "CUPPA_INCONCLUSIVE",
                Condition.OTHER,
                "Molecular Tissue of Origin classifier: Inconclusive (highest likelihood: xxx - xx%).");

        CuppaPrediction cuppaPrediction = TestCuppaFactory.builder().cancerType("Melanoma").likelihood(0.45).build();

        ConclusionAlgo.generateCUPPAConclusion(conclusion, cuppaPrediction, actionabilityMap);

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- Molecular Tissue of Origin classifier: Inconclusive.");
    }

    @Test
    public void canGenerateCUPPAConclusionInconclusiveWithLocation() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = create("CUPPA_INCONCLUSIVE",
                TypeAlteration.CUPPA_INCONCLUSIVE,
                "CUPPA_INCONCLUSIVE",
                Condition.OTHER,
                "Molecular Tissue of Origin classifier: Inconclusive (highest likelihood: xxx - xx%).");

        CuppaPrediction cuppaPrediction = TestCuppaFactory.builder().cancerType("Melanoma").likelihood(0.601).build();

        ConclusionAlgo.generateCUPPAConclusion(conclusion, cuppaPrediction, actionabilityMap);

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- Molecular Tissue of Origin classifier: Inconclusive (highest likelihood: Melanoma-60.1%).");
    }

    @Test
    public void canGenerateVariantsConclusion() {
        List<ReportableVariant> reportableVariants = createTestReportableVariants();
        Map<String, DriverGene> driverGenesMap = Maps.newHashMap();
        driverGenesMap.put("CHEK2", createDriverGene("CHEK2", DriverCategory.TSG));
        driverGenesMap.put("APC", createDriverGene("APC", DriverCategory.ONCO));
        driverGenesMap.put("BRCA2", createDriverGene("BRCA2", DriverCategory.TSG));
        driverGenesMap.put("BRCA1", createDriverGene("BRCA1", DriverCategory.TSG));
        driverGenesMap.put("TERT", createDriverGene("TERT", DriverCategory.ONCO));
        driverGenesMap.put("KRAS", createDriverGene("KRAS", DriverCategory.ONCO));

        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("CHEK2", TypeAlteration.INACTIVATION, "CHEK2", Condition.ONLY_HIGH, "CHEK2 inactivation");
        actionabilityMap = append(actionabilityMap, "APC", TypeAlteration.ACTIVATING_MUTATION, "APC", Condition.HIGH_NO_ACTIONABLE, "APC");
        actionabilityMap =
                append(actionabilityMap, "BRCA2", TypeAlteration.INACTIVATION, "BRCA2", Condition.ONLY_HIGH, "BRCA2 inactivation");
        actionabilityMap =
                append(actionabilityMap, "BRCA1", TypeAlteration.INACTIVATION, "BRCA1", Condition.ONLY_HIGH, "BRCA1 inactivation");
        actionabilityMap = append(actionabilityMap, "germline", TypeAlteration.GERMLINE, "germline", Condition.ONLY_HIGH, "germline");
        actionabilityMap =
                append(actionabilityMap, "NOT_BIALLELIC", TypeAlteration.NOT_BIALLELIC, "NOT_BIALLELIC", Condition.OTHER, "not biallelic");
        actionabilityMap = append(actionabilityMap,
                "TERT",
                TypeAlteration.PROMOTER_MUTATION,
                "TERT",
                Condition.HIGH_NO_ACTIONABLE,
                "TERT promotor mutation");
        actionabilityMap =
                append(actionabilityMap, "KRAS", TypeAlteration.ACTIVATING_MUTATION, "KRAS", Condition.HIGH_NO_ACTIONABLE, "KRAS");
        ChordRecord analysis = TestChordFactory.builder().hrdValue(0.8).hrStatus(ChordStatus.HR_DEFICIENT).build();

        ConclusionAlgo.generateVariantConclusion(conclusion,
                reportableVariants,
                actionabilityMap,
                driverGenesMap,
                Sets.newHashSet(),
                Sets.newHashSet(),
                Sets.newHashSet(),
                analysis);

        assertEquals(6, conclusion.size());
        assertTrue(conclusion.contains("- APC (p.Val600Arg) APC"));
        assertTrue(conclusion.contains("- CHEK2 (c.123A>C splice) CHEK2 inactivating mutation not biallelic"));
        assertTrue(conclusion.contains("- BRCA1 (c.123A>C, p.Val600Arg, p.Val602Arg) BRCA1 inactivation"));
        assertTrue(conclusion.contains("- BRCA2 (c.1235A>C splice) BRCA2 inactivation"));
        assertTrue(conclusion.contains("- TERT (c.-124C>T) TERT promotor mutation"));
        assertTrue(conclusion.contains("- KRAS (p.Arg58* (p.Arg59*)) KRAS"));
    }

    @Test
    public void canGenerateCNVConclusion() {
        Set<PurpleGainLossData> gainLoss = createTestGainsLosses();
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("BRAF", TypeAlteration.AMPLIFICATION, "BRAF", Condition.ALWAYS, "BRAF");
        actionabilityMap = append(actionabilityMap, "KRAS", TypeAlteration.AMPLIFICATION, "KRAS", Condition.ALWAYS, "KRAS");
        actionabilityMap = append(actionabilityMap, "CDKN2A", TypeAlteration.LOSS, "CDKN2A", Condition.ALWAYS, "CDKN2A");
        actionabilityMap = append(actionabilityMap, "EGFR", TypeAlteration.LOSS, "EGFR", Condition.ALWAYS, "EGFR");

        ConclusionAlgo.generateCNVConclusion(conclusion, gainLoss, actionabilityMap, Sets.newHashSet(), Sets.newHashSet(), true);

        assertEquals(4, conclusion.size());
        assertTrue(conclusion.contains("- BRAF (copies: 4) BRAF"));
        assertTrue(conclusion.contains("- KRAS (copies: 8) KRAS"));
        assertTrue(conclusion.contains("- CDKN2A (copies: 0) CDKN2A"));
        assertTrue(conclusion.contains("- EGFR (copies: 0) EGFR"));
    }

    @Test
    public void canGenerateFusionConclusion() {
        Set<LinxFusion> fusions = createTestFusions();
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("BRAF", TypeAlteration.INTERNAL_DELETION, "BRAF", Condition.ALWAYS, "BRAF");
        actionabilityMap = append(actionabilityMap, "MET", TypeAlteration.FUSION, "MET", Condition.ALWAYS, "MET");
        actionabilityMap = append(actionabilityMap, "EGFR", TypeAlteration.KINASE_DOMAIN_DUPLICATION, "EGFR", Condition.ALWAYS, "EGFR");

        ConclusionAlgo.generateFusionConclusion(conclusion, fusions, actionabilityMap, Sets.newHashSet(), Sets.newHashSet());

        assertEquals(4, conclusion.size());
        assertTrue(conclusion.contains("- BRAF - BRAF ( - ) BRAF"));
        assertTrue(conclusion.contains("- CAV2 - MET ( - ) MET"));
        assertTrue(conclusion.contains("- EGFR - EGFR ( - ) EGFR"));
        assertTrue(conclusion.contains("- EGFR - EGFR ( - ) EGFR"));
    }

    @Test
    public void canGenerateHomozygousDisruptionConclusion() {
        Set<HomozygousDisruption> homozygousDisruptions =
                Sets.newHashSet(createHomozygousDisruption("PTEN"), createHomozygousDisruption("KRAS"));
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("PTEN", TypeAlteration.INACTIVATION, "PTEN", Condition.ALWAYS, "PTEN");
        actionabilityMap = append(actionabilityMap, "KRAS", TypeAlteration.INACTIVATION, "KRAS", Condition.ALWAYS, "KRAS");

        ConclusionAlgo.generateHomozygousDisruptionConclusion(conclusion,
                homozygousDisruptions,
                actionabilityMap,
                Sets.newHashSet(),
                Sets.newHashSet());

        assertEquals(2, conclusion.size());
        assertEquals(conclusion.get(0), "- PTEN PTEN");
        assertEquals(conclusion.get(1), "- KRAS KRAS");
    }

    @Test
    public void canGenerateVirusOnlyVirusConclusion() {
        Set<AnnotatedVirus> viruses = createTestVirusInterpreterEntries();
        List<LilacAllele> alleles = Lists.newArrayList();
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("EBV", TypeAlteration.POSITIVE, "EBV", Condition.ONLY_HIGH, "EBV");
        actionabilityMap = append(actionabilityMap, "HPV", TypeAlteration.POSITIVE, "HPV", Condition.ONLY_HIGH, "HPV");
        actionabilityMap = append(actionabilityMap, "MCV", TypeAlteration.POSITIVE, "MCV", Condition.ONLY_HIGH, "MCV");

        ConclusionAlgo.generateVirusHLAConclusion(conclusion, viruses, alleles, actionabilityMap, Sets.newHashSet(), Sets.newHashSet());

        assertEquals(2, conclusion.size());
        assertTrue(conclusion.contains("- MCV MCV"));
        assertTrue(conclusion.contains("- HPV HPV"));
    }

    @Test
    public void canGenerateHLAOnlyConclusion() {
        Set<AnnotatedVirus> viruses = Sets.newHashSet();
        List<LilacAllele> alleles = createTestLilacRecord().alleles();
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("HLA-A*02", TypeAlteration.POSITIVE, "HLA-A*02", Condition.ALWAYS, "HLA-A*02 conclusion");

        ConclusionAlgo.generateVirusHLAConclusion(conclusion, viruses, alleles, actionabilityMap, Sets.newHashSet(), Sets.newHashSet());

        assertEquals(1, conclusion.size());
        assertTrue(conclusion.contains("- HLA-A*02 HLA-A*02 conclusion"));
    }

    @Test
    public void canGenerateHLAVirusBothConclusion() {
        Set<AnnotatedVirus> viruses = createTestVirusInterpreterEntries();
        List<LilacAllele> alleles = createTestLilacRecord().alleles();
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("HPV-16 | HLA-A*02", TypeAlteration.POSITIVE, "HPV-16 | HLA-A*02", Condition.ONLY_HIGH, "HPV HLA conclusion");

        ConclusionAlgo.generateVirusHLAConclusion(conclusion, viruses, alleles, actionabilityMap, Sets.newHashSet(), Sets.newHashSet());

        assertEquals(1, conclusion.size());
        assertTrue(conclusion.contains("- HPV-16 | HLA-A*02 HPV HLA conclusion"));
    }

    @Test
    public void canGenerateHrdConclusion() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = create("HRD", TypeAlteration.POSITIVE, "HRD", Condition.ALWAYS, "HRD");

        ChordRecord chord = TestChordFactory.builder().hrdValue(0.8).hrStatus(ChordStatus.HR_DEFICIENT).build();
        Set<String> hrd = Sets.newHashSet("BRCA1");

        ConclusionAlgo.generateHrdConclusion(conclusion, chord, actionabilityMap, Sets.newHashSet(), Sets.newHashSet(), hrd);

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- HRD (0.8) HRD");
    }

    @Test
    public void canGenerateHrpConclusion() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = create("HRD", TypeAlteration.POSITIVE, "HRD", Condition.ALWAYS, "HRD");

        ChordRecord chord = TestChordFactory.builder().hrdValue(0.4).hrStatus(ChordStatus.HR_PROFICIENT).build();
        ConclusionAlgo.generateHrdConclusion(conclusion, chord, actionabilityMap, Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet());

        assertEquals(0, conclusion.size());
    }

    @Test
    public void canGenerateMSIConclusion() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = create("MSI", TypeAlteration.POSITIVE, "MSI", Condition.ALWAYS, "MSI");

        ConclusionAlgo.generateMSIConclusion(conclusion,
                PurpleMicrosatelliteStatus.MSI,
                4.5,
                actionabilityMap,
                Sets.newHashSet(),
                Sets.newHashSet());

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- MSI (4.5) MSI");
    }

    @Test
    public void canGenerateMSSConclusion() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = create("MSI", TypeAlteration.POSITIVE, "MSI", Condition.ALWAYS, "MSI");

        ConclusionAlgo.generateMSIConclusion(conclusion,
                PurpleMicrosatelliteStatus.MSS,
                3.2,
                actionabilityMap,
                Sets.newHashSet(),
                Sets.newHashSet());

        assertEquals(0, conclusion.size());
    }

    @Test
    public void canGenerateTMBHighConclusion() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("High-TMB", TypeAlteration.POSITIVE, "High-TMB", Condition.ALWAYS, "TMB");

        ConclusionAlgo.generateTMBConclusion(conclusion, 17, actionabilityMap, Sets.newHashSet(), Sets.newHashSet());

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- TMB (17) TMB");
    }

    @Test
    public void canGenerateTMBLowConclusion() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("High-TMB", TypeAlteration.POSITIVE, "High-TMB", Condition.ALWAYS, "TMB");

        ConclusionAlgo.generateTMBConclusion(conclusion, 13, actionabilityMap, Sets.newHashSet(), Sets.newHashSet());
        assertEquals(0, conclusion.size());
    }

    @Test
    public void canGeneratePurityConclusionBelow() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("PURITY", TypeAlteration.PURITY, "PURITY", Condition.OTHER, "low purity (XX%)");

        ConclusionAlgo.generatePurityConclusion(conclusion, 0.16, true, actionabilityMap);

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- low purity (16%)\n");
    }

    @Test
    public void canGeneratePurityConclusionAbove() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("PURITY", TypeAlteration.PURITY, "PURITY", Condition.OTHER, "low purity (XX%)");

        ConclusionAlgo.generatePurityConclusion(conclusion, 0.3, true, actionabilityMap);
        assertEquals(0, conclusion.size());
    }

    @Test
    public void canGeneratePurityConclusionReliable() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("PURITY_UNRELIABLE", TypeAlteration.PURITY_UNRELIABLE, "PURITY_UNRELIABLE", Condition.OTHER, "unreliable");

        ConclusionAlgo.generatePurityConclusion(conclusion, 0.3, false, actionabilityMap);

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- unreliable\n");
    }

    @Test
    public void canGenerateTotalResultsOncogenic() {
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("NO_ONCOGENIC", TypeAlteration.NO_ONCOGENIC, "NO_ONCOGENIC", Condition.OTHER, "no_oncogenic");

        ConclusionAlgo.generateTotalResults(conclusion, actionabilityMap, Sets.newHashSet(), Sets.newHashSet());

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- no_oncogenic");
    }

    @Test
    public void canGenerateTotalResultsActionable() {
        Set<String> oncogenic = Sets.newHashSet();
        List<String> conclusion = Lists.newArrayList();
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap =
                create("NO_ACTIONABLE", TypeAlteration.NO_ACTIONABLE, "NO_ACTIONABLE", Condition.OTHER, "no_actionable");

        oncogenic.add("fusion");
        ConclusionAlgo.generateTotalResults(conclusion, actionabilityMap, oncogenic, Sets.newHashSet());

        assertEquals(1, conclusion.size());
        assertEquals(conclusion.get(0), "- no_actionable");
    }

    @NotNull
    private static Map<ActionabilityKey, ActionabilityEntry> create(@NotNull String gene, @NotNull TypeAlteration typeAlteration,
            @NotNull String match, @NotNull Condition condition, @NotNull String conclusion) {
        Map<ActionabilityKey, ActionabilityEntry> actionabilityMap = Maps.newHashMap();
        return append(actionabilityMap, gene, typeAlteration, match, condition, conclusion);
    }

    @NotNull
    private static Map<ActionabilityKey, ActionabilityEntry> append(@NotNull Map<ActionabilityKey, ActionabilityEntry> actionabilityMap,
            @NotNull String gene, @NotNull TypeAlteration typeAlteration, @NotNull String match, @NotNull Condition condition,
            @NotNull String conclusion) {
        ActionabilityKey key = ImmutableActionabilityKey.builder().match(gene).type(typeAlteration).build();
        ActionabilityEntry entry =
                ImmutableActionabilityEntry.builder().match(match).type(typeAlteration).condition(condition).conclusion(conclusion).build();
        actionabilityMap.put(key, entry);
        return actionabilityMap;
    }

    @NotNull
    private static List<ReportableVariant> createTestReportableVariants() {
        ReportableVariant variant1 = TestReportableVariantFactory.builder()
                .gene("APC")
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.Val600Arg")
                .canonicalHgvsCodingImpact("c.123A>C")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .driverLikelihood(0.9)
                .biallelic(true)
                .build();

        ReportableVariant variant2 = TestReportableVariantFactory.builder()
                .gene("BRCA2")
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.?")
                .canonicalHgvsCodingImpact("c.1235A>C")
                .canonicalCodingEffect(PurpleCodingEffect.SPLICE)
                .driverLikelihood(0.9)
                .biallelic(true)
                .build();

        ReportableVariant variant3 = TestReportableVariantFactory.builder()
                .gene("BRCA1")
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.Val602Arg")
                .canonicalHgvsCodingImpact("c.124A>C")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .biallelic(true)
                .driverLikelihood(0.82)
                .build();

        ReportableVariant variant4 = TestReportableVariantFactory.builder()
                .gene("BRCA1")
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.Val600Arg")
                .canonicalHgvsCodingImpact("c.123A>C")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .biallelic(true)
                .driverLikelihood(0.82)
                .build();

        ReportableVariant variant5 = TestReportableVariantFactory.builder()
                .gene("CHEK2")
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("")
                .canonicalHgvsCodingImpact("c.123A>C")
                .canonicalCodingEffect(PurpleCodingEffect.SPLICE)
                .biallelic(false)
                .driverLikelihood(0.85)
                .build();

        ReportableVariant variant6 = TestReportableVariantFactory.builder()
                .gene("BRCA1")
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.?")
                .canonicalHgvsCodingImpact("c.123A>C")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .biallelic(true)
                .driverLikelihood(0.82)
                .build();

        ReportableVariant variant7 = TestReportableVariantFactory.builder()
                .gene("TERT")
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact(Strings.EMPTY)
                .canonicalHgvsCodingImpact("c.-124C>T")
                .canonicalCodingEffect(PurpleCodingEffect.NONE)
                .canonicalEffect("upstream_gene")
                .biallelic(true)
                .driverLikelihood(0.82)
                .build();

        ReportableVariant variant8 = TestReportableVariantFactory.builder()
                .gene("CDKN2A")
                .isCanonical(true)
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.Arg58*")
                .canonicalHgvsCodingImpact("c.172C>T")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .biallelic(true)
                .driverLikelihood(0.82)
                .build();

        ReportableVariant variant9 = TestReportableVariantFactory.builder()
                .gene("CDKN2A")
                .isCanonical(false)
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.Arg58*")
                .canonicalHgvsCodingImpact("c.171C>T")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .otherImpactClinical(ImmutablePurpleTranscriptImpact.builder()
                        .hgvsCodingImpact("c.172C>T")
                        .hgvsProteinImpact("p.Arg59*")
                        .transcript("transcript2")
                        .spliceRegion(false)
                        .effects(Sets.newHashSet())
                        .codingEffect(PurpleCodingEffect.MISSENSE)
                        .build())
                .biallelic(true)
                .driverLikelihood(0.82)
                .build();

        ReportableVariant variant10 = TestReportableVariantFactory.builder()
                .gene("KRAS")
                .isCanonical(false)
                .canonicalTranscript("transcript1")
                .canonicalHgvsProteinImpact("p.Arg58*")
                .canonicalHgvsCodingImpact("c.17C>T")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .otherImpactClinical(ImmutablePurpleTranscriptImpact.builder()
                        .hgvsCodingImpact("c.1723C>T")
                        .hgvsProteinImpact("p.Arg59*")
                        .transcript("transcript2")
                        .spliceRegion(false)
                        .effects(Sets.newHashSet())
                        .codingEffect(PurpleCodingEffect.MISSENSE)
                        .build())
                .biallelic(false)
                .driverLikelihood(0.9)
                .build();

        return Lists.newArrayList(variant1, variant2, variant3, variant4, variant5, variant6, variant7, variant8, variant9, variant10);
    }

    @NotNull
    private static DriverGene createDriverGene(@NotNull String name, @NotNull DriverCategory likelihoodMethod) {
        return TestDriverGeneFactory.builder().gene(name).likelihoodType(likelihoodMethod).build();
    }

    @NotNull
    private static Set<PurpleGainLossData> createTestGainsLosses() {
        PurpleGainLossData gainLoss1 = TestPurpleFactory.gainLossBuilderOncoAct()
                .gene("BRAF")
                .interpretation(CopyNumberInterpretation.FULL_GAIN)
                .minCopies(4)
                .maxCopies(4)
                .build();
        PurpleGainLossData gainLoss2 = TestPurpleFactory.gainLossBuilderOncoAct()
                .gene("KRAS")
                .interpretation(CopyNumberInterpretation.PARTIAL_GAIN)
                .minCopies(3)
                .maxCopies(8)
                .build();
        PurpleGainLossData gainLoss3 = TestPurpleFactory.gainLossBuilderOncoAct()
                .gene("CDKN2A")
                .interpretation(CopyNumberInterpretation.FULL_LOSS)
                .minCopies(0)
                .maxCopies(0)
                .build();
        PurpleGainLossData gainLoss4 = TestPurpleFactory.gainLossBuilderOncoAct()
                .gene("EGFR")
                .interpretation(CopyNumberInterpretation.PARTIAL_LOSS)
                .minCopies(0)
                .maxCopies(3)
                .build();
        PurpleGainLossData gainLoss5 = TestPurpleFactory.gainLossBuilderOncoAct()
                .gene("CDKN2A")
                .interpretation(CopyNumberInterpretation.PARTIAL_LOSS)
                .minCopies(0)
                .maxCopies(3)
                .build();
        return Sets.newHashSet(gainLoss1, gainLoss2, gainLoss3, gainLoss4, gainLoss5);
    }

    @NotNull
    private static Set<AnnotatedVirus> createTestVirusInterpreterEntries() {
        Set<AnnotatedVirus> virusEntries = Sets.newHashSet();
        AnnotatedVirus virus1 = TestVirusInterpreterFactory.builder()
                .interpretation(VirusInterpretation.EBV)
                .virusDriverLikelihoodType(VirusLikelihoodType.LOW)
                .build();
        AnnotatedVirus virus2 = TestVirusInterpreterFactory.builder()
                .interpretation(VirusInterpretation.HPV)
                .virusDriverLikelihoodType(VirusLikelihoodType.HIGH)
                .build();
        AnnotatedVirus virus3 = TestVirusInterpreterFactory.builder()
                .interpretation(VirusInterpretation.MCV)
                .virusDriverLikelihoodType(VirusLikelihoodType.HIGH)
                .build();

        virusEntries.add(virus1);
        virusEntries.add(virus2);
        virusEntries.add(virus3);
        return virusEntries;
    }

    @NotNull
    public static LilacRecord createTestLilacRecord() {
        List<LilacAllele> alleles = Lists.newArrayList();
        alleles.add(TestLilacFactory.builder().allele("A*02:01").somaticMissense(2D).tumorCopyNumber(4.7).build());
        alleles.add(TestLilacFactory.builder().allele("A*03:01").somaticMissense(0D).tumorCopyNumber(1.5).build());
        alleles.add(TestLilacFactory.builder()
                .allele("B*18:02")
                .somaticSplice(1D)
                .tumorCopyNumber(1.2)
                .somaticNonsenseOrFrameshift(1D)
                .build());
        alleles.add(TestLilacFactory.builder().allele("B*35:02").tumorCopyNumber(1.1).build());
        alleles.add(TestLilacFactory.builder().allele("C*10:12").somaticSynonymous(1D).tumorCopyNumber(0).build());
        alleles.add(TestLilacFactory.builder().allele("C*16:02").tumorCopyNumber(0).somaticMissense(1D).build());

        return ImmutableLilacRecord.builder().qc("PASS").alleles(alleles).build();
    }

    @NotNull
    private static HomozygousDisruption createHomozygousDisruption(@NotNull String gene) {
        return TestLinxFactory.homozygousDisruptionBuilder().gene(gene).build();
    }

    @NotNull
    private static Set<LinxFusion> createTestFusions() {
        Set<LinxFusion> fusions = Sets.newHashSet();
        fusions.add(linxFusionBuilder("BRAF", "BRAF", true).reportedType(LinxFusionType.EXON_DEL_DUP).build());
        fusions.add(linxFusionBuilder("CAV2", "MET", true).reportedType(LinxFusionType.KNOWN_PAIR).build());
        fusions.add(linxFusionBuilder("EGFR", "EGFR", true).fusedExonUp(25)
                .fusedExonDown(14)
                .reportedType(LinxFusionType.EXON_DEL_DUP)
                .build());
        fusions.add(linxFusionBuilder("EGFR", "EGFR", true).fusedExonUp(26)
                .fusedExonDown(18)
                .reportedType(LinxFusionType.EXON_DEL_DUP)
                .build());
        fusions.add(linxFusionBuilder("EGFR", "EGFR", true).fusedExonUp(15)
                .fusedExonDown(23)
                .reportedType(LinxFusionType.EXON_DEL_DUP)
                .build());
        return fusions;
    }

    @NotNull
    private static ImmutableLinxFusion.Builder linxFusionBuilder(@NotNull String geneStart, @NotNull String geneEnd, boolean reported) {
        return TestLinxFactory.fusionBuilder().geneStart(geneStart).geneEnd(geneEnd).reported(reported);
    }
}