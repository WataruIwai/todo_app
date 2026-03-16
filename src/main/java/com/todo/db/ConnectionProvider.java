package com.todo.db;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection();
}
