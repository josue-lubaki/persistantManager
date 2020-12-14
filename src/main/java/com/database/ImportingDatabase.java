package com.database;

import com.exception.CustomAccessException;
import java.sql.*;

public class ImportingDatabase {

    private Connection con = null;


    // Constructor
    public ImportingDatabase(){
        this.connect();
    }

    public Connection getConnexion() {
        return con;
    }

    public void connect() {
        try {
            String databaseURL = "jdbc:postgresql://localhost:5432/postgres";
            String databaseUserName = "postgres";
            String databaseUserPassword = "Heroes";
            con = DriverManager.getConnection(databaseURL, databaseUserName, databaseUserPassword);
            System.out.println("Connection completed");
        } catch (SQLException s) {
            System.out.println("Echec de la connexion : " + s.getMessage());
        }
    }

    public void close() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Déconnexion de la Base de données !");
            } catch (SQLException errorConnexion) {
                System.out.println("La Connexion n'a pu être établie");
                errorConnexion.printStackTrace();
            }
        }
    }

    public int ObtenirIndexSuivant(String nameSequence) throws CustomAccessException, SQLException {
        Statement statement = con.createStatement();
        statement.execute("SELECT nextval('" + nameSequence + "'::regclass)");
        ResultSet rs = statement.getResultSet();
        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
        throw new CustomAccessException("Impossible de récupérer l'index courant : " + nameSequence);
    }
}
