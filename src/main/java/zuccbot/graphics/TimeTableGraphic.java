package zuccbot.graphics;

import zuccbot.db.Records;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TimeTableGraphic {
    private static int count = 0;

    public File printImage(Records[][] input) throws IOException {

        int width = 800;
        int height = 500;
        int columnWidth = width / 6;
        int cellHeight = height / 7;

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // fill all the image with white
        g2d.setColor(new Color(128, 243, 188));
        g2d.fillRect(0, 0, width, cellHeight);
        g2d.setColor(new Color(226, 252, 239));
        g2d.fillRect(0, cellHeight, width, height);

        //draw the grid
        g2d.setColor(Color.black);
        for (int i = 0; i < 7; i++) {
            g2d.drawLine(columnWidth * i, 0, columnWidth * i, height);
            for (int j = 0; j < 6; j++) {
                g2d.drawLine(0, cellHeight * i, width, cellHeight * i);
            }
        }
        String[] fLiine = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"};

        //prints the subject and the room
        for (int i = 0; i < 6; i++) {
            g2d.setFont(g2d.getFont().deriveFont(18.F));
            g2d.drawString(fLiine[i], (columnWidth * i) + 10, cellHeight - 25);
            g2d.setFont(g2d.getFont().deriveFont(12.F));
            for (int j = 0; j < 6; j++) {
                if (input[i][j] != null) {
                    if (input[i][j].getSubject().length() < 20) {
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
                        g2d.drawString(input[i][j].getSubject(), (columnWidth * i) + 5, (cellHeight * (j + 2)) - 30);
                        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
                    } else {
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
                        g2d.drawString(input[i][j].getSubject().substring(0, 12), (columnWidth * i) + 5, (cellHeight * (j + 2)) - 45);
                        g2d.drawString(input[i][j].getSubject().substring(12), (columnWidth * i) + 5, (cellHeight * (j + 2)) - 30);
                        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
                    }
                    g2d.drawString(input[i][j].getRoom().trim(), (columnWidth * i) + 5, (cellHeight * (j + 2)) - 13);
                }
            }
        }

        g2d.dispose();

        // Save as PNG
        File file = new File(count++ + ".png");
        ImageIO.write(bufferedImage, "png", file);

        return file;
    }
}
