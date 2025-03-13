package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.SubTask;
import tasks.Task;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SubTasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubTasksHandler(TaskManager taskManager) {
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
                            ArrayList<SubTask> subTasks = taskManager.getSubTasks();
                            String jsonResponse = gson.toJson(subTasks);
                            writeResponse(exchange, jsonResponse, 200);

                        } else if (pathSplit.length == 3) {
                            int subTaskId = getId(exchange);
                            SubTask subTask = taskManager.getSubTaskByID(subTaskId);
                            if (subTask != null) {
                                String jsonResponse = gson.toJson(subTask);
                                writeResponse(exchange, jsonResponse, 200);
                            } else {
                                sendText(exchange, "Подзадача с ID " + subTaskId + " не найдена.", 404);
                            }
                        } else {
                            sendText(exchange, "Некорректный путь запроса.", 400);
                        }
                    } catch (NumberFormatException e) {
                        sendText(exchange, "Некорректный ID подзадачи.", 400);
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
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            taskManager.createSubTask(subTask);
                            sendText(exchange, "Подзадача успешно создана.", 201);

                        } else if (pathSplit.length == 3) {
                            String body = parseTaskFromRequest(exchange);
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            taskManager.updateSubTask(subTask, getId(exchange));
                            sendText(exchange, "Подзадача успешно обновлена.", 201);
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
                    if (pathSplit.length == 3) {
                        taskManager.deleteSubtaskById(getId(exchange));
                        sendText(exchange, "Подзадача успешно удалена.", 200);
                    } else {
                        sendText(exchange, "Необходимо ввести ади задачи для удаления", 400);
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