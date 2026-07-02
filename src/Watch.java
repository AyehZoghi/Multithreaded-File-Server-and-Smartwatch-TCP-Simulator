import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Watch {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private GeoPoint currentLoc;
    private static Random random = new Random();

    private int IMEI;

    public Watch(Socket socket, int IMEI) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.IMEI = IMEI;
            this.currentLoc = new GeoPoint(Math.random() * 180 - 90, Math.random() * 360 - 180);
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write("WATCH " + IMEI);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (!socket.isClosed()) {
                String msg = scanner.nextLine();
                bufferedWriter.write(msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;

                while (!socket.isClosed()) {
                    try {
                        msg = bufferedReader.readLine();
                        System.out.println(msg);
                        if (msg != null) {
                            handleServerCommand(msg);
                        }
                    } catch (IOException e) {
                        shutdownClient();
                    }
                }
            }
        }).start();
    }

    public void handleServerCommand(String msg) {
        if (msg.equals("3G*" + IMEI + "*POWEROFF")) {
            shutdownClient();

        } else if (msg.equals("3G*" + IMEI + "*FIND")) {
            System.out.println("Watch is ringing for 30 seconds");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void shutdownClient() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Watch with IMEI " + IMEI + " powered off.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Set<Integer> usedImeis = new HashSet<>();

    public static int generateUniqueImei() {
        int IMEI;
        do {
            IMEI = 1000000000 + random.nextInt(900000000);
        } while (usedImeis.contains(IMEI));
        usedImeis.add(IMEI);
        return IMEI;
    }

    private void updateLocation() {
        double lat = currentLoc.getLat() + Math.random() * 0.0001 - 0.00005;
        double lon = currentLoc.getLon() + Math.random() * 0.0001 - 0.00005;
        currentLoc.setLat(lat);
        currentLoc.setLon(lon);
    }

    public void startLocationService() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.execute(() -> {
            while (!socket.isClosed()) {
                updateLocation();
                try {
                    bufferedWriter.write("3G*" + IMEI + "*UD " + currentLoc.getLat() + " " + currentLoc.getLon());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    Thread.sleep(45000);
                } catch (IOException | InterruptedException e) {
                    shutdownClient();
                }
            }
        });
    }

    public int getHealthParameter() {
        return 80 + random.nextInt(40);
    }

    public void startHealthService() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.execute(() -> {
            while (!socket.isClosed()) {
                try {
                    bufferedWriter.write("3G*" + IMEI + "*HEALTH " + getHealthParameter() + " " + getHealthParameter() + " " + getHealthParameter());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    Thread.sleep(30000);
                } catch (IOException | InterruptedException e) {
                    shutdownClient();
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 12345);
        int IMEI = generateUniqueImei();
        Watch watch = new Watch(s, IMEI);
        watch.listenForMessage();
        watch.startLocationService();
        watch.startHealthService();
        watch.sendMessage();
    }
}
