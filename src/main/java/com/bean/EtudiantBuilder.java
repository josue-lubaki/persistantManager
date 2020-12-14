package com.bean;

import com.database.ImportingDatabase;
import com.exception.CustomAccessException;

import java.sql.SQLException;

public class EtudiantBuilder {
    private final Etudiant etudiant;

    public EtudiantBuilder(){
        etudiant = new Etudiant();
    }

    public Etudiant build(){
        return etudiant;
    }

    public EtudiantBuilder setEtudiantid(int etudiantid){
        etudiant.setEtudiantid(etudiantid);
        return this;
    }

    public EtudiantBuilder setFname(String fname){
        etudiant.setFname(fname);
        return this;
    }

    public EtudiantBuilder setLname(String lname){
        etudiant.setLname(lname);
        return this;
    }

    public EtudiantBuilder setAge(int age){
        etudiant.setAge(age);
        return this;
    }

    /**
     * methode permettant la création d'un Etudiant en connaissant son etudiantId
     * @param etudiantid : l'ID de l'Etudiant
     * @param fname : le prenom de l'étudiant
     * @param lname : le nom de l'étudiant
     * @param age : l'âge de l'étudiant
     * @return Etudiant
     * */
    public static Etudiant creation(int etudiantid, String fname, String lname, int age){
        return new EtudiantBuilder()
                .setEtudiantid(etudiantid)
                .setFname(fname)
                .setLname(lname)
                .setAge(age)
                .build();
    }

    /**
     * methode permettant la création d'un Etudiant avec la bonne valeur de (etudiantId) ID venant de la Base de données
     * @param fname : le prenom de l'étudiant
     * @param lname : le nom de l'étudiant
     * @param age : l'âge de l'étudiant
     * @return Etudiant
     * @throws CustomAccessException si le champs est inaccessible
     * @throws SQLException s'il y a erreur dans l'accès de la base de données
     * */
    public static Etudiant creation(String fname, String lname, int age) throws CustomAccessException, SQLException {
        int idLibre = ImportingDatabase.ObtenirIndexSuivant("etudiant_seqs");
        return EtudiantBuilder.creation(idLibre,fname,lname,age);
    }

}
