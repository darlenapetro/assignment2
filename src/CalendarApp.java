import java.util.Scanner;

public class CalendarApp {
    public static void main(String[] args) {
        CalendarTracker calendar = new CalendarTracker();
        Scanner scanner = new Scanner(System.in);

        // Load tasks from file if filename is passed as an argument
        if (args.length > 0) {
            try {
                calendar.loadTasksFromFile(args[0]);
                System.out.println("Tasks loaded successfully from file.");
            } catch (Exception e) {
                System.out.println("Error loading tasks: " + e.getMessage());
            }
        }

        System.out.println("Calendar Tracker Application");
        System.out.println("Commands: quit, display <day>, add <task>, delete <day>,<task_id>, export <filename>");

        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("quit")) {
                System.out.println("Exiting application.");
                break;
            }

            try {
                if (command.startsWith("display ")) {
                    String day = command.substring(8).trim();
                    var tasks = calendar.getTasksForDay(day);
                    if (tasks.isEmpty()) {
                        System.out.println("No tasks for " + day);
                    } else {
                        System.out.println("Tasks for " + day + ":");
                        for (var task : tasks) {
                            System.out.printf("  %s: %s (%d mins) - %s%n", task.getTaskId(), task.getStartTime(), task.getDuration(), task.getDescription());
                        }
                    }
                } else if (command.startsWith("add ")) {
                    String taskDetails = command.substring(4).trim();
                    calendar.parseAndAddTask(taskDetails);
                    System.out.println("Task added successfully.");
                } else if (command.startsWith("delete ")) {
                    String[] parts = command.substring(7).split(",", 2);
                    if (parts.length == 2) {
                        String day = parts[0].trim();
                        String taskId = parts[1].trim();
                        if (calendar.deleteTask(day, taskId)) {
                            System.out.println("Task deleted successfully.");
                        } else {
                            System.out.println("Task not found.");
                        }
                    } else {
                        System.out.println("Invalid delete command.");
                    }
                } else if (command.startsWith("export ")) {
                    String fileName = command.substring(7).trim();
                    calendar.exportTasks(fileName);
                    System.out.println("Tasks exported successfully to " + fileName);
                } else {
                    System.out.println("Invalid command.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
                }
}
