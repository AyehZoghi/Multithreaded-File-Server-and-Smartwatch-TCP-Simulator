import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WatchManager implements ClientManager{

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private int IMEI;


    public WatchManager(BufferedWriter bufferedWriter, BufferedReader bufferedReader, int IMEI) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.IMEI = IMEI;
    }
    @Override
    public void execCommand(String msg) throws IOException {
        if (msg.startsWith("3G*")) {
            LogManager.logActivity(msg);

            String[] parts = msg.split("[\\*\\s]");
            String command = parts[2];

            switch (command) {
                case "HEALTH" -> handleHealthData(parts);
                case "UD" -> handleLocationData(parts);
                default -> {
                    bufferedWriter.write("ERROR: Unknown command");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        }

    }
    private void handleHealthData(String[] parts) throws IOException {
        double heartRate = Double.parseDouble(parts[3]);
        double bloodPressureLow = Double.parseDouble(parts[4]);
        double bloodPressureHigh = Double.parseDouble(parts[5]);

        System.out.println("3G*"+IMEI+"*HEALTH, "+heartRate+", "+bloodPressureLow + ", " + bloodPressureHigh);
    }

    private void handleLocationData(String[] parts) throws IOException {
        double lat = Double.parseDouble(parts[3]);
        double lon = Double.parseDouble(parts[4]);
        System.out.println("3G*"+IMEI+"*UD, "+lat+", "+lon);
    }
}
