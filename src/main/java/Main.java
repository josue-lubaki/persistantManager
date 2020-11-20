import org.postgresql.util.PSQLException;

import java.sql.SQLException;
<<<<<<< HEAD
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
=======
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scan = new Scanner(System.in);
    private static Scanner chiffre = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("\t\t\tMENU PRINCIPALE\n\n Tapez le chiffre qui correspond à votre action :");
        int monChoix = 0;

        do{
            System.out.println("1. Obtenir les Données Venant de la Base des Données\n" +
                    "2. Entrer une nouvelle donnée dans la Base de Données\n" +
                    "3. Quitter");
            monChoix = chiffre.nextInt();
            if(monChoix == 1)
                ConsultationDonnees();
            else if (monChoix == 2)
                ConsultationDonnees(); // TODO : à modifier pour le remplacer par L'Insertion
            else if (monChoix == 3)
                continue;

            String decision = null;
            do{
                System.out.println("Voulez-vous exécuter une autre action (Y/N)");
                decision = scan.nextLine();
                if(decision.toLowerCase().equals("y"))
                    monChoix = 0;
            }while(decision.toLowerCase() != "y" && decision.toLowerCase() != "n");
        }while(monChoix != 1 && monChoix != 2 && monChoix == 0);
>>>>>>> master
    }

    /* Methode Affichant le Menu pour Permettre de Recueillir les Données */
    public static void ConsultationDonnees() {
        System.out.println("Patientez pendant que nous vous connectons à la Base de données...");
        // Se connecter à la Base de Donnée
        ImportingDatabase uneConnexion = new ImportingDatabase();//on cree une instance nommée uneConnexion
        uneConnexion.connect();
        List records;

        System.out.println("\t\t\tRECUPERATION DONNEES");
        int choixUser = 0;
        String decision = null;
        do {
            System.out.println("Tapez le chiffre qui correspond à votre action :");
            System.out.println(
                            "1. Consulter la liste de tous les Etudiants\n" +
                            "2. Voir les Coordonnées d'un Etudiant à partir de son ID étudiant\n" +
                            "3. Consulter la liste de tous les Cours disponibles\n" +
                            "4. Voir les informations d'un Cours à partir d'ID du cours\n" +
                            "5. Consulter la liste de tous les Inscriptions faites\n" +
                            "6. Voir les informations d'une inscription à partir de son ID");

            System.out.println("Votre choix : ");
            choixUser = chiffre.nextInt();
            switch (choixUser) {
                case 1:{
                    records = uneConnexion.retrieveSet(Etudiant.class, "SELECT * FROM etudiant");
                    System.out.println("Liste de tous les Etudiants :");
                    Etudiant.listeEtudiants.forEach(e -> System.out.println(e));
                    System.out.println(); //Aérer le Code
                    break;
                }
                case 2:{
                    System.out.println("Entrer l'ID de l'étudiant : ");
                    int idEtudiant = chiffre.nextInt();
                    String sql = "SELECT * FROM etudiant WHERE etudiantid = " + idEtudiant;
                    records = uneConnexion.retrieveSet(Etudiant.class, sql);
                    records.forEach(e-> System.out.println(e));
                    break;
                }
                case 3:{
                    records = uneConnexion.retrieveSet(Cours.class, "SELECT * FROM cours");
                    Cours.listeCours.forEach(e->System.out.println(e));
                    break;
                }
                case 4:{
                    System.out.println("Entrer l'ID du Cours : ");
                    int idCours = chiffre.nextInt();
                    String sql = "SELECT * FROM cours WHERE coursid = " + idCours;
                    records = uneConnexion.retrieveSet(Cours.class, sql);
                    records.forEach(e-> System.out.println(e));
                    break;
                }
                case 5:{
                    records = uneConnexion.retrieveSet(Inscription.class, "SELECT * FROM inscription");
                    Inscription.listeInscriptions.stream().forEach(e->System.out.println(e));
                    break;
                }
                case 6:{
                    System.out.println("Entrer l'ID de l'inscription : ");
                    int idInscription = chiffre.nextInt();
                    String sql = "SELECT * FROM cours WHERE coursid = " + idInscription;
                    records = uneConnexion.retrieveSet(Inscription.class, sql);
                    records.forEach(e-> System.out.println(e));
                    break;
                }
                default:
                    System.out.println("Veuillez entrer une commande Valide !");
            }
            do{
                System.out.println("Voulez-vous obtenir une autre donnée (Y/N)");
                decision = scan.nextLine();
                if(decision.toLowerCase().equals("y"))
                    choixUser = 0;
            }while(!decision.toLowerCase().equals("y") && !decision.toLowerCase().equals("n"));
        } while (choixUser != 1 && choixUser != 2 && choixUser != 3 && choixUser != 4 && choixUser != 5
                && choixUser != 6 && choixUser == 0);

    uneConnexion.close();
    }
}
