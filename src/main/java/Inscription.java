import annotations.Bean;
import annotations.idBeanExterne;
import annotations.Ignore;

import java.util.ArrayList;
import java.util.List;

@Bean(table="inscription", primaryKey = "inscriptionid", listMyInstance = "listeInscriptions")
public class Inscription {
    /******* Variables instances *********/
    private int inscriptionid;
    private int etudiantid;
    private int coursid;

    @idBeanExterne(idbeanexterne = "coursid")
    private Cours unCours;

    @idBeanExterne(idbeanexterne = "etudiantid")
    private Etudiant unEtudiant;

    @Ignore
    public static List<Inscription> listeInscriptions = new ArrayList<>();

    /************ Constructor ***********/
    public Inscription(int etudiantid, int coursid){

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


    @Override
    public String toString() {
        return "Inscription : " + inscriptionid + " \t idetudiant: "  + etudiantid + "\t coursid: " + coursid;
    }
}
