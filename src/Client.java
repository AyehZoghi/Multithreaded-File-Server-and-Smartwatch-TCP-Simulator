import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write("CLIENT");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
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

                while (socket.isConnected()) {
                    try {
                        msg = bufferedReader.readLine();
                        System.out.println(msg);
                    } catch (IOException e) {
                        shutdownClient();
                    }
                }
            }
        }).start();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 12345);
        Client client = new Client(s);
        client.listenForMessage();
        client.sendMessage();
    }
}
