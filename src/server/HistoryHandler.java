package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        if (method.equals("GET") && pathSplit.length == 2) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            ArrayList<Task> history = taskManager.getHistory();
            writeResponse(exchange, gson.toJson(history), 200);
        } else {
            sendText(exchange, "Необрабатываемый метод", 400);
        }
    }
}
