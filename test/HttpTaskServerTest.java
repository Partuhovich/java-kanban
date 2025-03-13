import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.TaskManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer server;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private TaskManager taskManager;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        ArrayList<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {

        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        Task updatedTask = new Task("Updated Task", "Updated Description", TaskStatus.IN_PROGRESS);
        updatedTask.setId(taskId);
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");

        Task retrievedTask = taskManager.getTaskById(taskId);
        assertEquals(updatedTask.getName(), retrievedTask.getName(), "Названия задач не совпадают");
        assertEquals(updatedTask.getDescription(), retrievedTask.getDescription(), "Описания задач не совпадают");
        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.getStatus(), "Статус задачи не обновлен");
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        assertEquals(0, taskManager.getTasks().size(), "Задача не была удалена");
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        ArrayList<Task> tasksFromManager = taskManager.getTasks();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic", TaskStatus.NEW);
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");
        ArrayList<Epic> epicsFromManager = taskManager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test Epic", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }


    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        assertEquals(0, taskManager.getEpics().size(), "Эпик не был удален");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Description 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", TaskStatus.IN_PROGRESS);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        ArrayList<Epic> epicsFromManager = taskManager.getEpics();
        assertEquals(2, epicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic", TaskStatus.NEW);
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask("Test SubTask", "Testing subtask", TaskStatus.NEW, epicId);
        String subTaskJson = gson.toJson(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");
        ArrayList<SubTask> subTasksFromManager = taskManager.getSubTasks();

        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test SubTask", subTasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask("Test SubTask", "Test Description", TaskStatus.NEW, epicId);
        taskManager.createSubTask(subTask);
        int subTaskId = subTask.getId();

        SubTask updatedSubTask = new SubTask("Updated SubTask", "Updated Description", TaskStatus.IN_PROGRESS, epicId);
        updatedSubTask.setId(subTaskId);
        String updatedSubTaskJson = gson.toJson(updatedSubTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTaskId))
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");

        SubTask retrievedSubTask = taskManager.getSubTaskByID(subTaskId);
        assertEquals(updatedSubTask.getName(), retrievedSubTask.getName(), "Названия подзадач не совпадают");
        assertEquals(updatedSubTask.getDescription(), retrievedSubTask.getDescription(), "Описания подзадач не совпадают");
        assertEquals(TaskStatus.IN_PROGRESS, retrievedSubTask.getStatus(), "Статус подзадачи не обновлен");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask("Test SubTask", "Test Description", TaskStatus.NEW, epicId);
        taskManager.createSubTask(subTask);
        int subTaskId = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTaskId))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        assertEquals(0, taskManager.getSubTasks().size(), "Подзадача не была удалена");
    }

    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, epicId);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", TaskStatus.IN_PROGRESS, epicId);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        ArrayList<SubTask> subTasksFromManager = taskManager.getSubTasks();
        assertEquals(2, subTasksFromManager.size(), "Некорректное количество подзадач");
    }
}
