package com.bean;

import com.annotations.Bean;
import com.annotations.BeanList;
import com.annotations.Ignore;

import java.util.ArrayList;
import java.util.List;

@Bean(table = "etudiant", primaryKey = "etudiantid", listMyInstance = "listeEtudiants")
public class Etudiant {
    /********* Variables instances *********/
    private int etudiantid;
    private String fname;
    private String lname;
    private int age;

    @Ignore
    public static final ArrayList<Etudiant> listeEtudiants = new ArrayList<>();

    @BeanList
    public ArrayList<Inscription> inscriptions = new ArrayList<>();

    public Etudiant(){
        listeEtudiants.add(this);
    }

    /********** Getter & Setter ***********/
    public int getEtudiantid() {
        return etudiantid;
    }

    public void setEtudiantid(int etudiantid) {
        this.etudiantid = etudiantid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * METHODE PERMETTANT DE RECHERCHER UN ELEMENT DANS LA LISTE
     * Return true if found element
     * */
    public static boolean isContainedEtudiant(List<Etudiant> liste, int unID){
        for (Etudiant t : liste){
            if(t.getEtudiantid() == unID)
                return true;
        }
        return false;
    }

    @Override
    public String toString() {

        return "Etudiant : " + etudiantid + " - " + fname + " " + lname + " a " + age + " ans";
    }
}


