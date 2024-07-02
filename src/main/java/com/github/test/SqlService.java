package com.github.test;

import com.github.test.util.FormattedLogger;
import org.bukkit.event.Listener;

import java.sql.*;
import java.util.List;

public abstract class SqlService implements Listener {
    protected final Connection connection;
    protected final FormattedLogger logger;
    protected final String tableName;

    protected SqlService(
            Connection connection,
            FormattedLogger logger,
            String tableName) {
        this.connection = connection;
        this.logger = logger;
        this.tableName = tableName;
        initializeSqlTable();
    }

    protected void initializeSqlTable() {
        List<String> queries = createTableQueries();
        try {
            DatabaseMetaData databaseMetaData = this.connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, this.tableName, new String[]{"TABLE"});
            try (resultSet) {
                if (!resultSet.next()) {
                    try (Statement statement = this.connection.createStatement()) {
                        for (String query : queries) {
                            statement.execute(query);
                        }
                        this.logger.info("Successfully create table '%s'", this.tableName);
                    }
                } else {
                    this.logger.info("found existing table '%s'", this.tableName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract List<String> createTableQueries();
}
