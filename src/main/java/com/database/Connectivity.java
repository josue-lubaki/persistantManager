package com.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connectivity implements ConnectivityChecking{
    private String host;
    private String driver;
    private String nameBase;
    private int port;
    private String user;
    private String password;

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.getConnectionURL(), user, password);
    }

    @Override
    public Connection resetConnexion() {
        this.driver = null;
        this.host = null;
        this.user = null;
        this.password = null;
        this.port = 0;
        this.nameBase = null;
        return null;
    }


    @Override
    public String getConnectionURL() {
        // Reproduire ce modèle : "jdbc:postgresql://localhost:5432/postgres";
        return "jdbc:" + driver + "://" + host + ":" + port + "/" + nameBase;
    }

    @Override
    public void setLoginConnection(String driver, String host, String user, String password, int port, String nameBase) {
        this.driver = driver;
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.nameBase = nameBase;
    }
}
