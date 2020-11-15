import org.postgresql.util.PSQLException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner scan = new Scanner(System.in);
        ImportingDatabase uneConnexion = new ImportingDatabase();//on cree une instance nommee uneConnexion
        // Se connecter
        uneConnexion.connect();

        // Demande des Inputs
        System.out.print("Entrer la Requête SQL : ");
        String sql = scan.nextLine();

        // Instancier les Beans
        Etudiant etudiant = new Etudiant();
        Cours cours = new Cours();

        List records = uneConnexion.retrieveSet(etudiant.getClass(),sql);

        //System.out.println(etudiant);

        for(Object e : records){
            System.out.println("Objet : " + e);
        }

        // Se Deconnecter
        uneConnexion.close();
    }

}
