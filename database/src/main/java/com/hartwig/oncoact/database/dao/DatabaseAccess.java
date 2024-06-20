package com.hartwig.oncoact.database.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.hartwig.oncoact.protect.ProtectEvidence;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class DatabaseAccess implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseAccess.class);

    private static final String DEV_CATALOG = "oncoact_test";

    public static final String DB_USER = "db_user";
    public static final String DB_PASS_ENV_VARIABLE = "db_pass_env_variable";
    public static final String DB_URL = "db_url";

    public static final String DB_DEFAULT_ARGS = "?serverTimezone=UTC&useSSL=false";

    @NotNull
    private final Connection connection;
    @NotNull
    private final ProtectDAO protectDAO;

    public DatabaseAccess(@NotNull final String userName, @NotNull final String passwordEnvVariable, @NotNull final String url)
            throws SQLException {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
        String password = System.getenv().get(passwordEnvVariable);

        this.connection = DriverManager.getConnection(url, userName, password);
        String catalog = connection.getCatalog();
        LOGGER.debug("Connecting to database '{}'", catalog);
        DSLContext context = DSL.using(connection, SQLDialect.MYSQL, settings(catalog));

        this.protectDAO = new ProtectDAO(context);
    }

    public static void addDatabaseCmdLineArgs(@NotNull Options options, boolean isRequired) {
        options.addOption(Option.builder(DB_USER).desc("Database username").hasArg(true).required(isRequired).build());
        options.addOption(Option.builder(DB_PASS_ENV_VARIABLE).desc("Database password").hasArg(true).required(isRequired).build());
        options.addOption(Option.builder(DB_URL).desc("Database url").hasArg(true).required(isRequired).build());
    }

    @NotNull
    public static DatabaseAccess databaseAccess(@NotNull CommandLine cmd) throws SQLException {
        return databaseAccess(cmd, false);
    }

    @NotNull
    public static DatabaseAccess databaseAccess(@NotNull CommandLine cmd, boolean applyDefaultArgs) throws SQLException {
        String userName = cmd.getOptionValue(DB_USER);
        String passwordEnvVariable = cmd.getOptionValue(DB_PASS_ENV_VARIABLE);
        String databaseUrl = cmd.getOptionValue(DB_URL);
        String jdbcUrl = "jdbc:" + databaseUrl;

        if (applyDefaultArgs && !jdbcUrl.contains("serverTimezone") && !jdbcUrl.contains("useSSL")) {
            jdbcUrl += DB_DEFAULT_ARGS;
        }

        return new DatabaseAccess(userName, passwordEnvVariable, jdbcUrl);
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("DB connection close failed: {}", e.toString());
        }
    }

    @Nullable
    private static Settings settings(@NotNull String catalog) {
        if (catalog.equals(DEV_CATALOG)) {
            return null;
        }

        return new Settings().withRenderMapping(new RenderMapping().withSchemata(new MappedSchema().withInput(DEV_CATALOG)
                .withOutput(catalog))).withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED);
    }

    public void writeProtectEvidence(@NotNull String sample, @NotNull List<ProtectEvidence> evidence) {
        protectDAO.write(sample, evidence);
    }
}

