package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Epic;
import tasks.Task;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] pathSplit = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET":
                    try {
                        if (pathSplit.length == 2) {
                            ArrayList<Task> tasks = taskManager.getTasks();
                            String jsonResponse = gson.toJson(tasks);
                            writeResponse(exchange, jsonResponse, 200);

                        } else if (pathSplit.length == 3) {
                            int taskId = getId(exchange);
                            Task task = taskManager.getTaskById(taskId);
                            if (task != null) {
                                String jsonResponse = gson.toJson(task);
                                writeResponse(exchange, jsonResponse, 200);
                            } else {
                                sendText(exchange, "Задача с ID " + taskId + " не найдена.", 404);
                            }
                        } else {
                            sendText(exchange, "Некорректный путь запроса.", 400);
                        }
                    } catch (NumberFormatException e) {
                        sendText(exchange, "Некорректный ID задачи.", 400);
                    } catch (NoSuchElementException e) {
                        sendText(exchange, "Ошибка нахождения элемента: " + e.getMessage(), 404);
                    } catch (Exception e) {
                        sendText(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
                    }
                    break;
                case "POST":
                    try {
                        if (pathSplit.length == 2) {
                            String body = parseTaskFromRequest(exchange);
                            Task task = gson.fromJson(body, Task.class);
                            taskManager.createTask(task);
                            sendText(exchange, "Задача успешно создана.", 201);

                        } else if (pathSplit.length == 3) {
                            String body = parseTaskFromRequest(exchange);
                            Task task = gson.fromJson(body, Task.class);
                            taskManager.updateTask(task, getId(exchange));
                            sendText(exchange, "Задача успешно создана.", 201);
                        }
                    } catch (IllegalArgumentException e) {
                        sendText(exchange, "Ошибка запроса: " + e.getMessage(), 400);
                    } catch (NoSuchElementException e) {
                        sendText(exchange, "Ошибка нахождения элемента: " + e.getMessage(), 404);
                    } catch (IllegalStateException e) {
                        sendText(exchange, "Ошибка пересечения: " + e.getMessage(), 406);
                    } catch (Exception e) {
                        sendText(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
                    }
                    break;
                case "DELETE":
                    taskManager.deleteTaskById(getId(exchange));
                    sendText(exchange, "Задача успешно удалена!", 200);
                    break;
                default:
                    sendText(exchange, "Необрабатываемый метод", 400);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }

    }
}
