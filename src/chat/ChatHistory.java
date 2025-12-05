package chat;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatHistory {
    private static final String FILE = "chat_history.txt";

    public static void addMessage(String from, String text) {
        String line = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " [" + from + "] " + text + System.lineSeparator();
        try {
            Files.write(Paths.get(FILE), line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    public static void showHistory() {
        System.out.println("\n=== Chat History ===");
        try {
            Files.readAllLines(Paths.get(FILE)).forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("(no history yet)");
        }
        System.out.println("==================\n");
    }
}