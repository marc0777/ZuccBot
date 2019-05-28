package zuccbot.timeTables;

public class Couple{
    private String Subject;
    private String Room;

    Couple(){
        Subject="";
        Room="";
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String Subject) {
        this.Subject = Subject;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String Room) {
        this.Room = Room;
    }
}

