import annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImportingDatabase {// cette classe permet de faire l'importation de la base de données
    final String databaseURL = "jdbc:postgresql://localhost:5432/postgres";
    final String databaseUserName ="postgres";
    final String databaseUserPassword ="Heroes";
    static Connection con = null;

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public Connection connect(){
        try {
            con = DriverManager.getConnection(databaseURL, databaseUserName, databaseUserPassword);
            System.out.println("Connection completed");
        }catch(SQLException s){
            System.out.println("Echec de la connexion : " + s.getMessage());
        }
        return con;
    }

    // Methode pour arrêter la connexion
    public void close() throws SQLException {
        if(con != null) {
            con.close();
            System.out.println("Connection closed.");
        }
    }

    public static <T> List<T> retrieveSet(Class<T> beanClass, String sql) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        //initialisation d'une liste
        List<T> list = new ArrayList<>();

        //creation de la connexion
        Statement statement = con.createStatement();
        System.out.println("Requete SQL: \""+ sql+" \" ");

        //on execute la requete SQL a partir de la connexion
        ResultSet resultatQuery = statement.executeQuery(sql);

        T bean, tempBean;
        Annotation uneAnnotation;
        String className; // Obtenir le nom du Representant d'un object
        String nameTable = null; // Nom de la Table correspondant à la Proprieté <table> dans un @Bean
        String clef_Primaire; // Nom de la clé correspondant à la Proprieté <primaryKey> dans un @Bean
        int idClefPrimaire; // L'ID correspondant la valeur de la clé primaire sur le Bean dont on se situe.
        String request =null; // La Requete SQL produit pendant la recursivité

        //on parcourt les resultats de la requete
        while(resultatQuery.next()){

            bean = beanClass.newInstance();//on instancie un objet
            Field[] fields = beanClass.getDeclaredFields();//on recupere toutes les donnes qui ont ete encapsules

            for(Field field : fields){//on itere dans un tableau fields de champs (Field)

                field.setAccessible(true);

                //si le type du champ ou s'il s'agit d'un string ou encore Timestamp
                if(field.getType().isPrimitive() || field.getType().isInstance(new Timestamp(System.currentTimeMillis()))) {

                    int colonne = resultatQuery.findColumn(field.getName());
                    field.set(bean, resultatQuery.getObject(colonne));
                }

                else if(field.getAnnotations().length != 0){//si c'est egal a 0,le champ contient des annotations alors
                    uneAnnotation = field.getAnnotations()[0];
                    if(uneAnnotation instanceof Ignore) {continue;}

                    else if(uneAnnotation instanceof BeanList){

                        // Obtenir le representant la Classe qui contient data puis le nom de son Champ
                        className = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0].getTypeName();

                        try {
                            nameTable = Class.forName(className).getAnnotation(Bean.class).table();

                            clef_Primaire = beanClass.getAnnotation(Bean.class).primaryKey();
                            idClefPrimaire = (int) resultatQuery.getObject(resultatQuery.findColumn(clef_Primaire));
                            request = "SELECT * FROM " + nameTable + " WHERE " + clef_Primaire + " = " + idClefPrimaire;
                            field.set(bean, retrieveSet(Class.forName(className), request));

                        }catch (ClassNotFoundException e) {
                            System.out.print("La classe n'a pas ete trouvee.");
                            e.printStackTrace();
                        }

                    }


                    else if(uneAnnotation instanceof idBeanExterne){
                        className = field.getType().getName();
                        try {
                            nameTable = Class.forName(className).getAnnotation(Bean.class).table();
                            clef_Primaire = Class.forName(className).getAnnotation(Bean.class).primaryKey();
                            idClefPrimaire = (int) resultatQuery.getObject(resultatQuery.findColumn(clef_Primaire));

                            if((tempBean = VerifierExistence((Class<T>) Class.forName(className), idClefPrimaire )) != null){
                                    field.set(bean,tempBean);
                            }

                            else {
                                request = "SELECT * FROM " + nameTable + " WHERE "+ clef_Primaire + " = " + idClefPrimaire;
                                field.set(bean, retrieveSet(Class.forName(className), request).get(0));
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            String nomID = beanClass.getAnnotation(Bean.class).primaryKey();//on veut populer le nom de l'id
            Field id = beanClass.getDeclaredField(nomID);//on recupere un champ qui represente l'id de la classe courante

            id.setAccessible(true);

            if ((tempBean = VerifierExistence(beanClass, id.getInt(bean))) != null) {	//Vérifie l'existence du bean avec son ID
                list.add(tempBean);
            }

            else { list.add(bean);}
        }

        resultatQuery.close();
        statement.close();

        return list;

    }

    private static <T> T VerifierExistence(Class<T> beanClass, int unID) {

        String nomID = null;//on declare le nom de la liste de l'id qu'on veut recuperer
        String nomFieldList = null;//on declare le nom de la liste de field qu'on veut recuperer

        try {
            nomID = beanClass.getAnnotation(Bean.class).primaryKey();
            Field id = beanClass.getDeclaredField(nomID);
            id.setAccessible(true);

            //on recupere la liste listMyinstance() via les annotations
            nomFieldList = beanClass.getAnnotation(Bean.class).listMyInstance();
            Field fieldList = beanClass.getDeclaredField(nomFieldList);
            fieldList.setAccessible(true);

            List<T> uneListe = (ArrayList) fieldList.get(null);
            System.out.println("--->TEST : "+ beanClass.getSimpleName() + " " + unID);

            for(T bean: uneListe){
                if(fieldList.getInt(bean) == unID){
                    System.out.println("---->ID :" + unID + " existe deja");
                    return bean;
                }
            }

        } catch (NoSuchFieldException e) {
            System.out.println("Le representant de "+ nomID+" n'existe pas");
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            System.out.println("Le representant de"+ nomFieldList +" n'existe pas");
            e.printStackTrace();
        }

        System.out.println("---->ID :" + unID + " n'existe pas");

        return null;
    }


}
