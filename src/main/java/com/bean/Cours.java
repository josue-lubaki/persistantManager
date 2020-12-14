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
     * METHODE PERMETTANT DE RECHERCHER UN ELEMENT DANS LA LISTE DE COURS
     * Return true if found element
     * */
    public static boolean isContainedCourse(List<Cours> liste, int unID){
        for (Cours course : liste){
            if(course.getCoursid() == unID)
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return coursid + " | " + nameCours + " | " + description + " a pour Sigle : " + sigle;
    }

    public void setCoursid(int coursid) {
        this.coursid = coursid;
    }
}
