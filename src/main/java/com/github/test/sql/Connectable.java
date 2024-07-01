package com.github.test.sql;

import org.jetbrains.annotations.Contract;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public class Connectable {
    protected Connection connection;

    protected Connectable(Connection connection) {
        this.connection = connection;
    }

    public boolean execute(String s) {
        fetchConnection();
        try (Statement statement = this.connection.createStatement()) {
            boolean result = statement.execute(s);
            this.connection.commit();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeStatement(String s) {
        fetchConnection();
        try (Statement statement = this.connection.createStatement()) {
            return statement.executeQuery(s);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Optional<T> executeStatement(String s, ResultMapper<T> mapper) {
        fetchConnection();
        try (Statement statement = this.connection.createStatement()) {
            try (ResultSet set = statement.executeQuery(s)) {
                this.connection.commit();
                boolean next = set.next();
                if (!next) {
                    return Optional.empty();
                }
                return Optional.of(mapper.map(set));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> executeStatements(String s, Function<ResultSet, T> mapper) {
        fetchConnection();
        try (Statement statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            try (ResultSet set = statement.executeQuery(s)) {
                this.connection.commit();
                int count;
                if (!set.last()) {
                    set.close();
                    return List.of();
                } else {
                    count = set.getRow();
                    set.beforeFirst();
                }
                List<T> results = new ArrayList<>(count);
                while (set.next()) {
                    T result = mapper.apply(set);
                    results.add(result);
                }
                return results;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract("-> fail")
    protected void fetchConnection() {
        if (this.connection == null) {
            throw new NoSuchElementException("Cannot retrieve a connection.");
        }
    }
}
