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

    @idBeanExterne
    public Cours unCours;

    @idBeanExterne
    public Etudiant unEtudiant;

    @Ignore
    public static final ArrayList<Inscription> listeInscriptions = new ArrayList<>();

    /************ Constructors ***********/
    public Inscription(){listeInscriptions.add(this);}
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

    public Cours getUnCours(){return unCours;}
    public Etudiant getUnEtudiant(){return unEtudiant;}
    @Override
    public String toString() {
        return inscriptionid + " | etudiantid : " + etudiantid + " | coursid : " + coursid;
    }
}
