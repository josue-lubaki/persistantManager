public class Etudiant {
    /********* Variables instances *********/
    private int etudiantid;
    private String fname;
    private String lname;
    private int age;
    // Liste d'inscription à ajouter

    /*********** Constructor **************/
    public Etudiant(int etudiantid, String fname, String lname, int age){
        this.etudiantid = etudiantid;
        this.fname = fname;
        this.lname = lname;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
