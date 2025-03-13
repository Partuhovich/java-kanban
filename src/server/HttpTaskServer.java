package server;

import com.sun.net.httpserver.HttpServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
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
        HttpTaskServer server = new HttpTaskServer(new InMemoryTaskManager());
        server.start();
    }
}