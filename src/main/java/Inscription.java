import annotations.Bean;
import annotations.BeanIntern;
import annotations.Ignore;

import java.util.ArrayList;
import java.util.List;

@Bean(table="inscription", primaryKey = "inscriptionid", listMyInstance = "listeInscriptions")
public class Inscription {
    /******* Variables instances *********/
    private int inscriptionid;
    private int etudiantid;
    private int coursid;

    @BeanIntern
    private Cours unCours;

    @BeanIntern
    private Etudiant unEtudiant;

    @Ignore
    public static List<Inscription> listeInscriptions = new ArrayList<>();

    /************ Constructor ***********/
    public Inscription(int inscriptionid, int etudiantid, int coursid){
        this.inscriptionid = inscriptionid;
        this.etudiantid = etudiantid;
        this.coursid = coursid;
    }

    /********* Getter & Setter **********/
    public int getInscriptionid() {
        return inscriptionid;
    }

    public void setInscriptionid(int inscriptionid) {
        this.inscriptionid = inscriptionid;
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
}
