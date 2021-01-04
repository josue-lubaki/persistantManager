package com.bean;

import com.annotations.Bean;
import com.annotations.BeanList;
import com.annotations.Ignore;

import java.util.ArrayList;
import java.util.List;

@Bean(table ="cours", primaryKey="coursid",listMyInstance = "listeCours")
public class Cours {

    /********* Variables instances *********/
    private int coursid;
    private String nameCours;
    private String sigle;
    private String description;

    @Ignore
    public static final ArrayList<Cours> listeCours = new ArrayList<>();

    @BeanList
    public ArrayList<Inscription> inscriptions = new ArrayList<>();

    public Cours(){
        listeCours.add(this);
    }

    /************ Getter & Setter **********/
    public int getCoursid() {
        return coursid;
    }

    public String getNameCours() {
        return nameCours;
    }

    public void setNameCours(String nameCours) {
        this.nameCours = nameCours;
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Methode permettant de s'assurer de l'existence du Cours dans une liste de Cours
     * @param liste : Liste contenant les cours
     * @param coursid : correspond au coursid du Cours dont on recherche
     * @return true if found element
     * */
    public static boolean isContainedCourse(List<Cours> liste, int coursid){
        for (Cours course : liste){
            if(course.getCoursid() == coursid)
                return true;
        }
        return false;
    }

    /** Methode permettant d'aller chercher un Cours dans la Liste des Cours
     * @param liste : Liste contenant les cours
     * @param coursid : correspond au coursid du Cours dont on recherche
     * @return Cours
     * */
    public static Cours askMeCourse(List<Cours> liste, int coursid){
        for (Cours e : liste)
            if(e.getCoursid() == coursid)
                return e;
        return null;
    }

    @Override
    public String toString() {
        return coursid + " | " + nameCours + " | " + description + " a pour Sigle : " + sigle;
    }

    public void setCoursid(int coursid) {
        this.coursid = coursid;
    }
}
