public class Inscription {
    /******* Variables instances *********/
    private int inscriptionid;
    private int etudiantid;
    private int coursid;
    // Ajouter deux instances : Cours et Etudiant

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
