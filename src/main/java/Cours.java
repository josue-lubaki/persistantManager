import annotations.Bean;
import annotations.BeanList;
import annotations.Ignore;

import java.util.ArrayList;
import java.util.List;

@Bean(table ="cours", primaryKey="coursid",listMyInstance = "listeCours")
public class Cours {
    /********* Variables instances *********/
    private int coursid;
    private String nameCours;
    private String sigle;
    private String description;

    @Ignore
    public static List<Cours> listeCours = new ArrayList<>();

    @BeanList
    private List<Inscription> inscriptions = new ArrayList<>();

    /************* Constructor *************/
    public Cours( String nameCours, String sigle, String description){
        this.nameCours = nameCours;
        this.sigle = sigle;
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

    /*********** toString ****************/
    @Override
    public String toString() {
        return "Nom du Cours : " + nameCours + "\t sigle: " + sigle + " \t description" + description;
    }
}
