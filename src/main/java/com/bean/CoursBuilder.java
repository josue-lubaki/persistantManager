package com.bean;

import com.database.ImportingDatabase;
import com.exception.CustomAccessException;
import com.Main;

import java.sql.SQLException;

public class CoursBuilder {
    private final Cours course;

    public CoursBuilder(){
        course = new Cours();
    }

    public Cours build(){
        return course;
    }

    public CoursBuilder setCoursid(int coursid){
        course.setCoursid(coursid);
        return this;
    }

    public CoursBuilder setNameCours(String nameCours){
        course.setNameCours(nameCours);
        return this;
    }

    public CoursBuilder setSigle(String sigle){
        course.setSigle(sigle);
        return this;
    }

    public CoursBuilder setDescription(String description){
        course.setDescription(description);
        return this;
    }

    /**
     * methode permettant la création d'un Cours tout en connaissant son coursid
     * @param coursid : l'ID du cours
     * @param nameCours : le nom du Cours
     * @param sigle : le sigle du Cours
     * @param description : la description du Cours
     * @return Cours
     * */
    public static Cours creation(int coursid, String nameCours, String sigle, String description){
        return new CoursBuilder()
                .setCoursid(coursid)
                .setNameCours(nameCours)
                .setSigle(sigle)
                .setDescription(description)
                .build();
    }

    /**
     * methode permettant la création d'un Cours avec la bonne valeur de (coursid) ID venant de la Base de données
     * @param nameCours : le nom du Cours
     * @param sigle : le sigle du Cours
     * @param description : la description du Cours
     * @return Cours
     * @throws CustomAccessException si le champs est inaccessible
     * @throws SQLException s'il y a erreur dans l'accès de la base de données
     * */
    public static Cours creation(String nameCours, String sigle, String description) throws CustomAccessException, SQLException {
        int idLibre = ImportingDatabase.ObtenirIndexSuivant("cours_seqs");
        return CoursBuilder.creation(idLibre,nameCours,sigle,description);
    }
}
