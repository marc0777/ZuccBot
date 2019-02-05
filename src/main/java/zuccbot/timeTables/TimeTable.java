package zuccbot.timeTables;

public class TimeTable {
    private Couple[][] matrix;
    private String course;
    TimeTable(String course) {
        this.course =course;
        matrix= new Couple[6][6];
        for(int i=0;i<6;i++) {
            for (int k = 0; k < 6; k++) {
                matrix[i][k]=new Couple();
            }
        }
    }

    public void setmatrix(int a, int b,String c){
        //matrix[a][b]=c;
    }

    public Couple getmatrix(int a,int b){
        return matrix[a][b];
    }
    public String getcourse() {
        return course;
    }

    public void setcourse(String course) {
        this.course = course;
    }
}
