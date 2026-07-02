import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private int IMEI;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private ClientManager clientManager;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String initMsg = bufferedReader.readLine();
            System.out.println(initMsg);
            if (initMsg.equals("CLIENT")) {
                this.clientManager = new FileManager(bufferedWriter, bufferedReader);
                System.out.println("Client connected");
            } else if (initMsg.startsWith("WATCH")) {
                String[] parts = initMsg.split(" ");
                if (parts.length == 2) {
                    this.IMEI = Integer.parseInt(parts[1]);
                    this.clientManager = new WatchManager(bufferedWriter, bufferedReader, IMEI);
                    System.out.println("Watch connected with IMEI " + IMEI);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdownHandler();
        }
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            shutdownHandler();
        }
    }

    public int getIMEI(){
        return IMEI;
    }

    public void shutdownHandler() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String msg;

        while (!socket.isClosed()) {
            try {
                msg = bufferedReader.readLine();

                if (msg == null) return;

                clientManager.execCommand(msg);
            } catch (IOException e) {
                e.printStackTrace();
                shutdownHandler();
                break;
            }
        }
    }
}
