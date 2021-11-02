package com;

import com.bean.*;
import com.database.ImportingDatabase;
import com.persistance.PersistantManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.lang.*;

import static com.GestionTransaction.afficherlisteEtudiantCours;
import static com.GestionTransaction.listeCoursEtudiant;

public class Main {
    private static final Scanner scan = new Scanner(System.in);
    private static final Scanner chiffre = new Scanner(System.in);

    public static void main(String[] args) {
        int monChoix;
        do {
            System.out.println("******************************************************************");
            System.out.println("\t\t\t╔═════════════════════╗");
            System.out.println("\t\t\t║   MENU PRINCIPALE   ║");
            System.out.println("\t\t\t╚═════════════════════╝");
            System.out.println(" Tapez le chiffre qui correspond à votre action : \n" +
                    "1. Obtenir les Données Venant de la Base des Données\n" +
                    "2. Entrer une nouvelle donnée dans la Base de Données\n" +
                    "3. Quitter");
            monChoix = chiffre.nextInt();
            try {
                if (monChoix == 1)
                    MenuConsultationDonnees();
                else if (monChoix == 2)
                    MenuInsertUpdateData();
                else if (monChoix == 3) {
                    chiffre.close();
                    scan.close();
                    copyright();
                    System.exit(0);
                }
            } catch (SQLException throwables) {
                System.out.println("SQL error : " + throwables.getMessage());
                throwables.printStackTrace();
            } catch (IllegalAccessException e) {
                System.out.println("Erreur d'accès sur le Champ : " + e.getMessage());
                e.printStackTrace();
            } catch (InstantiationException e) {
                System.out.println("Erreur d'instanciation de la Classe " + e.getMessage());
                e.printStackTrace();
            }

        } while (true);
    }

    /******************************************************************************************************
     *****    METHODE AFFICHANT LE MENU QUI PERMET DE RECUEILLIR LES DONNÉES PROVENANT DE LA BASE     *****
     *****************************************************************************************************/
    private static void MenuConsultationDonnees() throws SQLException, IllegalAccessException, InstantiationException {
        System.out.println("******************************************************************");
        System.out.println("\t\t\t╔══════════════════════════╗");
        System.out.println("\t\t\t║   RECUPERATION DONNEES   ║");
        System.out.println("\t\t\t╚══════════════════════════╝");
        int choixUser;
        String decision;
        do {
            System.out.println("Tapez le chiffre qui correspond à votre action :");
            System.out.println(
                    "1. Consulter la liste de tous les Etudiants\n" +
                            "2. Voir les Coordonnées d'un Etudiant à partir de son ID étudiant\n" +
                            "3. Consulter la liste de tous les Cours disponibles\n" +
                            "4. Voir les informations d'un Cours à partir d'ID du cours\n" +
                            "5. Consulter la liste de tous les Inscriptions faites\n" +
                            "6. Voir les informations d'une inscription à partir de son ID\n" +
                            "7. Consulter la liste des cours d'un étudiant à partir de son ID\n" +
                            "8. Consulter la liste des étudiants dans un cours selon ID du Cours\n" +
                            "0. Revenir au Menu Principal");

            choixUser = prompt("Votre choix");
            switch (choixUser) {
                case 1: {
                    System.out.println("******************************************************************");
                    System.out.println("\t\tListe de tous les Etudiants\n\t\t---------------------------");
                    System.out.println("Recupération de tous les étudiants en cours d'exécution...");
                    listing(Objects.requireNonNull(PersistantManager
                            .retrieveSet(Etudiant.class, ImportingDatabase.GET_ALL_STUDENT)));
                    System.out.println("******************************************************************");
                    break;
                }
                case 2: {
                    System.out.println("******************************************************************");
                    int idEtudiant = prompt("Entrer l'ID de l'étudiant");
                    listing(Objects.requireNonNull(PersistantManager
                            .retrieveSet(Etudiant.class, ImportingDatabase.GET_SPECIFIC_STUDENT(idEtudiant))));
                    System.out.println("******************************************************************");
                    break;
                }
                case 3: {
                    System.out.println("******************************************************************");
                    System.out.println("\t\tListe de tous les Cours\n\t\t-----------------------");
                    listing(Objects.requireNonNull(PersistantManager
                            .retrieveSet(Cours.class, ImportingDatabase.GET_ALL_COURSE)));
                    System.out.println("******************************************************************");
                    break;
                }
                case 4: {
                    System.out.println("******************************************************************");
                    int idCours = prompt("Entrer l'ID du Cours");
                    listing(Objects.requireNonNull(PersistantManager
                            .retrieveSet(Cours.class, ImportingDatabase.GET_SPECIFIC_COURSE(idCours))));
                    System.out.println("******************************************************************");
                    break;
                }
                case 5: {
                    System.out.println("******************************************************************");
                    System.out.println("\t\tListe de tous les Inscriptions\n\t\t------------------------------");
                    System.out.println("Recupération de la liste de tous les inscriptions en cours d'exécution...");
                    listing(Objects.requireNonNull(PersistantManager
                            .retrieveSet(Inscription.class, ImportingDatabase.GET_ALL_SUBSCRIBE)));
                    System.out.println("******************************************************************");
                    break;
                }
                case 6: {
                    System.out.println("******************************************************************");
                    int idInscription = prompt("Entrer l'ID de l'inscription");
                    listing(Objects.requireNonNull(PersistantManager
                            .retrieveSet(Inscription.class, ImportingDatabase.GET_SPECIFIC_SUBSCRIBE(idInscription))));
                    System.out.println("******************************************************************");
                    break;
                }
                case 7:
                    System.out.println("******************************************************************");
                    listeCoursEtudiant();
                    System.out.println("******************************************************************");
                    break;
                case 8:
                    System.out.println("******************************************************************");
                    afficherlisteEtudiantCours();
                    System.out.println("******************************************************************");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Veuillez entrer une commande Valide !");
                    break;
            }

            do {
                if (choixUser == 0)
                    break;

                decision = promptString("Voulez-vous obtenir une autre donnée (Y/N)").toLowerCase();
                if (decision.equals("y")) {
                    choixUser = -1;
                }
            } while (!decision.equals("y") && !decision.equals("n"));
            ImportingDatabase.disconnect(ImportingDatabase.getConnexion());
        } while (choixUser != 1 && choixUser != 2 && choixUser != 3 && choixUser != 4 && choixUser != 5
                && choixUser != 6 && choixUser != 0);
    }

    /*****************************************************************************************************
     ***     METHODE AFFICHANT LE MENU QUI PERMET D'INSERER, METTRE À JOURS DANS LA BASE DE DONNÉES      **
     *****************************************************************************************************/
    private static void MenuInsertUpdateData() throws IllegalAccessException, SQLException, InstantiationException {
        System.out.println("******************************************************************");
        System.out.println("\t\t\t╔═════════════════════════════════╗");
        System.out.println("\t\t\t║   INSERTION ET UPDATE DONNEES   ║");
        System.out.println("\t\t\t╚═════════════════════════════════╝");
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
                            "6. Ajouter un Etudiant, Ajouter un Cours et l'inscrire à ce cours.\n" +
                            "0. Revenir au menu Principal");

            choixUser = prompt("Votre choix");
            switch (choixUser) {
                case 1:
                    System.out.println("******************************************************************");
                    GestionTransaction.ajouterEtudiantBD();
                    System.out.println("******************************************************************");
                    break;
                case 2:
                    System.out.println("******************************************************************");
                    GestionTransaction.ajouterCoursBD();
                    break;
                case 3:
                    System.out.println("******************************************************************");
                    GestionTransaction.ajouterCoursEtudiant();
                    System.out.println("******************************************************************");
                    break;
                case 4:
                    System.out.println("******************************************************************");
                    GestionTransaction.ajouterEtudiantAvecInscription();
                    System.out.println("******************************************************************");
                    break;
                case 5:
                    System.out.println("******************************************************************");
                    GestionTransaction.inscrirePlusieursEtudiantsOneCours();
                    System.out.println("******************************************************************");
                    break;
                case 6:
                    System.out.println("******************************************************************");
                    GestionTransaction.AjouterEtudiantCoursInscription();
                    System.out.println("******************************************************************");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Veuillez entrer une commande Valide !");
                    break;
            }

            do {
                if (choixUser == 0)
                    break;
                decision = promptString("Voulez-vous Exécuter une autre Opération (Y/N)").toLowerCase();
                if (decision.equals("y")) {
                    choixUser = -1;
                }
            } while (!decision.equals("y") && !decision.equals("n"));
            ImportingDatabase.disconnect(ImportingDatabase.getConnexion());
        } while (choixUser != 1 && choixUser != 2 && choixUser != 3 && choixUser != 4 && choixUser != 5
                && choixUser != 6 && choixUser != 0);
    }


    /**
     * METHODE PERMETTANT DE LISTER LES CONTENUES D'UNE LISTE
     */
    public static <T> void listing(List<T> maListe) {
        System.out.println("Voici le resultat : ");
        maListe.forEach(System.out::println);
    }


    /*********************************************************************************************
     ************     METHODES QUI PERMETTENT À L'UTILISATEUR D'ENTRÉE LES DONNÉES     ************
     *********************************************************************************************/
    public static int prompt(String message) {
        System.out.print(message + " : ");
        return Main.chiffre.nextInt();
    }

    public static String promptString(String message) {
        System.out.println(message + " : ");
        return Main.scan.nextLine();
    }

    /* Methode Quitter */
    public static void copyright(){
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   Copyright 2020 - Toute Reproduction Interdite ©     ║");
        System.out.println("║ @Authors : Josue Lubaki & Ismael Coulibaly & Xuyao Hu ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }
}
