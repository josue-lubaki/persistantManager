package com.persistance;

import com.database.ImportingDatabase;
import com.exception.CustomAccessException;
import com.annotations.Bean;
import com.annotations.BeanList;
import com.annotations.Ignore;
import com.annotations.idBeanExterne;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersistantManager {

    /**
     * Permet de recupèrer les proprietés de la base de données selon le representant de la classe génériquement
     *
     * @param beanClass : representant de la classe
     * @param sql       : la requête SQL à executer
     * @return List<T>
     */
    public static <T> List<T> retrieveSet(Class<T> beanClass, String sql) throws SQLException, InstantiationException, IllegalAccessException {
        List<T> records = new ArrayList<>();
        // PREPARATION DE LA REQUETE ET EXECUTION DU 'sql' REÇU EN PARAMETRE
        ResultSet resultatQuery = ImportingDatabase.retrieve(sql);
        retrieveBeans(beanClass, resultatQuery, records);

        return records;
    }

    /**
     * Methode qui traite le resultat de la requête SQL et ensuite applique la requête sur le Bean courant
     *
     * @param beanClass     : représentant de la classe
     * @param resultatQuery : le resultat de la reqûete SQL
     * @param records       : liste dans laquelle on enregistre tous les beans
     * @throws IllegalAccessException si le champs n'est pas accesssible
     * @throws InstantiationException si l'instantiation n'a pas reussie
     * @throws SQLException           s'il y a erreur dans l'accès de la base de données
     */
    private static <T> void retrieveBeans(Class<T> beanClass, ResultSet resultatQuery, List<T> records) throws IllegalAccessException, InstantiationException, SQLException {
        T record;
        Annotation uneAnnotation;

        // ON PARCOURT LE RESULTAT DE LA REQUÊTE
        while (resultatQuery.next()) {
            record = beanClass.newInstance();//on instancie un objet
            Field[] fields = beanClass.getDeclaredFields();//on recupere toutes les donnes qui ont ete encapsules
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getAnnotations().length != 0) {
                    uneAnnotation = field.getAnnotations()[0];
                    if (uneAnnotation instanceof Ignore)
                        continue;
                    if (uneAnnotation instanceof BeanList) {
                        retrieveBeanList(beanClass, record, resultatQuery, field);
                    }
                    if (uneAnnotation instanceof idBeanExterne) {
                        retrieveBeanExtern(record, resultatQuery, field);
                    }
                } else {
                    retrieveSimpleBean(record, resultatQuery, field);
                }
            }
            populer(beanClass, records, record);
        }
    }

    /**
     * RECUPERATION DES BEANS SIMPLES
     *
     * @param record    : bean utilisé dans la recursivité pour accumulé les données au fur et à mesure
     * @param resultSet : le resultat de la reqûete SQL
     * @param field     : Le Champs de la bean actuellement évalué
     * @return void
     * @throws IllegalAccessException : Impossible d'accéder à un champs
     */
    private static <T> void retrieveSimpleBean(T record, ResultSet resultSet, Field field) throws IllegalAccessException {
        if (field.getAnnotations().length == 0 && field.getType().isPrimitive() || field.getType().isInstance("")) {
            try {
                field.set(record, resultSet.getObject(resultSet.findColumn(field.getName())));
            } catch (SQLException e) {
                System.out.println("La colonne n'a pas été trouvé @see findColumn");
                e.printStackTrace();
            }
        }
    }

    /**
     * RECUPERATION DES BEANLIST
     *
     * @param beanClass : représentant de la classe
     * @param resultSet : le resultat de la reqûete SQL
     * @param field     : Le Champs de la bean actuellement évalué
     * @return void
     * @throws IllegalAccessException : Impossible d'accéder à un Champs
     */
    private static <T> void retrieveBeanList(Class<T> beanClass, T bean, ResultSet resultSet, Field field) throws IllegalAccessException {
        String className = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
        String nameTable = null;
        String clef_Primaire = null;
        try {
            nameTable = Class.forName(className).getAnnotation(Bean.class).table();
            clef_Primaire = beanClass.getAnnotation(Bean.class).primaryKey();
            int idClefPrimaire = (int) resultSet.getObject(resultSet.findColumn(clef_Primaire));
            String request = "SELECT * FROM " + nameTable + " WHERE " + clef_Primaire + " = " + idClefPrimaire;
            field.set(bean, retrieveSet(Class.forName(className), request));
        } catch (ClassNotFoundException e) {
            System.out.println("La Classe " + nameTable + " est introuvable !");
            e.printStackTrace();
        } catch (SQLException throwables) {
            System.out.println("Impossible de trouver la colonne " + clef_Primaire);
            throwables.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("Impossible d'instancier " + bean.getClass().getName());
        }
    }

    /**
     * RECUPERATION DES BEANsEXTERN (bean de Jointure)
     *
     * @param bean      : bean utilisé dans la recursivité pour accumulé les données au fur et à mesure
     * @param resultSet : le resultat de la reqûete SQL
     * @param field     : Le Champs de la bean actuellement évalué
     * @return void
     * @throws IllegalAccessException : Impossible d'acceder au champs
     */
    private static <T> void retrieveBeanExtern(T bean, ResultSet resultSet, Field field) throws IllegalAccessException {
        try {
            String className = field.getType().getName();
            String nameTable = Class.forName(className).getAnnotation(Bean.class).table();
            String clef_Primaire = Class.forName(className).getAnnotation(Bean.class).primaryKey();
            int idClefPrimaire = (int) resultSet.getObject(resultSet.findColumn(clef_Primaire));
            T tempBean = VerifierExistence((Class<T>) Class.forName(className), idClefPrimaire);
            if (tempBean != null) {
                field.set(bean, tempBean);
            } else {
                String request = "SELECT * FROM " + nameTable + " WHERE " + clef_Primaire + " = " + idClefPrimaire;
                field.set(bean, Objects.requireNonNull(retrieveSet(Class.forName(className), request)).get(0));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("La classe appelée par forName() est introuvable");
            e.printStackTrace();
        } catch (SQLException throwables) {
            System.out.println("La colonne recherchée est introuvable, @see findColumn()");
            throwables.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("Impossible d'instancier " + bean.getClass().getName());
        }
    }

    /**
     * Methode permettant de Populer les valeurs dans les champs correspondants
     *
     * @param beanClass : représentant de la classe
     * @param list      : La liste qui contient tous les beans de la methode RetrieveSet
     * @param bean      : Le Champs de la bean actuellement évalué
     * @return void
     */
    private static <T> void populer(Class<T> beanClass, List<T> list, T bean) {
        String nomID = beanClass.getAnnotation(Bean.class).primaryKey();//on veut populer le nom de l'id
        Field id;//on recupere un champ qui represente l'id de la classe courante
        try {

            id = beanClass.getDeclaredField(nomID);
            id.setAccessible(true);
            T tempBean = VerifierExistence(beanClass, id.getInt(bean));
            if (tempBean != null)  //Vérifie l'existence du com.bean avec son ID
                list.add(tempBean);
            else
                list.add(bean);

        } catch (NoSuchFieldException e) {
            System.out.println("Introuvable beanClass.getDeclaredField");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("Impossible d'acceder à id.getInt(com.bean)");
            e.printStackTrace();
        }
    }

    /**
     * Une methode qui permet de Verifier si la bean existe déjà ou pas
     * Methode executée dans la RetrieveSet()
     * Si cette methode n'existe pas, la methode RetrieveSet fonctionnera en loop infini
     *
     * @param beanClass : Représentant de la Classe
     * @param id        : correspond à ID dont on veut connaître l'existence
     * @return T
     * @throws IllegalAccessException : Impossible d'acceder au Champs
     */
    private static <T> T VerifierExistence(Class<T> beanClass, int id) throws IllegalAccessException {
        String nomID = null;//on declare le nom de la liste de l'id qu'on veut recuperer
        Field fieldList = null;
        Field idField = null;
        try {
            nomID = beanClass.getAnnotation(Bean.class).primaryKey();
            idField = beanClass.getDeclaredField(nomID);
            idField.setAccessible(true);

            //on recuperes la liste listMyinstance() via les annotations
            String nomFieldList = beanClass.getAnnotation(Bean.class).listMyInstance();
            fieldList = beanClass.getDeclaredField(nomFieldList);
            fieldList.setAccessible(true);

        } catch (NoSuchFieldException e) {
            System.out.println("Impossinle d'accéder au répresentant de la classe qui contient '" + nomID + "'");
            e.printStackTrace();
        }

        assert fieldList != null;
        // get(null) pour recuperer tous les contenus et non un seul en particulier
        ArrayList<T> uneListe = (ArrayList<T>) fieldList.get(null);

        for (T bean : uneListe) {
            if (idField.getInt(bean) == id) {
                //System.out.println("---->ID :" + id + " existe deja");
                return bean;
            }
        }
        //System.out.println("ID : " + id + " n'existe pas");
        return null;
    }

    /**
     * Methode permettant d'insérer les données dans la Database
     * Return Row Count inserted
     *
     * @return int
     * @throws SQLException : une erreur dûe à une mauvaise manipulation de la Base de données
     * @see ImportingDatabase
     */
    public static <T> int insert(T bean) throws SQLException {

        int nbInsertions = 0;//on retourne un nombre d'insertions
        StringBuilder requeteSql = new StringBuilder("INSERT INTO ");
        Class<?> representantBean = bean.getClass();//representant de la classe bean
        String tableBean = representantBean.getAnnotation(Bean.class).table();// on recupere la table du bean
        String primaryKeyName = representantBean.getAnnotation(Bean.class).primaryKey();//de meme pour la cle primaire

        requeteSql.append(tableBean).append(getNomCol(bean, primaryKeyName)).append("VALUES (");
        Field[] fields = representantBean.getDeclaredFields();
        compositionChampsTable(fields, bean, primaryKeyName, requeteSql);
        requeteSql = new StringBuilder(requeteSql.substring(0, requeteSql.length() - 2) + ")");
        System.out.println("SQL : " + requeteSql);

        ImportingDatabase.retrieveInsert(requeteSql.toString());
        nbInsertions++;

        nbInsertions = insertionBeansComplexes(fields, nbInsertions, bean);

        return nbInsertions;
    }

    /**
     * Methode permettant d'insérer les beans Complexes
     * Retourne le nombre de ligne insérée avec les beans complexes
     *
     * @param fields       Tableau de tous les champs de la Class<T> beanClass
     * @param nbInsertions nombre d'insertion faite ou déjà faite
     * @param bean         la bean utilisée dans la recursivité pour vérifier si elle est complexe ou simple
     * @return int
     * @see Annotation
     */
    public static <T> int insertionBeansComplexes(Field[] fields, int nbInsertions, T bean) {
        for (Field field : fields) {
            try {
                if (field.getAnnotations().length != 0) {
                    Annotation annotation = field.getAnnotations()[0];
                    if (annotation instanceof Ignore)
                        continue;

                    if (annotation instanceof BeanList)
                        nbInsertions += bulkInsert((List<T>) field.get(bean));

                    else if (annotation instanceof idBeanExterne && field.get(bean) != null) {
                        if (VerifierExistenceBean(field.get(bean)))
                            nbInsertions += insert(field.get(bean));
                    }
                }
            } catch (IllegalAccessException | SQLException e) {
                System.out.println("Impossible d'accéder au " + field.getName());
                e.printStackTrace();
            }
        }
        return nbInsertions;
    }

    /**
     * Methode permettant de composer la requête qui sera exécutée pour l'insertion
     * Elle est chargée d'aller recupérer les noms de champs de la table en introduisant les valeurs voulu pour insertion
     *
     * @param fields         tableau contenant tous les champs de la classe (Class<T> beanClass)
     * @param bean           la bean utilisée dans la recursivité pour vérifier si elle est complexe ou simple
     * @param primaryKeyName le nom de la clé primaire utilisé pour la Tbale courante
     * @param requeteSql     contient le debut de la requête SQL à exécuter (INSERT INTO...)
     */
    public static <T> void compositionChampsTable(Field[] fields, T bean, String primaryKeyName, StringBuilder requeteSql) {
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals(primaryKeyName) || (field.getType().isPrimitive()
                    || field.getType().isInstance(""))) {
                try {
                    // Enlever le <'> lorsque'il s'agit d'un Integer
                    if (field.get(bean) instanceof Integer) {
                        requeteSql.append(field.get(bean)).append(", ");

                    } else {
                        requeteSql.append("'").append(field.get(bean)).append("', ");
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("Impossible d'accéder au " + field.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Methode d'insertion multiple
     *
     * @param listeBeans : la liste contenant tous les beans prêt pour insertion
     * @return int (nombre d'insertion faite)
     * @throws SQLException           : s'il y a erreur dans l'accès de la base de données
     * @throws IllegalAccessException : Impossible d'accéder au Champs
     */
    public static <T> int bulkInsert(List<T> listeBeans) throws SQLException, IllegalAccessException {
        int nbInsertions = 0;
        for (T bean : listeBeans) {
            if (VerifierExistenceBean(bean)) {
                nbInsertions += insert(bean);
            }
        }
        return nbInsertions;
    }

    /**
     * Methode permettant d'aller chercher le nom de champs de la Table dans la Base de données
     *
     * @param bean           : La bean actuellement évaluée (Correspond à la Table dans une BD)
     * @param primaryKeyName : correspond au nom du champs de la clé primaire
     * @return String
     */
    private static <T> String getNomCol(T bean, String primaryKeyName) {
        StringBuilder nomCol = new StringBuilder(" (");
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getAnnotations().length == 0 && field.getName().equals(primaryKeyName) || (
                    field.getType().isPrimitive() || field.getType().isInstance("")))
                nomCol.append(field.getName()).append(", "); // on ajoute le nom du champ tant que ce n'est pas une cle primaire
        }
        nomCol = new StringBuilder(nomCol.substring(0, nomCol.length() - 2) + ") ");// on enleve l'affichage de la virgule a la fin pour mettre une parenthese
        return nomCol.toString();
    }

    /**
     * Methode permettant de Vérifier l'existence de la Bean avant Insertion
     *
     * @param bean : correspond à la bean dont on veut connaître l'existence
     * @return boolean
     * @throws IllegalAccessException : Impossible d'accéder à un Champs
     */
    private static <T> boolean VerifierExistenceBean(T bean) throws IllegalAccessException {
        String nomClePrimaire = bean.getClass().getAnnotation(Bean.class).primaryKey();
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(nomClePrimaire)) {
                field.setAccessible(true);
                return (field.getInt(bean) != 0);
            }
        }
        throw new CustomAccessException("Le Champ correspondant à " + nomClePrimaire + " est introuvable");

    }
}
