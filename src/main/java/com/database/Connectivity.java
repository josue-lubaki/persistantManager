package com.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connectivity implements ConnectivityChecking{
    private String host;
    private String driver;
    private String nameDataBase;
    private int port;
    private String user;
    private String password;

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.getConnectionURL(), user, password);
    }

    @Override
    public String getConnectionURL() {
        // Reproduire ce modèle : "jdbc:postgresql://localhost:5432/postgres";
        return "jdbc:" + driver + "://" + host + ":" + port + "/" + nameDataBase;
    }

    @Override
    public void setLoginConnection(String driver, String host, String user, String password, int port, String nameDataBase) {
        this.driver = driver;
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.nameDataBase = nameDataBase;
    }
}
