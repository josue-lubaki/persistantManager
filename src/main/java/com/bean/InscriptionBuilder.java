package com.bean;

import com.database.ImportingDatabase;
import com.exception.CustomAccessException;

import java.sql.SQLException;

public class InscriptionBuilder {
    private static Inscription inscription;

    public InscriptionBuilder(){
        inscription = new Inscription();
    }

    public Inscription build(){
        return inscription;
    }

    public InscriptionBuilder setInscriptionid(final int inscriptionid){
        inscription.setInscriptionid(inscriptionid);
        return this;
    }

    public InscriptionBuilder setEtudiantid(final int etudiantid){
        inscription.setEtudiantid(etudiantid);
        return this;
    }

    public InscriptionBuilder setCoursid(final int coursid){
        inscription.setCoursid(coursid);
        return this;
    }

    /**
     * methode permettant la création d'un Inscription tout en connaissasnt son inscriptiond
     * @param etudiantid : ID de l'étudiant
     * @param coursid : ID du Cours
     * @return Inscription
     * @throws CustomAccessException si le champs est inaccessible
     * @throws SQLException s'il y a erreur dans l'accès de la base de données
     * */
    public static Inscription creation(int inscriptionid, int etudiantid, int coursid){
        return new InscriptionBuilder()
                .setInscriptionid(inscriptionid)
                .setEtudiantid(etudiantid)
                .setCoursid(coursid)
                .build();
    }

    /**
     * methode permettant la création d'un Inscription avec la bonne valeur de (Inscription) ID venant de la Base de données
     * @param etudiantid : ID de l'étudiant
     * @param coursid : ID du Cours
     * @return Inscription
     * @throws CustomAccessException si le champs est inaccessible
     * @throws SQLException s'il y a erreur dans l'accès de la base de données
     * */
    public static Inscription creation(int etudiantid, int coursid) throws SQLException, CustomAccessException {
        int idLibre = ImportingDatabase.ObtenirIndexSuivant("inscription_seqs");
        return InscriptionBuilder.creation(idLibre,etudiantid,coursid);
    }

}
