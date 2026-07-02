import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogManager {
    private static final Lock logLock = new ReentrantLock();
    private static final String LOG_FILE = "server_watch_log.txt";

    public static void logActivity(String msg) {
        logLock.lock();
        try {
            FileWriter fileWriter = new FileWriter(LOG_FILE, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logLock.unlock();
        }
    }
    
}
