import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Server {

    private static final int THREAD_POOL_SIZE = 10;

    private ServerSocket serverSocket;
    private Map<Integer, ClientHandler> clientHandlers = new HashMap<>();
    private Lock clientHandlersLock = new ReentrantLock();


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {

        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        ExecutorService commandExecutor = Executors.newSingleThreadExecutor();
        try {
            commandExecutor.submit(() -> {
                Scanner scanner = new Scanner(System.in);
                while (!serverSocket.isClosed()) {
                    String command = scanner.nextLine();
                    String[] parts = command.split("\\*");
                    if (parts.length == 3) {
                        int IMEI = Integer.parseInt(parts[1]);
                        clientHandlersLock.lock();
                        ClientHandler handler = clientHandlers.getOrDefault(IMEI, null);
                        clientHandlersLock.unlock();
                        if (handler != null) {
                            handler.sendMessage(command);
                        } else {
                            System.out.println("Watch with IMEI " + IMEI + " not found.");
                        }
                    }
                }
            });
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New client accepted!");
                ClientHandler clientHandler = new ClientHandler(socket);

                threadPool.execute(clientHandler);
                if (clientHandler.getIMEI() != 0) {
                    clientHandlersLock.lock();
                    clientHandlers.put(clientHandler.getIMEI(), clientHandler);
                    clientHandlersLock.unlock();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            shutdownServer();
        }
    }

    public void shutdownServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdownServer();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(12345);
        Server server = new Server(s);
        server.startServer();
    }

}
