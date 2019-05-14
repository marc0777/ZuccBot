package zuccbot;

import java.io.IOException;

public class TimeTableTask implements Runnable{
    private String url;
    TimeTableTask(String url){
        this.url=url;
    }

    @Override
    public void run() {
        try {
            new TimeTableDeamon(url).chechkTimeTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
