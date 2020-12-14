package com.database;

import com.exception.CustomAccessException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.sql.*;

public class ImportingDatabase {// cette classe permet de faire l'importation de la base de données

    private final ConnectivityChecking entity;
    private static ImportingDatabase instance;
    private PreparedStatement statement;
    private static Connection con;

    @Inject
    public ImportingDatabase(ConnectivityChecking entity) {
        this.entity = entity;
    }

    public static Connection getConnexion() {
        return con;
    }

    public static ImportingDatabase getInstance() {
        if (con == null) {
            Injector inject = Guice.createInjector(new ImportingDatabaseModule());
            ImportingDatabase.instance = inject.getInstance(ImportingDatabase.class);
            ImportingDatabase.instance.entity.setLoginConnection(
                    "postgresql",
                    "localhost",
                    "postgres",
                    "Heroes",
                    5432,
                    "postgres");
        }
        return instance;
    }

    public void closeMe() throws SQLException {
        if (!con.isClosed()) {
            con.close();
            con = null;
            this.statement = null;
        }
    }

    public static void disconnect(Connection connexion) throws SQLException {
        if (connexion != null) {
            con.close();
            con = null;
            instance.entity.resetConnexion();
        }
    }

    public static int ObtenirIndexSuivant(String nameSequence) throws CustomAccessException, SQLException {
        ResultSet rs = retrieve("SELECT nextval('" + nameSequence + "'::regclass)");
        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
        throw new CustomAccessException("Impossible de récupérer l'index courant : " + nameSequence);
    }


    private void PrepareStatementWithConnexion(String sql) throws SQLException {
        con = this.entity.getConnection();
        this.statement = con.prepareStatement(sql);
    }

    private void PrepareStatementWithoutConnexion(String sql) throws SQLException {
        this.statement = con.prepareStatement(sql);
    }

    private ResultSet ExecuteRequete() throws SQLException {
        this.statement.executeQuery();
        return this.statement.getResultSet();
    }

    private int ExecuteInsert(String sql) throws SQLException
    {
        VerifyConnexionAndCreateStatement(sql);
        return this.statement.executeUpdate();
        //return this.statement.getGeneratedKeys();
    }

    private ResultSet Execute(String sql) throws SQLException {
        VerifyConnexionAndCreateStatement(sql);

        // Contrôle d'Option
        ResultSet resultSet = this.ExecuteRequete();

        //this.closeMe();
        return resultSet;
    }

    private void VerifyConnexionAndCreateStatement(String sql) throws SQLException {
        // Vérifier si la Connexion est toujours maintenue (Si existe)
        if(con == null)
            this.PrepareStatementWithConnexion(sql);
        else
            this.PrepareStatementWithoutConnexion(sql);
    }

    // execute avec une manipulation de type SELECT
    public static ResultSet retrieve(String sql) throws SQLException {
        return ImportingDatabase.getInstance().Execute(sql);
    }

    // execute Pour Insertion, Update, delete
    public static void retrieveInsert(String sql) throws SQLException {
        ImportingDatabase.getInstance().ExecuteInsert(sql);
    }

}
