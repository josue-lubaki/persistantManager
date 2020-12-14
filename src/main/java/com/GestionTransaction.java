package com;


import com.bean.*;
import com.exception.CustomAccessException;
import com.persistance.PersistantManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionTransaction {

// *******************************************************************************************
// ***********   LES DIFFERENTES METHODES UTILISÉES POUR EFFECTUER DES OPÉRATIONS   ************
// ***********    DANS LA BASE DE DONNÉES (CREATION, MISE À JOUR, INSERTION,...)    ************
// *********************************************************************************************/

    /**
     * METHODE PERMETTANT DE CRÉER UNE INSTANCE ETUDIANT TOUT EN LUI DONNANT UN etudiantid
     *
     * @return .Etudiant
     * @see EtudiantBuilder
     */
    private static Etudiant creationEtudiant() throws CustomAccessException, SQLException {
        String firstName = Main.promptString("Entrer le Prenom de l'Etudiant");
        String lastName = Main.promptString("Entrer le Nom de l'Etudiant");
        int age = Main.prompt("Entrer l'age de l'Etudiant");
        return EtudiantBuilder.creation(firstName, lastName, age);
    }

    /**
     * METHODE PERMETTANT L'INSERTION DE L'ETUDIANT DANS LA BASE DE DONNÉES
     */
    public static void ajouterEtudiantBD() throws CustomAccessException, SQLException {
        System.out.println(PersistantManager.insert(creationEtudiant()) + " ligne inserée");
    }

    /**
     * METHODE PERMETTANT DE CRÉER UNE INSTANCE COURS TOUT EN LUI DONNANT UN coursid
     *
     * @return .Cours
     * @see CoursBuilder
     */
    private static Cours creationCours() throws CustomAccessException, SQLException {
        String name = Main.promptString("Entrer le nom du .Cours");
        String sigle = Main.promptString("Entrer le sigle pour " + name);
        String description = Main.promptString("Entrer la Description du .Cours");
        return CoursBuilder.creation(name, sigle, description);
    }

    /**
     * METHODE PERMETTANT L'INSERTION DU COURS DANS LA BASE DE DONNÉES
     */
    public static void ajouterCoursBD() throws CustomAccessException, SQLException {
        System.out.println(PersistantManager.insert(creationCours()) + " ligne inserée");
    }

    /**
     * METHODE PERMETTANT D'INSCRIRE UN ETUDIANT VIA etudiantid À UN COURS VIA coursid
     */
    public static void ajouterCoursEtudiant() throws CustomAccessException, SQLException {
        int etudiantid = Main.prompt("Entrer l'ID de l'étudiant");
        int coursid = Main.prompt("Enter the course id");
        int nbreLigneInsere = PersistantManager.insert(InscriptionBuilder.creation(etudiantid, coursid));
        System.out.println(nbreLigneInsere + " ligne inserée");
    }

    /**
     * METHODE PERMETTANT D'INSÉRER UN ETUDIANT DANS LA BASE DE DONNÉES TOUT EN LUI INSCRIVANT DANS CERTAINS COURS
     */
    public static void ajouterEtudiantAvecInscription() throws IllegalAccessException, SQLException, InstantiationException {
        Etudiant etudiant = creationEtudiant();
        List<Cours> listeCours = PersistantManager.retrieveSet(Cours.class, "SELECT * FROM cours");
        int nombreCoursExistante = listeCours.size();
        int countInscription;
        int counter = 0;
        ArrayList<Integer> listeChoixFait = new ArrayList<>();

        /* VERIFIER QUE LA VALEUR ENTRÉE PAR LES USERS NE DEPASSENT PAS LE NOMBRE DE COURS DISPONIBLE */
        do {
            countInscription = Main.prompt("Combien de Cours pour l'étudiant " + etudiant.getFname() + " ?");
            if (countInscription > nombreCoursExistante)
                System.out.println("Vous ne Pouvez pas entrer un nombre plus élevé que le nombre des cours existants\n" +
                        "Nous Disposons actuellement de " + nombreCoursExistante + " .Cours disponible.\n");
        } while (countInscription > nombreCoursExistante);

        /* VERIFIER QUE L'UTILISATEUR N'AFFECTE PAS DEUX FOIS LE MEME COURS À L'ETUDIANT */
        do {
            int coursid = Main.prompt((counter+1) + ". Entrer l'ID du .Cours");
            if (!listeChoixFait.contains(coursid) && Cours.isContainedCourse(listeCours,coursid)) {
                etudiant.inscriptions.add(InscriptionBuilder.creation(etudiant.getEtudiantid(), coursid));
                listeChoixFait.add(coursid);
                counter++;
            }
            else if(!Cours.isContainedCourse(listeCours,coursid)){
                System.out.println("Désolé, le cours avec l'ID '" + coursid + "' n'existe pas dans notre base de données");
            }
            else
                System.out.println("Désolé, " + etudiant.getFname() + " possède déjà ce cours, choississez un autre");

        } while (counter < countInscription);

        int nbreLigneInsere = PersistantManager.insert(etudiant);
        System.out.println(nbreLigneInsere + " ligne inserée");
    }

    /**
     * METHODE PERMETTANT LA CREATION D'UN NOUVEAU COURS TOUT EN INSCRIVANT UN CERTAINS NOMBRE D'ETUDIANT
     */
    public static void inscrirePlusieursEtudiantsOneCours() throws IllegalAccessException, SQLException, InstantiationException {
        Cours cours = creationCours();
        List<Etudiant> listeEtudiantExistant = PersistantManager.retrieveSet(Etudiant.class, "SELECT * FROM etudiant");
        int nombreEtudiantExistant = listeEtudiantExistant.size();
        int countEtudiant;
        int counter = 0;
        ArrayList<Integer> listeChoixFait = new ArrayList<>();

        /* VERIFIER QUE LA VALEUR ENTRÉE PAR LES USERS NE DEPASSENT PAS LE NOMBRE D'ETUDIANT DISPONIBLE */
        do {
            countEtudiant = Main.prompt("Entrer le nombre d'Etudiant à inscrire au cours " + cours.getNameCours());
            if (countEtudiant > nombreEtudiantExistant)
                System.out.println("Vous ne Pouvez pas entrer un nombre plus élevé que le nombre d'étudiants existants\n" +
                        "Nous Disposons actuellement de " + nombreEtudiantExistant + " étudiants dans la Base de données.\n");
        } while (countEtudiant > nombreEtudiantExistant);

        /* VERIFIER QUE L'UTILISATEUR N'AFFECTE PAS DEUX FOIS LE COURS À UN ETUDIANT */
        do {
            int etudiantid = Main.prompt((counter+1) + ". Entrer l'ID de l'.Etudiant");
            if (!listeChoixFait.contains(etudiantid) && Etudiant.isContainedEtudiant(listeEtudiantExistant,etudiantid)) {
                cours.inscriptions.add(InscriptionBuilder.creation(etudiantid, cours.getCoursid()));
                listeChoixFait.add(etudiantid);
                counter++;
            }
            else if(!Etudiant.isContainedEtudiant(listeEtudiantExistant,etudiantid)){
                System.out.println("Désolé, l'étudiant avec l'ID '" + etudiantid + "' n'existe pas dans notre base de données");
            }
            else
                System.out.println("Désolé, le cours " + cours.getSigle() + " a déjà été donné à cet étudiant, choississez un autre étudiant");

        } while (counter < countEtudiant);

        int nbreLigneInsere = PersistantManager.insert(cours);
        System.out.println(nbreLigneInsere + " ligne inserée");
    }

    /**
     * METHODE PERMETTANT LA CREATION D'UN ETUDIANT, D'UN COURS ET ASSIGNÉ À CET ETUDIANT LE NOUVEAU COURS
     */
    public static void AjouterEtudiantCoursInscription() throws CustomAccessException, SQLException {
        Etudiant etudiant = creationEtudiant();
        Cours cours = creationCours();
        PersistantManager.insert(etudiant);
        PersistantManager.insert(cours);
        Inscription inscription = InscriptionBuilder.creation(etudiant.getEtudiantid(), cours.getCoursid());
        int nbreLigneInsere = PersistantManager.insert(inscription);
        System.out.println(nbreLigneInsere + " ligne inserée");
    }

    /** LISTE DES COURS D'UN ETUDIANT À PARTIR DE SON ID
     * @return void
     * */
    public static void listeCoursEtudiant() throws IllegalAccessException, SQLException, InstantiationException {
        int idEtudiant = Main.prompt("Entrer l'ID de l'étudiant");
        List<Inscription> listInscriptionEtudiant = PersistantManager.retrieveSet(Inscription.class, "SELECT * FROM inscription WHERE etudiantid = " + idEtudiant);
        Etudiant etudiant = PersistantManager.retrieveSet(Etudiant.class, "SELECT * FROM etudiant WHERE etudiantid = " + idEtudiant).get(0);
        if(!listInscriptionEtudiant.isEmpty()) {
            System.out.println("\t\tListe des Cours de " + etudiant.getFname() + " " + etudiant.getLname()
                    + "\n\t\t---------------------------");
            for (Inscription e : listInscriptionEtudiant) {
                System.out.println(e.getUnCours());
            }
        }
        else
            System.out.println("Desolé, L'Etudiant avec ID '" + idEtudiant + "' n'existe pas dans la base de données");
    }

    /** LISTE DES COURS D'UN COURS À PARTIR DE SON ID
     * @return void
     * */
    public static void afficherlisteEtudiantCours() throws IllegalAccessException, SQLException, InstantiationException {
        int idCours = Main.prompt("Entrer l'ID du Cours");
        List<Inscription> listInscriptionCours = PersistantManager.retrieveSet(Inscription.class, "SELECT * FROM inscription WHERE coursid = " + idCours);
        Cours monCours = PersistantManager.retrieveSet(Cours.class, "SELECT * FROM cours WHERE coursid = " + idCours).get(0);
        if(!listInscriptionCours.isEmpty()){
            System.out.println("\t\tListe des étudiants inscrit au cours " + monCours.getNameCours()
                    + "\n\t\t---------------------------------------------------------------");
            for(Inscription e : listInscriptionCours){
                System.out.println(e.getUnEtudiant());
            }
        }
        else
            System.out.println("Desolé, Le cours avec ID '" + idCours + "' n'existe pas dans la base de données");
    }

}
