package com;

import com.bean.*;
import com.database.ImportingDatabase;
import com.exception.CustomAccessException;
import com.persistance.PersistantManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.bean.Cours.askMeCourse;
import static com.bean.Etudiant.askMeStudent;

public class GestionTransaction {

// *******************************************************************************************
// ***********   LES DIFFERENTES METHODES UTILISÉES POUR EFFECTUER DES OPÉRATIONS   ************
// ***********    DANS LA BASE DE DONNÉES (CREATION, MISE À JOUR, INSERTION,...)    ************
// *********************************************************************************************/

    /**
     * Methode permettant de Créer une Instance Etudiant tout en lui donnant un etudiantid
     *
     * @return Etudiant
     * @throws CustomAccessException : Impossible d'accéder à un champs
     * @throws SQLException          : une erreur dûe à une mauvaise manipulation de la Base de données
     * @see EtudiantBuilder
     */
    private static Etudiant creationEtudiant() throws CustomAccessException, SQLException {
        String firstName = Main.promptString("Entrer le Prenom de l'Etudiant");
        String lastName = Main.promptString("Entrer le Nom de l'Etudiant");
        String age = Main.promptString("Entrer l'age de l'Etudiant");
        if (age.equals(""))
            age = "0";
        System.out.println("Enregistrement de L'étudiant " + firstName + " en cours d'exécution...");
        return EtudiantBuilder.creation(firstName, lastName, Integer.parseInt(age));
    }

    /**
     * Methode permettant l'Insertion de L'Etudiant dans la base de données
     *
     * @return void
     * @throws CustomAccessException : Impossible d'accéder à un Champs
     * @throws SQLException          : une erreur dûe à une mauvaise manipulation de la Base de données
     * @see PersistantManager
     */
    public static void ajouterEtudiantBD() throws CustomAccessException, SQLException {
        System.out.println(PersistantManager.insert(creationEtudiant()) + " ligne inserée");
    }

    /**
     * Methode Permettant de Créer une Instance Cours tout en lui donnant un coursid
     *
     * @return Cours
     * @throws CustomAccessException : Impossible d'accéder à un champs
     * @throws SQLException          : une erreur dûe à une mauvaise manipulation de la Base de données
     * @see CoursBuilder
     */
    private static Cours creationCours() throws CustomAccessException, SQLException {
        String name = Main.promptString("Entrer le nom du Cours");
        String sigle = Main.promptString("Entrer le sigle pour " + name);
        String description = Main.promptString("Entrer la Description du Cours");
        System.out.println("Enregistrement du Cours " + name + " en cours d'exécution...");
        return CoursBuilder.creation(name, sigle, description);
    }

    /**
     * Methode permettant l'Insertion du Cours dans la Base de Données
     *
     * @return void
     * @throws CustomAccessException : Impossible d'accéder à un champs
     * @throws SQLException          : une erreur dûe à une mauvaise manipulation de la Base de données
     * @see PersistantManager
     */
    public static void ajouterCoursBD() throws CustomAccessException, SQLException {
        System.out.println(PersistantManager.insert(creationCours()) + " ligne inserée");
    }

    /**
     * Methode Permettant d'inscrire un Etudiant via etudiantid à un cours via coursid
     *
     * @return void
     * @throws IllegalAccessException : Impossible d'accéder à un champs
     * @throws SQLException           : une erreur dûe à une mauvaise manipulation de la Base de données
     * @throws InstantiationException : Erreur au niveau de l'instanciation d'un bean
     * @see PersistantManager
     * @see InscriptionBuilder
     */
    public static void ajouterCoursEtudiant() throws IllegalAccessException, SQLException, InstantiationException {
        int etudiantid = Main.prompt("Entrer l'ID de l'étudiant");
        List<Cours> listeCours = PersistantManager.retrieveSet(Cours.class, ImportingDatabase.GET_ALL_COURSE);
        Etudiant etudiant = PersistantManager.retrieveSet(Etudiant.class, ImportingDatabase.GET_SPECIFIC_STUDENT(etudiantid)).get(0);
        int nombreCoursExistante = listeCours.size();
        int countInscription;
        int counter = 0;
        ArrayList<Integer> listeChoixFait = new ArrayList<>();

        /* VERIFIER QUE LA VALEUR ENTRÉE PAR LES USERS NE DEPASSENT PAS LE NOMBRE DE COURS DISPONIBLE */
        do {
            countInscription = Main.prompt("Combien de Cours pour l'étudiant " + etudiant.getFname() + " ?");
            if (countInscription > nombreCoursExistante)
                System.out.println("Vous ne Pouvez pas entrer un nombre plus élevé que le nombre des cours existants\n" +
                        "Nous Disposons actuellement de " + nombreCoursExistante + " Cours disponible.\n");
        } while (countInscription > nombreCoursExistante);

        /* VERIFIER QUE L'UTILISATEUR N'AFFECTE PAS DEUX FOIS LE MEME COURS À L'ETUDIANT */
        do {
            int coursid = Main.prompt((counter + 1) + ". Entrer l'ID du Cours");
            if (!listeChoixFait.contains(coursid) && Cours.isContainedCourse(listeCours, coursid)) {
                listeChoixFait.add(coursid);
                counter++;
            } else if (!Cours.isContainedCourse(listeCours, coursid)) {
                System.out.println("Désolé, le cours avec l'ID '" + coursid + "' n'existe pas dans notre base de données");
            } else
                System.out.println("Désolé, " + etudiant.getFname() + " possède déjà ce cours, choississez un autre");

        } while (counter < countInscription);

        // Pour Chaque cours, on inscrit l'etudiant
        for (int value : listeChoixFait) {
            Cours monCours = askMeCourse(listeCours, value);
            Inscription inscription = InscriptionBuilder.creation(etudiantid, value);
            PersistantManager.insert(inscription);
            inscription.setCours(monCours);
            inscription.setEtudiant(etudiant);
        }
        System.out.println("Opération terminée");
    }


    /**
     * Methode Permettant d'insérer un etudiant dans la base de données tout en lui inscrivant dans certains cours
     *
     * @return void
     * @throws IllegalAccessException : Impossible d'accéder à un champs
     * @throws SQLException           : une erreur dûe à une mauvaise manipulation de la Base de données
     * @throws InstantiationException : Erreur au niveau de l'instanciation d'un bean
     * @see PersistantManager
     * @see InscriptionBuilder
     */
    public static void ajouterEtudiantAvecInscription() throws IllegalAccessException, SQLException, InstantiationException {
        Etudiant etudiant = creationEtudiant();
        List<Cours> listeCours = PersistantManager.retrieveSet(Cours.class, ImportingDatabase.GET_ALL_COURSE);
        int nombreCoursExistante = listeCours.size();
        int countInscription;
        int counter = 0;
        ArrayList<Integer> listeChoixFait = new ArrayList<>();

        /* VERIFIER QUE LA VALEUR ENTRÉE PAR LES USERS NE DEPASSENT PAS LE NOMBRE DE COURS DISPONIBLE */
        do {
            countInscription = Main.prompt("Combien de Cours pour l'étudiant " + etudiant.getFname() + " ?");
            if (countInscription > nombreCoursExistante)
                System.out.println("Vous ne Pouvez pas entrer un nombre plus élevé que le nombre des cours existants\n" +
                        "Nous Disposons actuellement de " + nombreCoursExistante + " Cours disponible.\n");
        } while (countInscription > nombreCoursExistante);

        /* VERIFIER QUE L'UTILISATEUR N'AFFECTE PAS DEUX FOIS LE MEME COURS À L'ETUDIANT */
        do {
            int coursid = Main.prompt((counter + 1) + ". Entrer l'ID du Cours");
            if (!listeChoixFait.contains(coursid) && Cours.isContainedCourse(listeCours, coursid)) {
                Inscription inscription = InscriptionBuilder.creation(etudiant.getEtudiantid(), coursid);
                etudiant.inscriptions.add(inscription);
                listeChoixFait.add(coursid);
                counter++;
            } else if (!Cours.isContainedCourse(listeCours, coursid)) {
                System.out.println("Désolé, le cours avec l'ID '" + coursid + "' n'existe pas dans notre base de données");
            } else
                System.out.println("Désolé, " + etudiant.getFname() + " possède déjà ce cours, choississez un autre");

        } while (counter < countInscription);

        int nbreLigneInsere = PersistantManager.insert(etudiant);

        // Ajout des informations Complementaires
        for (int i = 0; i < etudiant.inscriptions.size(); i++) {
            etudiant.inscriptions.get(i).setEtudiant(etudiant);
            etudiant.inscriptions.get(i).setCours(askMeCourse(listeCours, listeChoixFait.get(i)));
        }
        System.out.println(nbreLigneInsere + " ligne inserée");
    }

    /**
     * Methode permettant la Création d'un nouveau Cours tout en Inscrivant un certains nombre d'étudiant
     *
     * @return void
     * @throws IllegalAccessException : Impossible d'accéder à un champs
     * @throws SQLException           : une erreur dûe à une mauvaise manipulation de la Base de données
     * @throws InstantiationException : Erreur au niveau de l'instanciation d'un bean
     * @see PersistantManager
     * @see InscriptionBuilder
     */
    public static void inscrirePlusieursEtudiantsOneCours() throws IllegalAccessException, SQLException, InstantiationException {
        Cours cours = creationCours();
        List<Etudiant> listeEtudiantExistant = PersistantManager.retrieveSet(Etudiant.class, ImportingDatabase.GET_ALL_STUDENT);
        List<Inscription> listeInscriptionFaite = new ArrayList<>();
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
            int etudiantid = Main.prompt((counter + 1) + ". Entrer l'ID de l'Etudiant");
            if (!listeChoixFait.contains(etudiantid) && Etudiant.isContainedEtudiant(listeEtudiantExistant, etudiantid)) {
                Inscription isc = InscriptionBuilder.creation(etudiantid, cours.getCoursid());
                cours.inscriptions.add(isc);
                listeInscriptionFaite.add(isc);
                listeChoixFait.add(etudiantid);
                counter++;
            } else if (!Etudiant.isContainedEtudiant(listeEtudiantExistant, etudiantid)) {
                System.out.println("Désolé, l'étudiant avec l'ID '" + etudiantid + "' n'existe pas dans notre base de données");
            } else
                System.out.println("Désolé, le cours " + cours.getSigle() + " a déjà été donné à cet étudiant, choississez un autre étudiant");

        } while (counter < countEtudiant);

        int nbreLigneInsere = PersistantManager.insert(cours);

        // Ajout des informations Complementaires
        for (int i = 0; i < cours.inscriptions.size(); i++) {
            Etudiant student = askMeStudent(listeEtudiantExistant, listeChoixFait.get(i));
            assert student != null;
            student.inscriptions.add(listeInscriptionFaite.get(i));
            cours.inscriptions.get(i).setEtudiant(student);
            cours.inscriptions.get(i).setCours(cours);
        }
        System.out.println(nbreLigneInsere + " ligne inserée");
    }

    /**
     * Methode permettant la création d'un étudiant, d'un cours et assigné à cet étudiant le nouveau cours
     *
     * @return void
     * @throws CustomAccessException : Impossible d'accéder au Champ
     * @throws SQLException          : s'il y a erreur dans l'accès de la base de données
     * @see PersistantManager
     */
    public static void AjouterEtudiantCoursInscription() throws CustomAccessException, SQLException {
        Etudiant etudiant = creationEtudiant();
        Cours cours = creationCours();
        Inscription inscription = InscriptionBuilder.creation(etudiant.getEtudiantid(), cours.getCoursid());

        // Les Insertions
        PersistantManager.insert(cours);
        PersistantManager.insert(etudiant);
        PersistantManager.insert(inscription);

        // Setter les informations complémentaires de l'inscription
        inscription.setCours(cours);
        inscription.setEtudiant(etudiant);
        System.out.println("Opération terminée");
    }

    /**
     * Liste des Cours d'un Etudiant à partir de son etudiantid
     *
     * @return void
     * @throws IllegalAccessException : Impossible d'accéder à un champs
     * @throws SQLException           : une erreur dûe à une mauvaise manipulation de la Base de données
     * @throws InstantiationException : Erreur au niveau de l'instanciation d'un bean
     * @see PersistantManager
     */
    public static void listeCoursEtudiant() throws IllegalAccessException, SQLException, InstantiationException {
        int idEtudiant = Main.prompt("Entrer l'ID de l'étudiant");
        System.out.println("Recherche de l'Etudiant avec l'ID \"" + idEtudiant + "\" est en cours d'exécution...");
        List<Inscription> listInscriptionEtudiant = PersistantManager.retrieveSet(Inscription.class, ImportingDatabase.GET_SPECIFIC_SUBSCRIBE_OF_STUDENT(idEtudiant));
        List<Etudiant> etudiants = PersistantManager.retrieveSet(Etudiant.class, ImportingDatabase.GET_SPECIFIC_STUDENT(idEtudiant));

        if(etudiants.isEmpty()){
            System.out.println("\tDésolé, l'étudiant avec l'ID \"" + idEtudiant + "\" n'existe pas dans notre base de données");
            return;
        }

        Etudiant etudiant = PersistantManager.retrieveSet(Etudiant.class, ImportingDatabase.GET_SPECIFIC_STUDENT(idEtudiant)).get(0);

        if (!listInscriptionEtudiant.isEmpty()) {
            System.out.println("\t\tListe des Cours de " + etudiant.getFname() + " " + etudiant.getLname()
                    + "\n\t\t---------------------------");
            for (Inscription inscription : listInscriptionEtudiant) {
                System.out.println(inscription.getUnCours());
            }
        } else
            System.out.println("Desolé, L'Etudiant avec ID '" + idEtudiant + "' n'existe pas dans la base de données");
    }

    /**
     * Liste des étudiants d'un cours à partir de son coursid
     *
     * @return void
     * @throws IllegalAccessException : Impossible d'accéder à un champs
     * @throws SQLException           : une erreur dûe à une mauvaise manipulation de la Base de données
     * @throws InstantiationException : Erreur au niveau de l'instanciation d'un bean
     * @see PersistantManager
     */
    public static void afficherlisteEtudiantCours() throws IllegalAccessException, SQLException, InstantiationException {
        int idCours = Main.prompt("Entrer l'ID du Cours");
        System.out.println("Recherche du Cours avec l'ID : " + idCours + " en cours d'exécution...");
        List<Inscription> listInscriptionCours = PersistantManager.retrieveSet(Inscription.class, ImportingDatabase.GET_SPECIFIC_SUBSCRIBE_OF_COURSE(idCours));

        List<Cours> courses = PersistantManager.retrieveSet(Cours.class, ImportingDatabase.GET_SPECIFIC_COURSE(idCours));

        if(courses.isEmpty()){
            System.out.println("\tDésolé, le cours avec l'ID \"" + idCours + "\" n'existe pas dans notre base de données");
            return;
        }

        Cours monCours = PersistantManager.retrieveSet(Cours.class, ImportingDatabase.GET_SPECIFIC_COURSE(idCours)).get(0);

        if (!listInscriptionCours.isEmpty()) {
            System.out.println("\t\tListe des étudiants inscrit au cours " + monCours.getNameCours()
                    + "\n\t\t---------------------------------------------------------------");
            for (Inscription e : listInscriptionCours) {
                System.out.println(e.getUnEtudiant());
            }
        } else
            System.out.println("Desolé, Le cours avec ID '" + idCours + "' n'existe pas dans la base de données");
    }

}
