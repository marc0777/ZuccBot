package zuccbot.timeTables;

import com.google.gson.Gson;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import technology.tabula.CommandLineApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import static technology.tabula.CommandLineApp.buildOptions;

public class PDFParsing {
    private String url;
    private ArrayList<TimeTable> exitTable;

    public PDFParsing(String link) throws IOException {
        url = link;
        this.parse();
    }

    private void parse() throws IOException {

        readJ();
        clearj();
        //deliting the file fileProva to prevent errors

        File file = new File("fileProva");

        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }

    }

    //reading the PDF and converting to Json
    private void readJ() throws IOException {

        //reading PDF and parsing to Json into the file fileProva
        Files.copy(new URL(url).openStream(), Paths.get("fileProva"));
        String[] a = new String[4];
        a[0] = "fileProva";
        a[1] = "-o" + "output.json";
        a[2] = "-p" + "all";
        a[3] = "-f" + "JSON";


        DefaultParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(buildOptions(), a);
            (new CommandLineApp(System.out, line)).extractTables(line);
        } catch (org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method clears the JSON object in order to get only useful informations
     *
     * @throws FileNotFoundException
     */
    private void clearj() throws FileNotFoundException {
        Scanner input = new Scanner(new FileReader("output.json"));
        StringBuilder temp = new StringBuilder();
        while (input.hasNextLine()) temp.append(input.nextLine());
        Gson gson = new Gson();
        Time[] listed = gson.fromJson(temp.toString(), Time[].class);
        //putting the Gson ogject information into a java object
        exitTable = copy(listed);
        //removing all the string issues
        replacement(exitTable);
    }

    /**
     * this method takes the Java object created from the Json and it takes only the usefull information from it and puts them into an arraylist of timetables
     *
     * @param list
     * @return returns a list of time tables with the name of the course
     */
    private ArrayList<TimeTable> copy(Time[] list) {
        ArrayList<TimeTable> temp = new ArrayList<>();
        for (Time time : list) {
            //getting the class name from the Gson and creating a new object with that class name in it
            temp.add(new TimeTable(time.getData()[0][1].getText().split(" ")[1]));
        }
        for (int i = 0; i < list.length; i++) {
            int l = 1;
            for (int k = 2; k < 8; k++) {
                for (int j = 1; j < 7; j++) {
                    if (j == 6) {
                        j = 7;
                        l = 2;
                    }
                    temp.get(i).getMatrix((k - 2), (j - l)).setSubject(list[i].getData()[k][j].getText().split("-")[0].trim());
                    if ((list[i].getData()[k][j]).getText().split("-").length > 1) {
                        temp.get(i).getMatrix((k - 2), (j - l)).setRoom(list[i].getData()[k][j].getText().split("-")[1].trim());
                    }
                    l = 1;
                }
            }
        }
        return temp;
    }

    /**
     * this method replaces the \rLUNEDÌMARTEDÌMERCOLEDÌGIOVEDÌVENERDÌSABATO string with nothing to remove it
     *
     * @param list
     */
    private void replacement(ArrayList<TimeTable> list) {
        String temp = "";
        for (TimeTable timeTable : list) {
            timeTable.setCourse(timeTable.getCourse().replace("\rLUNEDÌMARTEDÌMERCOLEDÌGIOVEDÌVENERDÌSABATO", ""));
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++) {
                    timeTable.getMatrix(j, k).setSubject(timeTable.getMatrix(j, k).getSubject().replace("\r", ""));
                    timeTable.getMatrix(j, k).setRoom(timeTable.getMatrix(j, k).getRoom().replace("\r", ""));
                }
            }
        }
    }

    public ArrayList<TimeTable> getexitTable() {
        return exitTable;
    }
}