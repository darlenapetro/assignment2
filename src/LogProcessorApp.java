import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LogProcessorApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the log file path as a command-line argument.");
            return;
        }

        String filePath = args[0];
        LogProcessor logProcessor = new LogProcessor();

        try {
            System.out.println("Processing log file: " + filePath);
            logProcessor.processLogFile(filePath);

            System.out.println("\nTotal Requests: " + logProcessor.getTotalRequests());

            System.out.println("\nTop 10 Client IPs:");
            List<Map.Entry<String, Integer>> topIPs = logProcessor.getTopClientIPs();
            topIPs.forEach(entry -> {
                double percentage = (entry.getValue() * 100.0) / logProcessor.getTotalRequests();
                System.out.printf("%s: %d requests (%.2f%%)\n", entry.getKey(), entry.getValue(), percentage);
            });

            System.out.println("\nTop 10 Fastest Routes:");
            List<RouteStats> topRoutes = logProcessor.getTopRoutes();
            topRoutes.forEach(System.out::println);

            System.out.println("\nTop 10 Slowest Requests:");
            List<LogEntry> slowestRequests = logProcessor.getTopSlowestRequests();
            slowestRequests.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
                            }
                 }
}
