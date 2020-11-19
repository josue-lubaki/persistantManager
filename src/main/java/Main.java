import org.postgresql.util.PSQLException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        // Instancier les Beans
        //Etudiant etudiant = new Etudiant();
        //Cours cours = new Cours();

        System.out.println("\t\t\tMENU PRINCIPALE\n\n Tapez le chiffre qui correspond à votre action :");
        System.out.print("1. Obtenir les Données Venant de la Base des Données" +
                "2. Entrer une nouvelle donnée dans la Base de Données");


        String choixUser = scan.nextLine();
        do{
            if(choixUser == "1")
                ConsultationDonnees();
            else if(choixUser == "2")
                ConsultationDonnees();
            else
                choixUser = null;

            String decision = null;
            do{
                System.out.println("Voulez-vous exécuter une autre action (Y/N)");
                if(decision.toLowerCase() == "y")
                    choixUser = null;
            }while(decision.toLowerCase() != "y" || decision.toLowerCase() != "n");
        }while(choixUser != "1" || choixUser != "2" || choixUser == null);

    }

    /* Methode Affichant le Menu pour Permettre de Recueillir les Données */
    public static void ConsultationDonnees() {
        // Se connecter à la Base de Donnée
        ImportingDatabase uneConnexion = new ImportingDatabase();//on cree une instance nommée uneConnexion
        uneConnexion.connect();
        List records;

        System.out.println("\t\t\tRECUPERATION DONNEES");
        String choixUser = null;
        do {
            System.out.println("le chiffre qui correspond à votre action :");
            System.out.println(
                            "1. Consulter la liste de tous les Etudiants\n" +
                            "2. Voir les Coordonnées d'un Etudiant à partir de son ID étudiant\n" +
                            "3. Consulter la liste de tous les Cours disponibles\n" +
                            "4. Voir les informations d'un Cours à partir d'ID du cours\n" +
                            "5. Consulter la liste de tous les Inscriptions faites" +
                            "6. Voir les informations d'une inscription à partir de son ID");

            System.out.print("Votre choix : ");
            choixUser = scan.nextLine();
            switch (choixUser) {
                case "1":{
                    records = uneConnexion.retrieveSet(Etudiant.class, "SELECT * FROM etudiant");
                    System.out.println("Liste de tous les Etudiants :");
                    Etudiant.listeEtudiants.forEach(e -> System.out.println(e));
                    System.out.println(); //Aérer le Code
                }
                case "2":{
                    System.out.print("Entrer l'ID de l'étudiant : ");
                    String idEtudiant = scan.nextLine();
                    String sql = "SELECT * FROM etudiant WHERE etudiantid = " + idEtudiant;
                    records = uneConnexion.retrieveSet(Etudiant.class, sql);
                    records.forEach(e-> System.out.println(e));
                }
                case "3":{
                    records = uneConnexion.retrieveSet(Cours.class, "SELECT * FROM cours");
                    Cours.listeCours.forEach(e->System.out.println(e));
                }
                case "4":{
                    System.out.print("Entrer l'ID du Cours : ");
                    String idCours = scan.nextLine();
                    String sql = "SELECT * FROM cours WHERE coursid = " + idCours;
                    records = uneConnexion.retrieveSet(Cours.class, sql);
                    records.forEach(e-> System.out.println(e));
                }
                case "5":{
                    records = uneConnexion.retrieveSet(Inscription.class, "SELECT * FROM inscription");
                    Inscription.listeInscriptions.forEach(e->System.out.println(e));
                }
                case "6":{
                    System.out.print("Entrer l'ID de l'inscription : ");
                    String idInscription = scan.nextLine();
                    String sql = "SELECT * FROM cours WHERE coursid = " + idInscription;
                    records = uneConnexion.retrieveSet(Inscription.class, sql);
                    records.forEach(e-> System.out.println(e));
                }
                String decision = null;
                do{
                    System.out.println("Voulez-vous obtenir une autre donnée (Y/N)");
                    if(decision.toLowerCase() == "y")
                        choixUser = null;
                }while(decision.toLowerCase() != "y" || decision.toLowerCase() != "n");

            }
        } while (choixUser != "1" || choixUser != "2" || choixUser != "3" || choixUser != "4" || choixUser != "5"
                || choixUser != "6" || choixUser == null);
    }
}
