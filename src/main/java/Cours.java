import annotations.Bean;
import annotations.BeanList;
import annotations.Ignore;

import java.util.ArrayList;

@Bean(table ="cours", primaryKey="coursid",listMyInstance = "listeCours")
public class Cours {
    /********* Variables instances *********/
    private int coursid;
    private String nameCours;
    private String sigle;
    private String description;

    @Ignore
    public static final ArrayList<Cours> listeCours = new ArrayList<>();

    @BeanList
    public ArrayList<Inscription> inscriptions = new ArrayList<>();

    /************* Constructors *************/
    public Cours( String nameCourse, String single, String description){
        this.nameCours = nameCourse;
        this.sigle = single;
        this.description = description;
        listeCours.add(this);
    }

    public Cours(){
        listeCours.add(this);
    }

    /************ Getter & Setter **********/
    public int getCoursid() {
        return coursid;
    }

    public String getNameCours() {
        return nameCours;
    }

    public void setNameCours(String nameCours) {
        this.nameCours = nameCours;
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return coursid + " | " + nameCours + " | " + description + " a pour Sigle : " + sigle;
    }
}
