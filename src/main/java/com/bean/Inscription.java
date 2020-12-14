package com.bean;

import com.annotations.Bean;
import com.annotations.idBeanExterne;
import com.annotations.Ignore;

import java.util.ArrayList;

@Bean(table="inscription", primaryKey = "inscriptionid", listMyInstance = "listeInscriptions")
public class Inscription {
    /******* Variables instances *********/
    private int inscriptionid;
    private int etudiantid;
    private int coursid;

    @Ignore
    public static final ArrayList<Inscription> listeInscriptions = new ArrayList<>();

    @idBeanExterne
    public Cours unCours;

    @idBeanExterne
    public Etudiant unEtudiant;

    /************ Constructors ***********/
    public Inscription(){listeInscriptions.add(this);}

    /********* Getter **********/
    public Cours getUnCours(){return unCours;}

    public Etudiant getUnEtudiant(){return unEtudiant;}

    /********* Setter **********/
    public void setEtudiantid(int etudiantid) {
        this.etudiantid = etudiantid;
    }

    public void setCoursid(int coursid) {
        this.coursid = coursid;
    }

    public void setInscriptionid(int inscriptionid) {
        this.inscriptionid = inscriptionid;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.unEtudiant = etudiant;
    }

    public void setCours(Cours cours) {
        this.unCours = cours;
    }

    @Override
    public String toString() {
        return inscriptionid + " | etudiantid : " + etudiantid + " | coursid : " + coursid;
    }



}
