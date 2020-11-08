import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ImportingDatabase {// cette classe permet de faire l'importation de la base de données

    private String databaseURL = "jdbc:postgresql://localhost:5432/postgres";
    private String databaseUserName ="postgres";
    private String databaseUserPassword ="tmtc";

    public Connection connect(){

        Connection con = null;
        try {
            con = DriverManager.getConnection(databaseURL, databaseUserName, databaseUserPassword);
            System.out.println("Connection completed");

        }

        catch(SQLException s){

            System.out.println("Echec de la connexion : " + s.getMessage());
        }
        return con;
    }


}
