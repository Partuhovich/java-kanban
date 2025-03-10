import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
                    if(pathSplit.length == 2) {
                        ArrayList<Task> tasks = taskManager.getTasks();
                        String response = tasks.stream()
                                .map(Task::toString)
                                .collect(Collectors.joining("\n"));
                        writeResponse(exchange, response, 200);
                    } else if(pathSplit.length == 3) {
                        Task task = taskManager.getTaskById(Integer.parseInt(pathSplit[2]));
                        String response = task.toString();
                        writeResponse(exchange, response, 200);
                    }
                    break;
                case "POST":
                    if(pathSplit.length == 2) {

                    } else if (pathSplit.length == 3) {

                    }
                    break;
                case "DELETE":

                    break;
                default:

            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
