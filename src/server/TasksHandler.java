package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] pathSplit = exchange.getRequestURI().getPath().split("/");
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
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
                                writeResponse(exchange, "Задача с ID " + taskId + " не найдена.", 404);
                            }
                        } else {
                            writeResponse(exchange, "Некорректный путь запроса.", 400);
                        }
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "Некорректный ID задачи.", 400);
                    } catch (Exception e) {
                        writeResponse(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
                    }
                    break;
                case "POST":
                    try {
                        if (pathSplit.length == 2) {
                            InputStream inputStream = exchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            JsonElement jsonElement = JsonParser.parseString(body);
                            if (!jsonElement.isJsonObject()) {
                                throw new IllegalArgumentException("Тело запроса должно быть JSON-объектом.");
                            }

                            Task task = gson.fromJson(body, Task.class);
                            taskManager.createTask(task);
                            writeResponse(exchange, "Задача успешно создана.", 201);

                        } else if (pathSplit.length == 3) {
                            InputStream inputStream = exchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            JsonElement jsonElement = JsonParser.parseString(body);
                            if (!jsonElement.isJsonObject()) {
                                throw new IllegalArgumentException("Тело запроса должно быть JSON-объектом.");
                            }

                            Task task = gson.fromJson(body, Task.class);
                            taskManager.updateTask(task, getId(exchange));
                            writeResponse(exchange, "Задача успешно создана.", 201);
                        }
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Некорректный JSON: " + e.getMessage(), 400);
                    } catch (Exception e) {
                        writeResponse(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
                    }
                    break;
                case "DELETE":
                    taskManager.deleteTaskById(getId(exchange));
                    sendText(exchange, "Задача успешно удалена!", 200);
                    break;
                default:

            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
