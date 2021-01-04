package com.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectivityChecking {
    Connection getConnection() throws SQLException;
    String getConnectionURL();
    void setLoginConnection(String driver, String host, String user, String password, int port, String nameDataBase);
}
