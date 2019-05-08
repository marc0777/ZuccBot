package zuccbot.db;

import zuccbot.Constants;
import zuccbot.timeTables.PDFParsing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeTablesDB {
    private static TimeTablesDB singleton = null;

    public static TimeTablesDB getInstance() {
        if (singleton == null) singleton = new TimeTablesDB();
        return singleton;
    }

    private final Connection db;
    private final Logger logger;

    public TimeTablesDB() {
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        db = Database.getInstance();
    }

    public void addTimeTables(String url) throws IOException {

        String sql = "INSERT INTO TimeTable(class,section,day,hourNumber,subject,room) VALUES(?,?,?,?,?,?)";
        PreparedStatement pstmt;
        PDFParsing pdf = new PDFParsing(url);

        try {

            for(int i = 0; i < pdf.getexitTable().size();i++){
                for(int j = 0; j < 6; j++){
                    for(int k = 0; k < 6; k++){

                        pstmt = db.prepareStatement(sql);
                        pstmt.setInt(1, Integer.parseInt(pdf.getexitTable().get(i).getClas()));//class number
                        pstmt.setString(2, pdf.getexitTable().get(i).getCourse());//section
                        pstmt.setInt(3, j);//day
                        pstmt.setInt(4,k);//hourNumber
                        pstmt.setString(5,pdf.getexitTable().get(i).getMatrix(k,j).getSubject());//subject
                        pstmt.setString(6,pdf.getexitTable().get(i).getMatrix(k,j).getRoom());//room
                        if(!pdf.getexitTable().get(i).getMatrix(k,j).getRoom().equals("") & !pdf.getexitTable().get(i).getMatrix(k,j).getSubject().equals("")){
                            pstmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "TimeTablesDB: An exception has been caught while trying to add a TimeTable...", e);
        }
    }

    public Records[][] getDate(int clas, String section){

        String sql = "SELECT * FROM TimeTable WHERE class = ? AND section =?";
        ArrayList<Records> temp = new ArrayList<>();
        Records[][] timeTemp= new Records[6][6];

        for (int i=0;i<6;i++){
            timeTemp[i]=new Records[6];
        }
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, clas);
            pstmt.setString(2,section);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                temp.add(new Records(rs.getInt("class"),
                        rs.getString("section"),
                        rs.getInt("day"),
                        rs.getInt("hourNumber"),
                        rs.getString("subject"),
                        rs.getString("room")));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "TimeTables: An exception has been caught while trying to get the time table...", e);
        }
        Records a = new Records();
        for(int i =0; i<6;i++){
            for(int k =0;k<temp.size();k++){
                if(temp.get(k).getDay()==i){
                    timeTemp[i][temp.get(k).getHourNumber()]=temp.get(k);
                }
            }

        }
        return timeTemp;

    }
    public void printImage(Records[][] input) throws IOException {

        int width = 800;
        int height = 500;
        int columnWidth = width/6;
        int cellHeight=height/7;

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // fill all the image with white
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        //draw the grid
        g2d.setColor(Color.black);
        for (int i=0;i<7;i++){
            g2d.drawLine(columnWidth*i,width%width,columnWidth*i,height);
            for (int j=0;j<6;j++){
                g2d.drawLine(width%width,cellHeight*i,width,cellHeight*i);
            }
        }
        String[] fLiine={"Lunedì","Martedì","Mercoledì","Giovedì","Venerdì","Sabato"};


        for (int i=0;i<6;i++){
            g2d.drawString(fLiine[i],(columnWidth*i)+10,cellHeight-25);
            for(int j =0;j<6;j++){
                if(input[i][j]!=null)
                g2d.drawString(input[i][j].getSubject(),(columnWidth*i)+10,(cellHeight*(j+2))-25);
            }
        }

        g2d.dispose();

        // Save as PNG
        File file = new File("timeImage.png");
        ImageIO.write(bufferedImage, "png", file);
    }
}

