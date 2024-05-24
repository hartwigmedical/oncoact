package com.hartwig.oncoact.protect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.util.CsvFileReader;
import com.hartwig.serve.datamodel.*;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public final class ProtectEvidenceFile {

    private static final String TREATMENT_APPROACH_DELIMITER = ",";

    private static final String FIELD_DELIMITER = "\t";
    private static final String SOURCES_DELIMITER = ";";
    private static final String SOURCES_ITEM_DELIMITER = "|";
    private static final String SOURCES_ITEM_URL_DELIMITER = ",";

    private ProtectEvidenceFile() {
    }

    public static void write(@NotNull String file, @NotNull List<ProtectEvidence> evidences) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        for (ProtectEvidence evidence : evidences) {
            lines.add(toLine(evidence));
        }
        Files.write(new File(file).toPath(), lines);
    }

    @NotNull
    public static List<ProtectEvidence> read(@NotNull String file) throws IOException {
        List<ProtectEvidence> evidence = Lists.newArrayList();
        List<String> lines = Files.readAllLines(new File(file).toPath());

        Map<String, Integer> fields = CsvFileReader.getHeadersToDelimiter(lines.get(0), FIELD_DELIMITER);
        for (String line : lines.subList(1, lines.size())) {
            evidence.add(fromLine(fields, line));
        }
        return evidence;
    }

    @NotNull
    private static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("gene")
                .add("transcript")
                .add("isCanonical")
                .add("event")
                .add("eventIsHighDriver")
                .add("germline")
                .add("reported")
                .add("studyNctId")
                .add("studyTitle")
                .add("studyAcronym")
                .add("studyGender")
                .add("countriesOfStudy")
                .add("treatment")
                .add("treatmentApproachesDrugClass")
                .add("treatmentApproachesTherapy")
                .add("onLabel")
                .add("level")
                .add("direction")
                .add("sources")
                .toString();
    }

    @NotNull
    private static String therapyName(@Nullable ClinicalTrial clinicalTrial, @Nullable Treatment treatment) {
        boolean isClinicalTrial = clinicalTrial != null;
        boolean isTreatment = treatment != null;

        if (isClinicalTrial && isTreatment) {
            throw new IllegalStateException("An actionable event cannot be both a treatment and clinical trial");
        }

        if (isTreatment) {
            return treatment.name();
        } else {
            assert clinicalTrial != null;
            return setToField(clinicalTrial.therapyNames());
        }
    }

    @NotNull
    private static String setToField(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(",");
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    @NotNull
    private static String toLine(@NotNull ProtectEvidence evidence) {
        return new StringJoiner(FIELD_DELIMITER).add(nullToEmpty(evidence.gene()))
                .add(nullToEmpty(evidence.transcript()))
                .add(nullToEmpty(evidence.isCanonical()))
                .add(evidence.event())
                .add(nullToEmpty(evidence.eventIsHighDriver()))
                .add(String.valueOf(evidence.germline()))
                .add(String.valueOf(evidence.reported()))
                .add(evidence.clinicalTrial() != null ? Objects.requireNonNull(evidence.clinicalTrial()).studyNctId() : null)
                .add(evidence.clinicalTrial() != null ? Objects.requireNonNull(evidence.clinicalTrial()).studyTitle() : null)
                .add(evidence.clinicalTrial() != null ? Objects.requireNonNull(evidence.clinicalTrial()).studyAcronym() : null)
                .add(evidence.clinicalTrial() != null ? Objects.requireNonNull(evidence.clinicalTrial()).gender() : null)
                .add(evidence.clinicalTrial() != null ? setToString(Objects.requireNonNull(evidence.clinicalTrial()).countriesOfStudy()) : null)
                .add(therapyName(evidence.clinicalTrial(), evidence.treatment()))
                .add(evidence.treatment() != null ? setToString(Objects.requireNonNull(evidence.treatment()).treatmentApproachesDrugClass()) : null)
                .add(evidence.treatment() != null ? setToString(Objects.requireNonNull(evidence.treatment()).treatmentApproachesTherapy()) : null)
                .add(String.valueOf(evidence.onLabel()))
                .add(evidence.level().toString())
                .add(evidence.direction().toString())
                .add(sourcesToString(evidence.sources()))
                .toString();
    }

    @NotNull
    private static ProtectEvidence fromLine(@NotNull Map<String, Integer> fields, @NotNull String line) {
        String[] values = line.split(FIELD_DELIMITER, -1);

        return ImmutableProtectEvidence.builder()
                .gene(emptyToNullString(values[fields.get("gene")]))
                .transcript(emptyToNullString(values[fields.get("transcript")]))
                .isCanonical(emptyToNullBoolean(values[fields.get("isCanonical")]))
                .event(values[fields.get("event")])
                .eventIsHighDriver(emptyToNullBoolean(values[fields.get("eventIsHighDriver")]))
                .germline(Boolean.parseBoolean(values[fields.get("germline")]))
                .reported(Boolean.parseBoolean(values[fields.get("reported")]))
                .clinicalTrial(ImmutableClinicalTrial.builder()
                        .studyNctId(values[fields.get("studyNctId")])
                        .studyTitle(values[fields.get("studyTitle")])
                        .studyAcronym(values[fields.get("studyAcronym")])
                        .gender(values[fields.get("studyGender")])
                        .countriesOfStudy(stringToSet(values[fields.get("countriesOfStudy")]))
                        .therapyNames(stringToSet(values[fields.get("treatment")]))
                        .build())
                .treatment(ImmutableTreatment.builder()
                        .name(values[fields.get("treatment")])
                        .treatmentApproachesDrugClass(fields.containsKey("treatmentApproachesDrugClass") ? stringToSet(
                                values[fields.get("treatmentApproachesDrugClass")]) : Sets.newHashSet())
                        .treatmentApproachesTherapy(fields.containsKey("treatmentApproachesTherapy")
                                ? stringToSet(values[fields.get("treatmentApproachesTherapy")])
                                : Sets.newHashSet())
                        .build())
                .onLabel(Boolean.parseBoolean(values[fields.get("onLabel")]))
                .level(EvidenceLevel.valueOf(values[fields.get("level")]))
                .direction(EvidenceDirection.valueOf(values[fields.get("direction")]))
                .sources(stringToSources(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    public static String setToString(@NotNull Set<String> treatmentApproaches) {
        StringJoiner joiner = new StringJoiner(TREATMENT_APPROACH_DELIMITER);
        for (String url : treatmentApproaches) {
            joiner.add(url);
        }
        return joiner.toString();
    }

    @NotNull
    private static Set<String> stringToSet(@NotNull String fieldValue) {
        return Sets.newHashSet(fieldValue.split(TREATMENT_APPROACH_DELIMITER));
    }

    @NotNull
    @VisibleForTesting
    static String sourcesToString(@NotNull Iterable<KnowledgebaseSource> sources) {
        StringJoiner main = new StringJoiner(SOURCES_DELIMITER);
        for (KnowledgebaseSource source : sources) {
            StringJoiner joiner = new StringJoiner(SOURCES_ITEM_DELIMITER);
            joiner.add(source.name().toString());
            joiner.add(source.sourceEvent());

            StringJoiner sourceUrls = new StringJoiner(SOURCES_ITEM_URL_DELIMITER);
            for (String url : source.sourceUrls()) {
                sourceUrls.add(url);
            }
            joiner.add(sourceUrls.toString());
            joiner.add(source.evidenceType().toString());
            joiner.add(nullToEmptyString(source.rangeRank()));

            StringJoiner evidenceUrls = new StringJoiner(SOURCES_ITEM_URL_DELIMITER);
            for (String url : source.evidenceUrls()) {
                evidenceUrls.add(url);
            }
            joiner.add(evidenceUrls.toString());

            main.add(joiner.toString());
        }

        return main.toString();
    }

    @NotNull
    @VisibleForTesting
    static Set<KnowledgebaseSource> stringToSources(@NotNull String sourcesString) {
        Set<KnowledgebaseSource> sources = Sets.newHashSet();
        for (String entry : sourcesString.split(SOURCES_DELIMITER)) {
            String[] items = entry.split("\\" + SOURCES_ITEM_DELIMITER, -1);

            if (items.length == 5) {
                sources.add(ImmutableKnowledgebaseSource.builder()
                        .name(Knowledgebase.lookupKnowledgebase(items[0]))
                        .sourceEvent(items[1])
                        .sourceUrls(urlsToSet(items[2]))
                        .evidenceType(EvidenceType.valueOf(items[3]))
                        .rangeRank(null)
                        .evidenceUrls(urlsToSet(items[4]))
                        .build());
            } else if (items.length == 6) {
                sources.add(ImmutableKnowledgebaseSource.builder()
                        .name(Knowledgebase.lookupKnowledgebase(items[0]))
                        .sourceEvent(items[1])
                        .sourceUrls(urlsToSet(items[2]))
                        .evidenceType(EvidenceType.valueOf(items[3]))
                        .rangeRank(NullToInteger(items[4]))
                        .evidenceUrls(urlsToSet(items[5]))
                        .build());
            }

        }
        return sources;
    }

    @NotNull
    private static Set<String> urlsToSet(@NotNull String urls) {
        if (urls.isEmpty()) {
            return Sets.newHashSet();
        }
        return Sets.newHashSet(urls.split(SOURCES_ITEM_URL_DELIMITER));
    }

    @NotNull
    private static String nullToEmpty(@Nullable Boolean booleanValue) {
        return booleanValue != null ? Boolean.toString(booleanValue) : Strings.EMPTY;
    }

    @Nullable
    private static Boolean emptyToNullBoolean(@NotNull String value) {
        return !value.isEmpty() ? Boolean.parseBoolean(value) : null;
    }

    @NotNull
    private static String nullToEmpty(@Nullable String string) {
        return string != null ? string : Strings.EMPTY;
    }

    @Nullable
    private static String emptyToNullString(@NotNull String value) {
        return !value.isEmpty() ? value : null;
    }

    @NotNull
    private static String nullToEmptyString(@Nullable Integer string) {
        return string != null ? String.valueOf(string) : Strings.EMPTY;
    }

    @Nullable
    private static Integer NullToInteger(@Nullable String value) {
        return value != null && !value.equals(Strings.EMPTY) ? Integer.valueOf(value) : null;
    }
}