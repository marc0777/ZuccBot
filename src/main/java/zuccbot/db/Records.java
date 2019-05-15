package zuccbot.db;

public class Records{
    private int clas;
    private String section;
    private int day;
    private int hourNumber;
    private String subject;
    private String room;

    public Records(int clas, String section, int day, int hourNumber, String subject, String room){
        this.clas= clas;
        this.section=section;
        this.day=day;
        this.hourNumber=hourNumber;
        this.subject=subject;
        this.room=room;
    }

    public Records(){
        this.clas=0;
        this.section="";
        this.day=-1;
        this.hourNumber=-1;
        this.subject="";
        this.room="";
    }

    public int getClas() {
        return clas;
    }

    public String getSection() {
        return section;
    }

    public int getDay() {
        return day;
    }

    public int getHourNumber() {
        return hourNumber;
    }

    public String getSubject() {
        return subject;
    }

    public String getRoom() {
        return room;
    }

    public String buildMessage() {
        return (getHourNumber() + 1) + "ᵃ ora " + getSubject() + " in " + getRoom();
    }
}
