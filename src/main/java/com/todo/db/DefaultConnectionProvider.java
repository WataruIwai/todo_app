package com.todo.db;

import java.sql.Connection;

public class DefaultConnectionProvider implements ConnectionProvider {
    @Override
    public Connection getConnection() {
        return DbConnection.getConnection();
    }
}
