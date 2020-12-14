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

    @idBeanExterne
    public Cours unCours;

    @idBeanExterne
    public Etudiant unEtudiant;

    @Ignore
    public static final ArrayList<Inscription> listeInscriptions = new ArrayList<>();

    /************ Constructors ***********/
    public Inscription(){listeInscriptions.add(this);}
    public Inscription(int inscriptionid, int etudiantid, int coursid){
        this.inscriptionid = inscriptionid;
        this.etudiantid = etudiantid;
        this.coursid = coursid;
        listeInscriptions.add(this);
    }

    /********* Getter & Setter **********/
    public int getInscriptionid() {
        return inscriptionid;
    }

    public int getEtudiantid() {
        return etudiantid;
    }

    public void setEtudiantid(int etudiantid) {
        this.etudiantid = etudiantid;
    }

    public int getCoursid() {
        return coursid;
    }

    public void setCoursid(int coursid) {
        this.coursid = coursid;
    }

    public Cours getUnCours(){return unCours;}
    public Etudiant getUnEtudiant(){return unEtudiant;}
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
