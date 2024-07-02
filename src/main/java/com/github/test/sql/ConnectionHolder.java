package com.github.test.sql;

import java.sql.Connection;

public interface ConnectionHolder {
    public abstract Connection getConnection();
}
