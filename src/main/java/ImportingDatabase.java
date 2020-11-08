import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ImportingDatabase {// cette classe permet de faire l'importation de la base de données
    final String databaseURL = "jdbc:postgresql://localhost:5432/postgres";
    final String databaseUserName ="postgres";
    final String databaseUserPassword ="Heroes";
    Connection con = null;

    public Connection connect(){
        try {
            con = DriverManager.getConnection(databaseURL, databaseUserName, databaseUserPassword);
            System.out.println("Connection completed");
        }catch(SQLException s){
            System.out.println("Echec de la connexion : " + s.getMessage());
        }
        return con;
    }

    // Methode pour arrêter la connexion
    public void close() throws SQLException {
        if(con != null) {
            con.close();
            System.out.println("Connection closed.");
        }
    }


}
