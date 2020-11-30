import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.lang.*;

public class Main {
    private static final Scanner scan = new Scanner(System.in);
    private static final Scanner chiffre = new Scanner(System.in);
    public static void main(String[] args) throws IllegalAccessException, SQLException {
        int monChoix;
        do{
            System.out.println("\t\t\tMENU PRINCIPALE\n\n Tapez le chiffre qui correspond à votre action :");
            System.out.println("1. Obtenir les Données Venant de la Base des Données\n" +
                    "2. Entrer une nouvelle donnée dans la Base de Données\n" +
                    "3. Quitter");
            monChoix = chiffre.nextInt();

            if(monChoix == 1)
                MenuConsultationDonnees();
            else if (monChoix == 2)
                MenuInsertUpdateData();
            else if (monChoix == 3) {
                System.exit(0);
            }

        }while(monChoix != 1 && monChoix != 2 && monChoix != 3);
    }

    /******************************************************************************************************
     *****    METHODE AFFICHANT LE MENU QUI PERMET DE RECUEILLIR LES DONNÉES PROVENANT DE LA BASE     *****
     *****************************************************************************************************/
    /* Methode Affichant le Menu pour Permettre de Recueillir les Données */
    private static void MenuConsultationDonnees() throws CustomAccessException {
        System.out.println("Patientez pendant que nous vous connectons à la Base de données...");
        ImportingDatabase uneConnexion = new ImportingDatabase();
        uneConnexion.connect();

        System.out.println("\t\t\tRECUPERATION DONNEES");
        int choixUser = 0;
        String decision;
        do {
            System.out.println("Tapez le chiffre qui correspond à votre action :");
            System.out.println(
                    "1. Consulter la liste de tous les Etudiants\n" +
                            "2. Voir les Coordonnées d'un Etudiant à partir de son ID étudiant\n" +
                            "3. Consulter la liste de tous les Cours disponibles\n" +
                            "4. Voir les informations d'un Cours à partir d'ID du cours\n" +
                            "5. Consulter la liste de tous les Inscriptions faites\n" +
                            "6. Voir les informations d'une inscription à partir de son ID");

                choixUser = Main.prompt("Votre choix");
                switch (choixUser) {
                    case 1:{
                        System.out.println("Liste de tous les Etudiants :");
                        uneConnexion.retrieveSet(Etudiant.class, "SELECT * FROM etudiant");
                        listing(Etudiant.listeEtudiants);
                        break;
                    }
                    case 2:{
                        int idEtudiant = Main.prompt("Entrer l'ID de l'étudiant");
                        uneConnexion.retrieveSet(Etudiant.class, "SELECT * FROM etudiant WHERE etudiantid = " + idEtudiant);
                        filtrageEtudiant(idEtudiant);
                        break;
                    }
                    case 3:{
                        System.out.println("Liste de tous les Cours :");
                        uneConnexion.retrieveSet(Cours.class, "SELECT * FROM cours");
                        listing(Cours.listeCours);
                        break;
                    }
                    case 4:{
                        int idCours = Main.prompt("Entrer l'ID du Cours");
                        listing(uneConnexion.retrieveSet(Cours.class, "SELECT * FROM cours WHERE coursid = " + idCours));
                        filtrageCours(idCours);
                        break;
                    }
                    case 5:{
                        System.out.println("Liste de tous les Inscriptions :");
                        uneConnexion.retrieveSet(Inscription.class, "SELECT * FROM inscription");
                        listing(Inscription.listeInscriptions);
                        break;
                    }
                    case 6:{
                        int idInscription = Main.prompt("Entrer l'ID de l'inscription");
                        listing(uneConnexion.retrieveSet(Inscription.class, "SELECT * FROM cours WHERE coursid = " + idInscription));
                        filtrageInscription(idInscription);
                        break;
                    }
                    default:
                        System.out.println("Veuillez entrer une commande Valide !");
                }
            boolean flag2 = true;
            boolean sortir2 = false;
            while(flag2){
                System.out.println("Voulez-vous obtenir une autre donnée (Y/N)");
                decision = scan.nextLine();
                if(decision.toLowerCase().equals("y")) {
                    flag2 = false;
                    choixUser = 0;
                }
                if(decision.toLowerCase().equals("n")){
                    flag2 =false;
                    sortir2 = true;
                }
            }
            if(sortir2){break;}
        } while (choixUser != 1 && choixUser != 2 && choixUser != 3 && choixUser != 4 && choixUser != 5
                && choixUser != 6 && choixUser == 0);
        uneConnexion.close();
    }

    /*****************************************************************************************************
    ***     METHODE AFFICHANT LE MENU QUI PERMET D'INSERER, METTRE À JOURS DANS LA BASE DE DONNÉES      **
    *****************************************************************************************************/
    private static void MenuInsertUpdateData() throws CustomAccessException, SQLException {
        System.out.println("\t\t\tINSERTION ET UPDATE DONNEES");
        int choixUser;
        String decision;
        do {
            System.out.println("Tapez le chiffre qui correspond à votre action :");
            System.out.println(
                    "1. Ajouter un Etudiant dans la Base de donnée\n" +
                            "2. Ajouter un Cours dans la Base de donnée\n" +
                            "3. Ajouter un Cours à un Etudiant à partir de l'ID de l'Etudiant et l'ID du cours\n" +
                            "4. Ajouter un Etudiant avec ses inscriptions de Cours dans la Base de donnée\n" +
                            "5. Inscrire Plusieurs Etudiants à un Cours à partir de l'ID de l'Etudiant\n" +
                            "6. Ajouter un Etudiant, Ajouter un Cours et l'inscrire à ce cours.");

            choixUser = Main.prompt("Votre choix");
            switch (choixUser) {
                case 1:
                    ajouterEtudiantBD();
                    break;
                case 2:
                    ajouterCoursBD();
                    break;
                case 3:
                    ajouterCoursEtudiant();
                    break;
                case 4:
                    ajouterEtudiantAvecInscription();
                    break;
                case 5:
                    inscrirePlusieursEtudiantsOneCours();
                    break;
                case 6:
                    AjouterEtudiantCoursInscription();
                    break;
                default:
                    System.out.println("Veuillez entrer une commande Valide !");
            }
            boolean flag3 = true;
            boolean sortir3 = false;
            while(flag3){
                System.out.println("Voulez-vous Exécuter une autre Opération (Y/N)");
                decision = scan.nextLine();
                if(decision.toLowerCase().equals("y")) {
                    flag3 = false;
                    choixUser = 0;
                }
                if(decision.toLowerCase().equals("n")){
                    flag3 =false;
                    sortir3 = true;
                }
            }
            if(sortir3){break;}
        } while (choixUser == 0);
//        uneConnexion.close();
    }

    /*********************************************************************************************
     ***********   LES DIFFERENTES METHODES UTILISÉES POUR EFFECTUER DES OPÉRATIONS   ************
     ***********    DANS LA BASE DE DONNÉES (CREATION, MISE À JOUR, INSERTION,...)    ************
     *********************************************************************************************/

    /** METHODE PERMETTANT DE CRÉER UNE INSTANCE ETUDIANT TOUT EN LUI DONNANT UN etudiantid
     *  @see EtudiantBuilder
     *  @return Etudiant */
    private static Etudiant creationEtudiant() throws CustomAccessException, SQLException {
        String firstName = Main.promptString("Entrer le Prenom de l'Etudiant");
        String lastName = Main.promptString("Entrer le Nom de l'Etudiant");
        int age = Main.prompt("Entrer l'age de l'Etudiant");
        return EtudiantBuilder.creation(firstName, lastName, age);
    }

    /** METHODE PERMETTANT L'INSERTION DE L'ETUDIANT DANS LA BASE DE DONNÉES */
    public static void ajouterEtudiantBD() throws CustomAccessException, SQLException {
        ImportingDatabase uneConnexion = new ImportingDatabase();
        uneConnexion.connect();
        uneConnexion.insert(creationEtudiant());
    }

    /** METHODE PERMETTANT DE CRÉER UNE INSTANCE COURS TOUT EN LUI DONNANT UN coursid
     *  @see CoursBuilder
     *  @return Cours */
    private static Cours creationCours() throws CustomAccessException, SQLException {
        String name = Main.promptString("Entrer le nom du Cours");
        String sigle = Main.promptString("Entrer le sigle pour " + name);
        String description = Main.promptString("Entrer la Description du Cours");
        return CoursBuilder.creation(name, sigle, description);
    }

    /** METHODE PERMETTANT L'INSERTION DU COURS DANS LA BASE DE DONNÉES */
    public static void ajouterCoursBD() throws CustomAccessException, SQLException {
        ImportingDatabase uneConnexion = new ImportingDatabase();
        uneConnexion.connect();
        uneConnexion.insert(creationCours());
    }

    /** METHODE PERMETTANT D'INSCRIRE UN ETUDIANT VIA etudiantid À UN COURS VIA coursid */
    public static void ajouterCoursEtudiant() throws CustomAccessException, SQLException {
        int etudiantid = Main.prompt("Entrer l'ID de l'étudiant");
        int coursid = Main.prompt("Enter the course id");
        Inscription inscription = InscriptionBuilder.creation(etudiantid, coursid);
        ImportingDatabase uneConnexion = new ImportingDatabase();
        uneConnexion.connect();
        uneConnexion.insert(inscription);
    }

    /** METHODE PERMETTANT D'INSÉRER UN ETUDIANT DANS LA BASE DE DONNÉES TOUT EN LUI INSCRIVANT DANS CERTAINS COURS */
    public static void ajouterEtudiantAvecInscription() throws CustomAccessException, SQLException {
        Etudiant etudiant = creationEtudiant();
        ImportingDatabase uneConnexion = new ImportingDatabase();
        uneConnexion.connect();
        List listeCours = uneConnexion.retrieveSet(Cours.class, "SELECT * FROM cours");
        int nombreCoursExistante = listeCours.size();
        int countInscription;

        /* VERIFIER QUE LA VALEUR ENTRÉE PAR LES USERS NE DEPASSENT PAS LE NOMBRE DE COURS DISPONIBLE */
        do{
            countInscription = Main.prompt("Combien de Cours pour l'étudiant " + etudiant.getFname() + " ?");
            if(countInscription > nombreCoursExistante)
                System.out.println("Vous ne Pouvez pas entrer un nombre plus élevé que le nombre des cours existants\n" +
                        "Nous Disposons actuellement de " + nombreCoursExistante + " Cours disponible.\n");
        }while(countInscription > nombreCoursExistante);

        for (int i=0 ; i < countInscription; i++){
            int coursid = Main.prompt("Entrer l'ID du Cours");
            etudiant.inscriptions.add(InscriptionBuilder.creation(etudiant.getEtudiantid(),coursid));
        }
        uneConnexion.insert(etudiant);
    }

    /** METHODE PERMETTANT LA CREATION D'UN NOUVEAU COURS TOUT EN INSCRIVANT UN CERTAINS NOMBRE D'ETUDIANT */
    public static void inscrirePlusieursEtudiantsOneCours() throws CustomAccessException, SQLException {
        Cours cours = creationCours();
        ImportingDatabase uneConnexion = new ImportingDatabase();
        uneConnexion.connect();
        int countEtudiant = Main.prompt("Entrer le nombre d'Etudiant à inscrire au cours " + cours.getNameCours());
        for (int i = 0 ; i < countEtudiant; i++){
            int etudiantid = Main.prompt("Entrer l'ID de l'Etudiant");
            cours.inscriptions.add(InscriptionBuilder.creation(etudiantid,cours.getCoursid()));
        }
        uneConnexion.insert(cours);
    }

    /** METHODE PERMETTANT LA CREATION D'UN ETUDIANT, D'UN COURS ET ASSIGNÉ À CET ETUDIANT LE NOUVEAU COURS */
    public static void AjouterEtudiantCoursInscription() throws CustomAccessException, SQLException {
        Etudiant etudiant = creationEtudiant();
        Cours cours = creationCours();
        ImportingDatabase uneConnexion = new ImportingDatabase();
        uneConnexion.connect();
        Inscription inscription = new InscriptionBuilder().build();
        inscription.setEtudiant(etudiant);
        inscription.setCours(cours);
        uneConnexion.insert(inscription);
    }

    /** METHODE PERMETTANT DE LISTER LES CONTENUES D'UNE LISTE */
    public static <T> void listing(List<T> liste){
        liste.forEach(e -> System.out.println(e));
    }

    /* filtrage Etudiant */
    public static void filtrageEtudiant(int id){
        for(Etudiant item : Etudiant.listeEtudiants){
            if(item.getEtudiantid() == id)
                System.out.println(item);
        }
    }

    /* filtrage Cours */
    public static void filtrageCours(int id){
        for(Cours item : Cours.listeCours){
            if(item.getCoursid() == id)
                System.out.println(item);
        }
    }

    /* filtrage Inscription */
    public static void filtrageInscription(int id){
        for(Inscription item : Inscription.listeInscriptions){
            if(item.getInscriptionid() == id)
                System.out.println(item);
        }
    }

    /*********************************************************************************************
    ************     METHODES QUI PERMETTENT À L'UTILISATEUR D'ENTRÉE LES DONNÉES     ************
    *********************************************************************************************/
    public static int prompt(String message)
    {
        System.out.print(message + " : ");
        return Main.chiffre.nextInt();
    }

    public static String promptString(String message)
    {
        System.out.println(message + " : ");
        return Main.scan.nextLine();
    }

}
