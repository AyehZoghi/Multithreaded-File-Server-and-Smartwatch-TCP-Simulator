import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.*;

public class FileManager implements ClientManager {
    private final Lock globalLock;
    private final Condition globalLockChange;
    private final Map<String, ReadWriteLock> filesLock;

    // TODO: maybe add a root_dir?

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public FileManager(BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.globalLock = new ReentrantLock();
        this.globalLockChange = globalLock.newCondition();
        this.filesLock = new HashMap<>();
    }

    public void readFile(String fileName, BufferedWriter out) throws IOException {
        acquireRead(fileName);
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                out.write("ERROR: File not found");
                out.newLine();
                out.flush();
                return;
            }
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = fileReader.readLine()) != null) {
                out.write(line);
                out.newLine();
            }
            fileReader.close();
            out.flush();
        } finally {
            releaseRead(fileName);
        }
    }

    public void writeFile(String fileName, String content, BufferedWriter out) throws IOException {
        acquireWrite(fileName);
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName, true));
            fileWriter.write(content);
            fileWriter.newLine();
            fileWriter.close();
            out.write(" Written in file successfully");
            out.newLine();
            out.flush();
        } finally {
            releaseWrite(fileName);
        }
    }

    public void createFile(String fileName, BufferedWriter out) throws IOException {
        acquireWrite(fileName);
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                out.write("File created: " + file.getName());
            } else {
                out.write("File already exists");
            }
            out.newLine();
            out.flush();
        } finally {
            releaseWrite(fileName);
        }
    }

    public void deleteFile(String fileName, BufferedWriter out) throws IOException {
        acquireWrite(fileName);
        try {
            File file = new File(fileName);
            if (file.delete()) {
                out.write("Deleted the file: " + file.getName());
            } else {
                out.write("Failed to delete the file.");
            }
            out.newLine();
            out.flush();
        } finally {
            releaseWrite(fileName);
        }
    }

    @Override
    public void execCommand(String msg) throws IOException{
        String[] p = msg.split(" ", 2);
        String command = p[0];
        String params = p.length > 1 ? p[1] : "";

        switch (command) {
            case "CREATE" -> createFile(params, bufferedWriter);
            case "DELETE" -> deleteFile(params, bufferedWriter);
            case "READ" -> readFile(params, bufferedWriter);
            case "WRITE" -> {
                String[] parsedParams = params.split(" ", 2);
                if (parsedParams.length == 2 && !parsedParams[0].isBlank()) {
                    String fileName = parsedParams[0];
                    String content = parsedParams[1];
                    writeFile(fileName, content, bufferedWriter);
                } else {
                    bufferedWriter.write("ERROR: Unknown command");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            default -> {
                bufferedWriter.write("ERROR: Unknown command");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
    }

    private void acquireRead(String fileName) {
        globalLock.lock();
        ReadWriteLock fileLock = getFileLock(fileName);
        fileLock.setReaders(fileLock.getReaders() + 1);
        if (fileLock.getReaders() == 1) lock(fileName);
        globalLock.unlock();
    }

    private void releaseRead(String fileName) {
        globalLock.lock();
        ReadWriteLock fileLock = getFileLock(fileName);
        fileLock.setReaders(fileLock.getReaders() - 1);
        if (fileLock.getReaders() == 0) unlock(fileName);
        globalLock.unlock();
    }

    private void acquireWrite(String fileName) {
        globalLock.lock();
        lock(fileName);
        globalLock.unlock();
    }

    private void releaseWrite(String fileName) {
        globalLock.lock();
        unlock(fileName);
        globalLock.unlock();
    }

    private ReadWriteLock getFileLock(String fileName) {
        return filesLock.computeIfAbsent(fileName, k -> new ReadWriteLock());
    }

    private void unlock(String fileName) {
        ReadWriteLock fileLock = getFileLock(fileName);
        fileLock.setLocked(false);
        globalLockChange.signalAll();
    }

    private void lock(String fileName) {
        ReadWriteLock fileLock = getFileLock(fileName);
        while (fileLock.isLocked()) {
            try {
                globalLockChange.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fileLock.setLocked(true);
    }
}