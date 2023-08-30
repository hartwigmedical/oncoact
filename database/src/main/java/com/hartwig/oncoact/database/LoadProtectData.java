package com.hartwig.oncoact.database;

import static com.hartwig.oncoact.database.dao.DatabaseAccess.addDatabaseCmdLineArgs;
import static com.hartwig.oncoact.database.dao.DatabaseAccess.databaseAccess;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.hartwig.oncoact.database.dao.DatabaseAccess;
import com.hartwig.oncoact.parser.CliAndPropertyParser;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidenceFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class LoadProtectData {

    private static final Logger LOGGER = LogManager.getLogger(LoadProtectData.class);

    private static final String SAMPLE = "sample";

    private static final String PROTECT_EVIDENCE_TSV = "protect_evidence_tsv";

    public static void main(@NotNull String[] args) throws ParseException, SQLException, IOException {
        Options options = createOptions();
        CommandLine cmd = new CliAndPropertyParser().parse(options, args);

        String evidenceTsv = cmd.getOptionValue(PROTECT_EVIDENCE_TSV);
        String sample = cmd.getOptionValue(SAMPLE);

        if (Utils.anyNull(evidenceTsv, sample) || !new File(evidenceTsv).exists()) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Patient-DB - Load PROTECT Data", options);
            System.exit(1);
        }

        try (DatabaseAccess dbWriter = databaseAccess(cmd)) {
            LOGGER.info("Reading PROTECT data for {} from {}", sample, evidenceTsv);
            List<ProtectEvidence> evidences = ProtectEvidenceFile.read(evidenceTsv);
            dbWriter.writeProtectEvidence(sample, evidences);
            LOGGER.info("Done writing {} PROTECT evidence items to database for {}", evidences.size(), sample);
        }
    }

    @NotNull
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(SAMPLE, true, "The tumor sample.");
        options.addOption(PROTECT_EVIDENCE_TSV, true, "Path towards the protect evidence tsv.");
        addDatabaseCmdLineArgs(options, false);
        return options;
    }
}
