import java.sql.SQLException;

public class EtudiantBuilder {
    private Etudiant etudiant;

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

    public static Etudiant creation(int etudiantid, String fname, String lname, int age){
        return new EtudiantBuilder()
                .setEtudiantid(etudiantid)
                .setFname(fname)
                .setLname(lname)
                .setAge(age)
                .build();
    }

    public static Etudiant creation(String fname, String lname, int age) throws CustomAccessException, SQLException {
        ImportingDatabase c = new ImportingDatabase();
        c.connect();
        return EtudiantBuilder.creation(c.nextIndexInsert("etudiant_seqs"),fname,lname,age);
    }

}
