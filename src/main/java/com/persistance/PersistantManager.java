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
     * RECUPERATION DES BEANsEXTERN
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
     * FONCTION POUR POPULER LE BEAN
     *
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

    private static <T> T VerifierExistence(Class<T> beanClass, int id) throws IllegalAccessException {
        String nomID = null;//on declare le nom de la liste de l'id qu'on veut recuperer
        Field fieldList = null;
        Field idField = null;
        try {
            nomID = beanClass.getAnnotation(Bean.class).primaryKey();
            idField = beanClass.getDeclaredField(nomID);
            idField.setAccessible(true);

            //on recuperes la liste listMyinstance() via les com.annotations
            String nomFieldList = beanClass.getAnnotation(Bean.class).listMyInstance();
            fieldList = beanClass.getDeclaredField(nomFieldList);
            fieldList.setAccessible(true);

        } catch (NoSuchFieldException e) {
            System.out.println("Impossinle d'accéder au répresentant de la classe qui contient '" + nomID + "'");
            e.printStackTrace();
        }

        assert fieldList != null;
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

    public static <T> int insert(T bean) throws SQLException {

        int nbInsertions = 0;//on retourne un nombre d'insertions
        StringBuilder requeteSql = new StringBuilder("INSERT INTO ");
        Class<?> representantBean = bean.getClass();//representant de la classe bean
        String tableBean = representantBean.getAnnotation(Bean.class).table();// on recupere la table du bean
        String primaryKeyName = representantBean.getAnnotation(Bean.class).primaryKey();//de meme pour la cle primaire

        requeteSql.append(tableBean).append(getNomCol(bean, primaryKeyName)).append("VALUES (");
        Field[] fields = representantBean.getDeclaredFields();
        Annotation annotation;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals(primaryKeyName) || (field.getType().isPrimitive()
                    || field.getType().isInstance(""))) {
                try {
                    requeteSql.append("'").append(field.get(bean)).append("', ");
                } catch (IllegalAccessException e) {
                    System.out.println("Impossible d'accéder au " + field.getName() + " du bean "
                            + tableBean);
                    e.printStackTrace();
                }
            }
        }
        requeteSql = new StringBuilder(requeteSql.substring(0, requeteSql.length() - 2) + ")");
        System.out.println("SQL : " + requeteSql);

        ImportingDatabase.retrieveInsert(requeteSql.toString());

        nbInsertions++;

        for (Field field : fields) {
            try {
                if (field.getAnnotations().length != 0) {
                    annotation = field.getAnnotations()[0];
                    if (annotation instanceof Ignore)
                        continue;

                    if (annotation instanceof BeanList)
                        nbInsertions += bulkInsert((List<T>) field.get(bean));

                    else if (annotation instanceof idBeanExterne && field.get(bean) != null) {
                        if (VerifierExistenceBean(field.get(bean)))
                            nbInsertions += insert(field.get(bean));
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Impossible d'accéder au " + field.getName() + " du com.bean "
                        + tableBean);
                e.printStackTrace();
            }
        }

        return nbInsertions;
    }


    public static <T> int bulkInsert(List<T> listeBeans) throws SQLException, IllegalAccessException {
        int nbInsertions = 0;
        for (T bean : listeBeans) {
            if (VerifierExistenceBean(bean)) {
                nbInsertions += insert(bean);
            }
        }
        return nbInsertions;
    }

    private static <T> String getNomCol(T bean, String primaryKeyName) {//methode pour obtenir le nom de colonne de la table ou on veut
        //inserer nos valeurs
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
