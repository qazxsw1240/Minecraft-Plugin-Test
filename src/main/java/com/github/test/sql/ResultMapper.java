package com.github.test.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultMapper<T> {
    public abstract T map(ResultSet set) throws SQLException;
}
