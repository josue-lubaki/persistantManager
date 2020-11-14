import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ImportingDatabase uneConnexion = new ImportingDatabase();//on cree une instance nommee uneConnexion
        // Se connecter
        uneConnexion.connect();

        // Se Deconnecter
        uneConnexion.close();
    }

}
