import zuccante.Parser;

public class PeriodicTask implements Runnable {
    @Override
    public void run() {
        Parser.parse();
        BatchSender.send();
    }
}
