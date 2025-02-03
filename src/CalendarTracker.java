import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarTracker {
    private final Map<String, List<Task>> tasks;

    public CalendarTracker() {
        tasks = new HashMap<>();
    }

    // Add a task after validating conflicts
    public void addTask(String date, String taskId, String startTime, int duration, String description) throws Exception {
        Task newTask = new Task(taskId, startTime, duration, description);

        if (!validateTask(date, newTask)) {
            throw new Exception("Task " + taskId + " conflicts with existing tasks on " + date);
        }

        tasks.computeIfAbsent(date, k -> new ArrayList<>()).add(newTask);
        tasks.get(date).sort(Comparator.comparing(Task::getStartTime));
    }

    // Validate if the new task conflicts with existing tasks
    private boolean validateTask(String date, Task newTask) {
        if (!tasks.containsKey(date)) {
            return true;
        }

        for (Task task : tasks.get(date)) {
            if (task.overlapsWith(newTask)) {
                return false;
            }
        }
        return true;
    }

    // Load tasks from a file
    public void loadTasksFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
            }
        }
    }

    // Parse a task line and add it
    public void parseAndAddTask(String taskLine) throws Exception {
        String[] parts = taskLine.split(",", 5);
        if (parts.length != 5) {
            throw new Exception("Invalid task format: " + taskLine);
        }

        String date = parts[0].trim();
        String taskId = parts[1].trim();
        String startTime = parts[2].trim();
        int duration = Integer.parseInt(parts[3].trim());
        String description = parts[4].trim();

        addTask(date, taskId, startTime, duration, description);
    }

    // Display tasks for a specific day
    public List<Task> getTasksForDay(String date) {
        return tasks.getOrDefault(date, new ArrayList<>());
    }

    // Delete a specific task
    public boolean deleteTask(String date, String taskId) {
        if (tasks.containsKey(date)) {
            List<Task> dayTasks = tasks.get(date);
            if (dayTasks.removeIf(task -> task.getTaskId().equals(taskId))) {
                if (dayTasks.isEmpty()) {
                    tasks.remove(date);
                }
                return true;
            }
        }
        return false;
    }

    // Export all tasks to a file
    public void exportTasks(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String date : tasks.keySet()) {
                for (Task task : tasks.get(date)) {
                    writer.write(String.format("%s,%s,%s,%d,%s%n", date, task.getTaskId(), task.getStartTime(), task.getDuration(), task.getDescription()));
                }
            }
        }
    }

    // Task class (inner)
    static class Task {
        private final String taskId;
        private final String startTime;
        private final int duration;
        private final String description;

        public Task(String taskId, String startTime, int duration, String description) {
            this.taskId = taskId;
            this.startTime = startTime;
            this.duration = duration;
            this.description = description;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getStartTime() {
            return startTime;
        }

        public int getDuration() {
            return duration;
        }

        public String getDescription() {
            return description;
        }

        // Check for overlap with another task
        public boolean overlapsWith(Task other) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            try {
                Date thisStart = sdf.parse(this.startTime);
                Date thisEnd = new Date(thisStart.getTime() + this.duration * 60 * 1000);

                Date otherStart = sdf.parse(other.startTime);
                Date otherEnd = new Date(otherStart.getTime() + other.duration * 60 * 1000);

                return !(thisEnd.before(otherStart) || thisStart.after(otherEnd));
            } catch (ParseException e) {
                throw new RuntimeException("Invalid time format", e);
            }
                            }
                }
}
