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

    public static Inscription creation(int inscriptionid, int etudiantid, int coursid){
        return new InscriptionBuilder()
                .setInscriptionid(inscriptionid)
                .setEtudiantid(etudiantid)
                .setCoursid(coursid)
                .build();
    }

    public static Inscription creation(int etudiantid, int coursid) throws CustomAccessException, SQLException {
        ImportingDatabase c = new ImportingDatabase();
        c.connect();
        return InscriptionBuilder.creation(c.nextIndexInsert("inscription_seqs"),etudiantid,coursid);
    }

}
