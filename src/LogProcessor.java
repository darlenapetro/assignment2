import java.io.*;
import java.util.*;

public class LogProcessor {
    private final Map<String, Integer> ipRequestCount = new HashMap<>();
    private final Map<String, Long> routeResponseTime = new HashMap<>();
    private final Map<String, Integer> routeRequestCount = new HashMap<>();
    private final PriorityQueue<LogEntry> slowestRequests = new PriorityQueue<>(Comparator.comparingInt(o -> o.responseTime));

    private long totalRequests = 0;

    public void processLogFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = parseLogLine(line);
                if (entry == null) continue;

                totalRequests++;
                ipRequestCount.put(entry.clientIp, ipRequestCount.getOrDefault(entry.clientIp, 0) + 1);

                String route = entry.method + " " + entry.url;
                routeResponseTime.put(route, routeResponseTime.getOrDefault(route, 0L) + entry.responseTime);
                routeRequestCount.put(route, routeRequestCount.getOrDefault(route, 0) + 1);

                slowestRequests.add(entry);
                if (slowestRequests.size() > 10) slowestRequests.poll();
            }
        } catch (IOException e) {
            throw new IOException("Error reading log file: " + filePath, e);
        }
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public List<Map.Entry<String, Integer>> getTopClientIPs() {
        return ipRequestCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .toList();
    }

    public List<RouteStats> getTopRoutes() {
        return routeRequestCount.entrySet().stream()
                .map(entry -> new RouteStats(entry.getKey(),
                        entry.getValue(),
                        routeResponseTime.get(entry.getKey()) / (double) entry.getValue()))
                .sorted(Comparator.comparingDouble(RouteStats::getAverageResponseTime))
                .limit(10)
                .toList();
    }

    public List<LogEntry> getTopSlowestRequests() {
        return slowestRequests.stream()
                .sorted((a, b) -> b.responseTime - a.responseTime)
                .limit(10)
                .toList();
    }

    private LogEntry parseLogLine(String line) {
        try {
            String[] parts = line.split(" ");
            if (parts.length < 6) return null;

            String clientIp = parts[0];
            String method = parts[3];
            String url = parts[4];
            int responseTime = Integer.parseInt(parts[5]);

            return new LogEntry(clientIp, method, url, responseTime);
        } catch (Exception e) {
            System.err.println("Malformed log entry: " + line);
            return null;
        }
    }
}

class LogEntry {
    String clientIp;
    String method;
    String url;
    int responseTime;

    public LogEntry(String clientIp, String method, String url, int responseTime) {
        this.clientIp = clientIp;
        this.method = method;
        this.url = url;
        this.responseTime = responseTime;
    }

    @Override
    public String toString() {
        return clientIp + " " + method + " " + url + " " + responseTime + "ms";
    }
}

class RouteStats {
    String route;
    int requestCount;
    double averageResponseTime;

    public RouteStats(String route, int requestCount, double averageResponseTime) {
        this.route = route;
        this.requestCount = requestCount;
        this.averageResponseTime = averageResponseTime;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    @Override
    public String toString() {
        return route + ": " + requestCount + " requests, avg response time " + averageResponseTime+"ms";
        }
}
