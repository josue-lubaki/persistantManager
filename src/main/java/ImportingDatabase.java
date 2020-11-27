import annotations.*;
import org.postgresql.core.SqlCommand;

import javax.xml.transform.Result;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImportingDatabase {// cette classe permet de faire l'importation de la base de données

    private Connection con = null;

    public void connect() {
        try {
            String databaseURL = "jdbc:postgresql://localhost:5432/postgres";
            String databaseUserName = "postgres";
            String databaseUserPassword = "tmtc";
            con = DriverManager.getConnection(databaseURL, databaseUserName, databaseUserPassword);
            System.out.println("Connection completed");
        } catch (SQLException s) {
            System.out.println("Echec de la connexion : " + s.getMessage());
        }
    }

    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException errorConnexion) {
                System.out.println("La Connexion n'a pu être établie");
                errorConnexion.printStackTrace();
            }
        }
    }

    public <T> List<T> retrieveSet(Class<T> beanClass, String sql) throws CustomAccessException{

        // initialisation d'une liste
        ArrayList<T> list = new ArrayList<>();
        try {
            // creation de la connexion
            Statement statement = con.createStatement();

            // on execute la requete SQL a partir de la connexion
            ResultSet resultatQuery = statement.executeQuery(sql);

            // Variables Utilisées
            T bean = null;
            T tempBean;
            Annotation uneAnnotation;
            String className; // Obtenir le nom du Representant d'un bean
            String nameTable; // Nom de la Table correspondant à la Proprieté <table> dans un @Bean
            String clef_Primaire; // Nom de la clé correspondant à la Proprieté <primaryKey> dans un @Bean
            int idClefPrimaire; // L'ID correspondant la valeur de la clé primaire sur le Bean dont on se situe.
            String request; // La Requete SQL produit pendant la recursivité
            int colonnes;

            //on parcourt les resultats de la requete (Table Data)
            while (resultatQuery.next()) {
                try {
                    bean = beanClass.newInstance();//on instancie un objet
                } catch (InstantiationException errorInstance) {
                    System.out.println("Impossible de créer une nouvelle instance de " + beanClass.getSimpleName() + "\n");
                    errorInstance.printStackTrace();
                }
                Field[] fields = beanClass.getDeclaredFields();//on recupere toutes les donnes qui ont ete encapsules

                for (Field field : fields) {//on itere dans un tableau fields de champs (Field)

                    field.setAccessible(true);
                    if (field.getAnnotations().length != 0) {//si c'est egal a 0,le champ contient des annotations alors
                        uneAnnotation = field.getAnnotations()[0];

                        if (uneAnnotation instanceof Ignore) {
                            continue;
                        }
                        if (uneAnnotation instanceof BeanList) {
                            try {
                                // Obtenir le representant la Classe qui contient data puis le nom de son Champ
                                className = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
                                nameTable = Class.forName(className).getAnnotation(Bean.class).table();
                                clef_Primaire = beanClass.getAnnotation(Bean.class).primaryKey();
                                idClefPrimaire = (int) resultatQuery.getObject(resultatQuery.findColumn(clef_Primaire));
                                request = "SELECT * FROM " + nameTable + " WHERE " + clef_Primaire + " = " + idClefPrimaire;
                                field.set(bean, retrieveSet(Class.forName(className), request));
                            } catch (ClassNotFoundException e) {
                                System.out.print("La classe n'a pas ete trouvee.");
                                e.printStackTrace();
                            }
                        }
                        if (uneAnnotation instanceof idBeanExterne) {
                            try {
                                className = field.getType().getName();
                                nameTable = Class.forName(className).getAnnotation(Bean.class).table();
                                clef_Primaire = Class.forName(className).getAnnotation(Bean.class).primaryKey();
                                idClefPrimaire = (int) resultatQuery.getObject(resultatQuery.findColumn(clef_Primaire));
                                tempBean = VerifierExistence((Class<T>) Class.forName(className), clef_Primaire);
                                if (tempBean != null) {
                                    field.set(bean, tempBean);
                                } else {
                                    request = "SELECT * FROM " + nameTable + " WHERE " + clef_Primaire + " = " + idClefPrimaire;
                                    field.set(bean, retrieveSet(Class.forName(className), request).get(0));
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (field.getType().isPrimitive() || field.getType().isInstance(new String())) {
                        colonnes = resultatQuery.findColumn(field.getName());
                        field.set(bean, resultatQuery.getObject(colonnes));
                    }
                }
                String nomID = beanClass.getAnnotation(Bean.class).primaryKey();//on veut populer le nom de l'id
                Field id = beanClass.getDeclaredField(nomID);//on recupere un champ qui represente l'id de la classe courante
                id.setAccessible(true);

                tempBean = VerifierExistence(beanClass, nomID);
                if (tempBean != null) {    //Vérifie l'existence du bean avec son ID
                    list.add(tempBean);
                } else {
                    list.add(bean);
                }
            }
            // Fermeture de la Requete et de sa liaison avec la Base de donnée
            resultatQuery.close();
            statement.close();
            return list;
        } catch (SQLException e) {
            System.out.println("Desolé, la Connexion à la Base de donnée n'a pas pu être établie\n");
            e.printStackTrace();
            return null;
        } catch ( IllegalAccessException | NoSuchFieldException e) {
            System.out.println("Impossible d'accèder à " + beanClass.getDeclaredFields().getClass().getName() + "\n");
            e.printStackTrace();
            return null;
        }
    }

    private static <T> T VerifierExistence(Class<T> beanClass, String unID) throws CustomAccessException, IllegalAccessException {
        String nomID = null;//on declare le nom de la liste de l'id qu'on veut recuperer
        String nomFieldList = null;//on declare le nom de la liste de field qu'on veut recuperer
        Field fieldList = null;
        try {
            nomID = beanClass.getAnnotation(Bean.class).primaryKey();
            Field id = beanClass.getDeclaredField(nomID);
            id.setAccessible(true);

            //on recuperes la liste listMyinstance() via les annotations
            nomFieldList = beanClass.getAnnotation(Bean.class).listMyInstance();
            fieldList = beanClass.getDeclaredField(nomFieldList);
            fieldList.setAccessible(true);

        } catch (NoSuchFieldException e) {
            System.out.println("Le representant de " + nomID + " n'existe pas");
            e.printStackTrace();


        }

            ArrayList<T> uneListe = (ArrayList<T>) fieldList.get(null);

            //System.out.println("--->TEST : " + beanClass.getSimpleName() + " " + unID);

            for (T bean : uneListe) {
                if (bean.getClass().getAnnotation(Bean.class).primaryKey().equals(unID)) { // TODO : Condition pas assez Cohérente
                    //System.out.println("---->ID :" + unID + " existe deja");
                    uneListe = new ArrayList<>();
                    return bean;
                }
            }

            uneListe = new ArrayList<>();

            return null;
    }


    public <T> int insert(T bean) throws SQLException {

        int nbInsertions = 0;//on retourne un nombre d'insertions
        String requeteSql = "INSERT INTO ";
        Class<?> representantBean = bean.getClass();//representant de la classe bean
        String tableBean = representantBean.getAnnotation(Bean.class).table();// on recupere la table du bean
        String primaryKeyName = representantBean.getAnnotation(Bean.class).primaryKey();//de meme pour la cle primaire

        requeteSql += tableBean + getNomCol(bean, primaryKeyName) + "VALUES (";
        Field[] fields = representantBean.getDeclaredFields();
        Annotation annotation;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals(primaryKeyName) || (field.getType().isPrimitive()
                    || field.getType().isInstance(new String()))) {
                try{
                    if(field.get(bean) instanceof Integer) {
                        requeteSql += field.get(bean) + ", ";

                    }
                    else {
                        requeteSql += "'" + field.get(bean) + "', ";

                    }
                } catch (IllegalAccessException e) {
                    System.out.println("Impossible d'accéder au " + field.getName() + " du bean "
                            + tableBean);
                    e.printStackTrace();
                }
            }
        }
        requeteSql = requeteSql.substring(0, requeteSql.length() - 2) + ")";
        System.out.println("SQL : " + requeteSql);

        Statement stat = con.createStatement();
        stat.execute(requeteSql);
        ResultSet resultats = stat.getResultSet();

        try {
            stat.close();
        } catch (SQLException e) {
            System.out.println("Impossible de fermer la requête SQL");
            e.printStackTrace();
        }
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
                        if (!VerifierExistence(field.get(bean)))
                            nbInsertions += insert(field.get(bean));
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Impossible d'accéder au " + field.getName() + " du bean "
                        + tableBean);
                e.printStackTrace();
            }
        }
        return nbInsertions;
    }


    public <T> int bulkInsert(List<T> listeBeans) throws SQLException, IllegalAccessException {
        int nbInsertions = 0;
        for (T bean : listeBeans) {
            if (!VerifierExistence(bean)) {
                nbInsertions += insert(bean);
            }
        }
        return nbInsertions;
    }

    public int ObtenirIndexActuel(String nameSequence) throws CustomAccessException, SQLException {
        Statement statement = con.createStatement();
        statement.execute("SELECT nextval('" + nameSequence + "'::regclass)");
        ResultSet rs = statement.getResultSet();
        if (rs.next()) {
            int numeroSuivant = rs.getInt(1) + 1;
            return numeroSuivant;
        }
        throw new CustomAccessException("Impossible de récupérer l'index courant : " + nameSequence);
    }


    private <T> String getNomCol(T bean, String primaryKeyName) {//methode pour obtenir le nom de colonne de la table ou on veut
        //inserer nos valeurs

        String nomCol = " (";
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getAnnotations().length == 0 && field.getName().equals(primaryKeyName) || (
                    field.getType().isPrimitive() || field.getType().isInstance(new String())))
                nomCol += field.getName() + ", "; // on ajoute le nom du champ tant que ce n'est pas une cle primaire
        }
        nomCol = nomCol.substring(0, nomCol.length() - 2) + ") ";// on enleve l'affichage de la virgule a la fin pour mettre une parenthese
        return nomCol;
    }


    private <T> void setValClePrimaire(T bean, String primaryKeyName, int primaryKeyNameValue) throws IllegalAccessException {
        Field[] fields = bean.getClass().getDeclaredFields();
        //try {
            for (Field field : fields) {
                if (field.getName().equals(primaryKeyName)) {
                    field.setAccessible(true);
                    field.setInt(bean, primaryKeyNameValue);
                    throw new CustomAccessException("Impossible d'insérer " + primaryKeyNameValue + " dans "
                            + primaryKeyName + " du Bean " + bean.getClass().getAnnotation(Bean.class).table());
                }
            }
        /*} catch (IllegalAccessException e) {
            System.out.println("Impossible d'insérer " + primaryKeyNameValue + " dans "
                    + primaryKeyName + " du Bean " + bean.getClass().getAnnotation(Bean.class).table());
        }*/
    }


    private <T> boolean VerifierExistence(T bean) throws IllegalAccessException {
        String nomClePrimaire = bean.getClass().getAnnotation(Bean.class).primaryKey();
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(nomClePrimaire)) {
                field.setAccessible(true);
                return  (field.getInt(bean)== 0);
            }
        }
        throw new CustomAccessException("Le Champ correspondant à " + nomClePrimaire + " est introuvable");

    }

}
