package server;

import com.sun.net.httpserver.HttpServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    static TaskManager taskManager= new InMemoryTaskManager();

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        System.out.println("Сервер запущен на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен.");
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        Task task = new Task("Task", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Description2", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task3 = new Task("Task3", "Description3", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        server.start();
    }
}