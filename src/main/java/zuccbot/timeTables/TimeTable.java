package zuccbot.timeTables;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * the course variable is an array with 2 positions that contains the year in position 0 and the course in position 1
 */
public class TimeTable {
    private Couple[][] matrix;
    private String[] course;
    TimeTable(String course) {
        this.course = new String[2];
        this.course[0] = course.substring(0,1);
        this.course[1] = course.substring(1);
        matrix= new Couple[6][6];
        for(int i=0;i<6;i++) {
            for (int k = 0; k < 6; k++) {
                matrix[i][k]=new Couple();
            }
        }
    }


    public Couple getMatrix(int a,int b){
        return matrix[a][b];
    }

    public String getCourse(){
        return  course[1];
    }
    public String getClas(){
        return course[0];
    }

    public void setCourse(String a){
        course[1]= a;
    }

}
