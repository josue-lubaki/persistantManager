package com.database;

import com.exception.CustomAccessException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.sql.*;

/**
 * Cette classe permet de faire l'importation de la base de données
 */
public class ImportingDatabase {

    /** Quelques differentes requêtes Utilisées dans le programme */
    public static String GET_ALL_STUDENT = "SELECT * FROM etudiant";
    public static String GET_ALL_COURSE = "SELECT * FROM cours";
    public static String GET_ALL_SUBSCRIBE = "SELECT * FROM inscription";
    public static String GET_SPECIFIC_STUDENT(int etudiantid){
        return "SELECT * FROM etudiant WHERE etudiantid = " + etudiantid;
    }
    public static String GET_SPECIFIC_COURSE(int coursid){
        return "SELECT * FROM cours WHERE coursid = " + coursid;
    }
    public static String GET_SPECIFIC_SUBSCRIBE(int inscriptionid){
        return "SELECT * FROM inscription WHERE inscriptionid = " + inscriptionid;
    }
    public static String GET_SPECIFIC_SUBSCRIBE_OF_STUDENT(int etudiantid){
        return "SELECT * FROM inscription WHERE etudiantid = " + etudiantid;
    }
    public static String GET_SPECIFIC_SUBSCRIBE_OF_COURSE(int coursid){
        return "SELECT * FROM inscription WHERE coursid = " + coursid;
    }

    /** Variables d'Instances */
    private final ConnectivityChecking entity;
    private static ImportingDatabase instance;
    private PreparedStatement statement;
    private static Connection con;

    @Inject
    public ImportingDatabase(ConnectivityChecking entity) {
        this.entity = entity;
    }

    public static Connection getConnexion() {
        return con;
    }

    /**
     * Methode permettant d'Obtenir l'instance de la Connexion
     * Si la connexion n'existe pas, elle fait une injection, ce qui permet de créer une nouvelle connexion
     * grâce aux identifiants passée en paramètre de setLoginConnection
     *
     * @return ImportingDatabase
     * @see Guice
     * @see Injector
     */
    public static ImportingDatabase getInstance() {
        if (con == null) {
            Injector inject = Guice.createInjector(new ImportingDatabaseModule());
            ImportingDatabase.instance = inject.getInstance(ImportingDatabase.class);
            ImportingDatabase.instance.entity.setLoginConnection(
                    "postgresql",
                    "localhost",
                    "postgres",
                    "Heroes",
                    5432,
                    "postgres");
        }
        return instance;
    }

    /**
     * Methode permettant de se déconnecter de l'actuelle Connexion
     *
     * @param connexion : Information de la connexion utilisé
     * @return void
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    public static void disconnect(Connection connexion) throws SQLException {
        if (connexion != null) {
            con.close();
            con = null;
        }
    }

    /**
     * Methode permettant d'aller Chercher le prochain ID disponible de la Table
     *
     * @param nameSequence : le nom de la Sequence de la Table
     * @return int
     * @throws CustomAccessException : Impossible d'accéder à un champs
     * @throws SQLException          : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    public static int ObtenirIndexSuivant(String nameSequence) throws CustomAccessException, SQLException {
        ResultSet rs = retrieve("SELECT nextval('" + nameSequence + "'::regclass)");
        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
        throw new CustomAccessException("Impossible de récupérer l'index courant : " + nameSequence);
    }

    /**
     * Methode permettant de préparer la Requête SQL, tout en créant une nouvelle connexion
     * Garde le resultat de la préparation dans la variable statement
     *
     * @param sql : La requête SQL à preparer pour éxecution
     * @return void
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    private void PrepareStatementWithConnexion(String sql) throws SQLException {
        con = this.entity.getConnection();
        this.statement = con.prepareStatement(sql);
    }

    /**
     * Methode permettant de préparer la Requête SQL, tout en Utilisant la connexion déjà existante
     * Garde le resultat de la préparation dans la variable statement
     *
     * @param sql : La requête SQL à preparer pour éxecution
     * @return void
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    private void PrepareStatementWithoutConnexion(String sql) throws SQLException {
        this.statement = con.prepareStatement(sql);
    }

    /**
     * Methode permettant d'exécuter la requête déjà prête de la variable statement (SELECT)
     *
     * @return ResultSet
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    private ResultSet ExecuteRequete() throws SQLException {
        this.statement.executeQuery();
        return this.statement.getResultSet();
    }

    /**
     * Methode permettant d'exécuter la requête déjà prête de la variable statement (INSERT,UPDATE,DELETE)
     *
     * @return void
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    private void ExecuteInsert(String sql) throws SQLException {
        VerifyConnexionAndCreateStatement(sql);
        this.statement.executeUpdate();
    }

    /**
     * Methode permettant d'exécuter la requête SQL reçue en paramètre,
     * tout en vérifiant l'existance de la conexion au préalable
     *
     * @param sql La Requête SQL à executer
     * @return ResultSet
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    private ResultSet Execute(String sql) throws SQLException {
        VerifyConnexionAndCreateStatement(sql);
        return this.ExecuteRequete();
    }

    /**
     * Methode permettant de vérifier l'existance de la connexion et s'assurer de la préparation de la requête
     *
     * @param sql La Requête SQL à préparer
     * @return void
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    private void VerifyConnexionAndCreateStatement(String sql) throws SQLException {
        // Vérifier si la Connexion est toujours maintenue (Si existe)
        if (con == null)
            this.PrepareStatementWithConnexion(sql);
        else
            this.PrepareStatementWithoutConnexion(sql);
    }

    /**
     * Methode faisant office de frontière entre différente classe en recevant la Requête SQL en paramètre,
     * Elle crée une Connexion à partir de l'instance et exécute la requête SQL.
     * S'execute avec une manipulation de type SELECT
     *
     * @param sql La Requête SQL à executer
     * @return ResultSet
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    public static ResultSet retrieve(String sql) throws SQLException {
        return ImportingDatabase.getInstance().Execute(sql);
    }

    /**
     * Methode faisant office de frontière entre différente classe en recevant la Requête SQL en paramètre,
     * Elle crée une Connexion à partir de l'instance et exécute la requête SQL.
     * S'execute avec une manipulation de type INSERT, UPDATE, DELETE
     *
     * @param sql La Requête SQL à executer
     * @return ResultSet
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     */
    public static void retrieveInsert(String sql) throws SQLException {
        ImportingDatabase.getInstance().ExecuteInsert(sql);
    }

}
