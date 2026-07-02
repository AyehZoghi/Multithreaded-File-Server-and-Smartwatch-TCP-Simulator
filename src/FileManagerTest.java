import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManagerTest {

    public static void main(String[] args) throws IOException {
        testCreateFile();
        testDeleteFile();
        testReadFile();
        testWriteFile();
    }

    public static void testCreateFile() throws IOException {
        // Mock BufferedReader and BufferedWriter
        BufferedReader reader = new BufferedReader(new StringReader(""));
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        // Initialize FileManager
        FileManager fileManager = new FileManager(writer, reader);

        // Test creating a file
        String fileName = "testCreateFile.txt";
        fileManager.execCommand("CREATE " + fileName);

        // Verify file creation
        Path filePath = Path.of(fileName);
        boolean fileExists = Files.exists(filePath);
        System.out.println("Test Create File: " + (fileExists ? "PASSED" : "FAILED"));

        // Clean up
        if (fileExists) {
            Files.delete(filePath);
        }
    }

    public static void testDeleteFile() throws IOException {
        // Mock BufferedReader and BufferedWriter
        BufferedReader reader = new BufferedReader(new StringReader(""));
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        // Initialize FileManager
        FileManager fileManager = new FileManager(writer, reader);

        // Create a file to delete
        String fileName = "testDeleteFile.txt";
        Path filePath = Path.of(fileName);
        Files.createFile(filePath);

        // Test deleting the file
        fileManager.execCommand("DELETE " + fileName);

        // Verify file deletion
        boolean fileExists = Files.exists(filePath);
        System.out.println("Test Delete File: " + (!fileExists ? "PASSED" : "FAILED"));
    }

    public static void testReadFile() throws IOException {
        // Mock BufferedReader and BufferedWriter
        BufferedReader reader = new BufferedReader(new StringReader(""));
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        // Initialize FileManager
        FileManager fileManager = new FileManager(writer, reader);

        // Create a file to read
        String fileName = "testReadFile.txt";
        Path filePath = Path.of(fileName);
        Files.writeString(filePath, "Hello, world!");

        // Test reading the file
        fileManager.execCommand("READ " + fileName);

        // Verify file content
        String fileContent = stringWriter.toString().trim();
        boolean contentMatches = "Hello, world!".equals(fileContent);
        System.out.println("Test Read File: " + (contentMatches ? "PASSED" : "FAILED"));

        // Clean up
        Files.delete(filePath);
    }

    public static void testWriteFile() throws IOException {
        // Mock BufferedReader and BufferedWriter
        BufferedReader reader = new BufferedReader(new StringReader(""));
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        // Initialize FileManager
        FileManager fileManager = new FileManager(writer, reader);

        // Create a file to write
        String fileName = "testWriteFile.txt";
        Path filePath = Path.of(fileName);
        Files.createFile(filePath);

        // Test writing to the file
        String content = "Goodbye, world!";
        fileManager.execCommand("WRITE " + fileName + " " + content);

        // Verify file content
        String fileContent = Files.readString(filePath).trim();
        boolean contentMatches = content.equals(fileContent);
        System.out.println(fileContent);
        System.out.println("Test Write File: " + (contentMatches ? "PASSED" : "FAILED"));

        // Clean up
        Files.delete(filePath);
    }
}
