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
     * Methode permettant de Vérifier l'existence d'un élement de la liste
     * @param liste : Liste dont on veut recherchée l'élement
     * @param unID : correspond à l'etudiantid de l'Etudiant Recherché
     * Return true if found element
     * */
    public static boolean isContainedEtudiant(List<Etudiant> liste, int unID){
        for (Etudiant t : liste){
            if(t.getEtudiantid() == unID)
                return true;
        }
        return false;
    }

    /**
     * Methode permettant d'aller chercher un Etudiant dans la Liste des Etudiants
     * @param liste : La liste contenant les étudiants
     * @param etudiantid : L'etudiantid de l'Etudiant recherché
     * @return Etudiant
     * */
    public static Etudiant askMeStudent(List<Etudiant> liste, int etudiantid){
        for (Etudiant e : liste)
            if(e.getEtudiantid() == etudiantid)
                return e;
        return null;
    }

    @Override
    public String toString() {
        return "Etudiant : " + etudiantid + " - " + fname + " " + lname + " a " + age + " ans";
    }
}


