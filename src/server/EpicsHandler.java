package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager taskManager) {
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
                            ArrayList<Epic> epics = taskManager.getEpics();
                            String jsonResponse = gson.toJson(epics);
                            writeResponse(exchange, jsonResponse, 200);

                        } else if (pathSplit.length == 3) {
                            int epicId = getId(exchange);
                            Epic epic = taskManager.getEpicByID(epicId);
                            String jsonResponse = gson.toJson(epic);
                            writeResponse(exchange, jsonResponse, 200);

                        } else if (pathSplit.length == 4) {
                            int epicId = getId(exchange);
                            Epic epic = taskManager.getEpicByID(epicId);
                            ArrayList<SubTask> subTasks = epic.getEpicSubTasks(taskManager.getSubTasks());
                            String jsonResponse = gson.toJson(subTasks);
                            writeResponse(exchange, jsonResponse, 200);
                        } else {
                            sendText(exchange, "Некорректный путь запроса.", 400);
                        }
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
                            Epic epic = gson.fromJson(body, Epic.class);
                            taskManager.createEpic(epic);
                            sendText(exchange, "Эпик успешно создан.", 201);
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
                    try {
                        taskManager.deleteEpicById(getId(exchange));
                        sendText(exchange, "Эпик успешно удалён!", 200);
                    } catch (NoSuchElementException e) {
                        sendText(exchange, "Ошибка нахождения элемента: " + e.getMessage(), 404);
                    } catch (Exception e) {
                        sendText(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
                    }
                    break;
                default:
                    sendText(exchange, "Необрабатываемый метод", 400);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}