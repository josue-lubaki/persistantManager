import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        ImportingDatabase uneConnexion = new ImportingDatabase();//on cree une instance nommee uneConnexion
        // Se connecter
        uneConnexion.connect();


        System.out.println("Veuillez entrer une requete SQL: ");
        Scanner scanner = new Scanner(System.in);
        String reponseUtilisateur = scanner.nextLine();


        Etudiant unEtudiant = new Etudiant();
        List listeRecord = uneConnexion.retrieveSet(unEtudiant.getClass(), reponseUtilisateur);
        for(Object e:listeRecord){
            System.out.print(e);
        }

        // Se Deconnecter
        uneConnexion.close();
    }

}
