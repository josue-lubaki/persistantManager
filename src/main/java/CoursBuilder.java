import java.sql.SQLException;

public class CoursBuilder {
    private Cours course;

    public CoursBuilder(){
        course = new Cours();
    }

    public Cours build(){
        return course;
    }

    public CoursBuilder setCoursid(int coursid){
        course.setCoursid(coursid);
        return this;
    }

    public CoursBuilder setNameCours(String nameCours){
        course.setNameCours(nameCours);
        return this;
    }

    public CoursBuilder setSigle(String sigle){
        course.setSigle(sigle);
        return this;
    }

    public CoursBuilder setDescription(String description){
        course.setDescription(description);
        return this;
    }

    public static Cours creation(int coursid, String nameCours, String sigle, String description){
        return new CoursBuilder()
                .setCoursid(coursid)
                .setNameCours(nameCours)
                .setSigle(sigle)
                .setDescription(description)
                .build();
    }

    public static Cours creation(String nameCours, String sigle, String description) throws CustomAccessException, SQLException {
        ImportingDatabase c = new ImportingDatabase();
        c.connect();
        return CoursBuilder.creation(c.nextIndexInsert("cours_seqs"),nameCours,sigle,description);
    }
}
