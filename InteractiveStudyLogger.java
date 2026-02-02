import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.*;

public class InteractiveStudyLogger {
    private static final Logger logger = Logger.getLogger("StudyLog");
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();

    static {
        try {
            FileHandler fh = new FileHandler("study_interactive.log", true);
            fh.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format("[%s] %s%n", LocalDateTime.now(), lr.getMessage());
                }
            });
            logger.addHandler(fh);
        } catch (IOException e) { System.err.println("Log setup failed."); }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 2026 Focus Mode Active ===");
        System.out.println("Instructions:");
        System.out.println("1. When distracted, type the reason and press ENTER.");
        System.out.println("2. When back to work, press ENTER again.");
        System.out.println("3. Type 'exit' to quit.");

        while (true) {
            System.out.print("\nWaiting for distraction (or type 'exit'): ");
            String reason = scanner.nextLine();

            if (reason.equalsIgnoreCase("exit")) break;
            if (reason.isEmpty()) continue;

            // Start Timing
            long start = System.currentTimeMillis();
            System.out.println(">>> Tracking: " + reason);
            System.out.print(">>> PRESS ENTER WHEN YOU ARE BACK TO STUDYING...");

            scanner.nextLine(); // Wait for user to return

            // End Timing
            long duration = System.currentTimeMillis() - start;
            String timeStr = formatDuration(duration);

            logExecutor.submit(() -> {
                logger.info(String.format("DISTRACTION: %s | DURATION: %s", reason, timeStr));
                System.out.println("✔ Logged: " + reason + " for " + timeStr);
            });
        }

        logExecutor.shutdown();
        System.out.println("Session Summary saved to study_interactive.log");
    }

    private static String formatDuration(long ms) {
        long s = (ms / 1000) % 60;
        long m = (ms / (1000 * 60)) % 60;
        return String.format("%02dm %02ds", m, s);
    }
}
