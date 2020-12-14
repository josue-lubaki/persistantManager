package com.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectivityChecking {
    Connection getConnection() throws SQLException;
    String getConnectionURL();
    Connection resetConnexion() throws SQLException;
    void setLoginConnection(String driver, String host, String user, String password, int port, String nameBase);
}
