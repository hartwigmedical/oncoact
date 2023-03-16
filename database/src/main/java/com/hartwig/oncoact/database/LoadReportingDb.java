package com.hartwig.oncoact.database;

import com.hartwig.oncoact.database.dao.DatabaseAccess;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidenceFile;
import com.hartwig.oncoact.reporting.AnalysedPatientReport;
import com.hartwig.oncoact.reporting.ReportingJson;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.hartwig.oncoact.database.dao.DatabaseAccess.addDatabaseCmdLineArgs;
import static com.hartwig.oncoact.database.dao.DatabaseAccess.databaseAccess;

public class LoadReportingDb {

    private static final Logger LOGGER = LogManager.getLogger(LoadReportingDb.class);

    private static final String REPORTING_JSON = "reporting_json";


    public static void main(@NotNull String[] args) throws ParseException, SQLException, IOException {
        Options options = createOptions();
        CommandLine cmd = new DefaultParser().parse(options, args);

        String reportingJson = cmd.getOptionValue(REPORTING_JSON);

        if (Utils.anyNull(reportingJson) || !new File(reportingJson).exists()) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Load Reporting data into DB", options);
            System.exit(1);
        }

        DatabaseAccess dbWriter = databaseAccess(cmd);
        LOGGER.info("Reading reporting json data from {}", reportingJson);
        AnalysedPatientReport analysedPatientReport = ReportingJson.read(reportingJson);
        dbWriter.writeReporting(analysedPatientReport);
        LOGGER.info("Done writing reporting data to database");
    }

    @NotNull
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(REPORTING_JSON, true, "Path towards the json reporting data JSON.");
        addDatabaseCmdLineArgs(options);
        return options;
    }
}