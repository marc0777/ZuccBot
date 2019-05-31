package zuccbot.timeTables;

public class ClassSection {
    private int clas;
    private String section;

    public ClassSection(int clas, String section) {
        this.clas = clas;
        this.section = section;
    }

    public ClassSection() {

    }

    public int getClas() {
        return clas;
    }

    public String getSection() {
        return section;
    }

    public void setClas(int clas) {
        this.clas = clas;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
