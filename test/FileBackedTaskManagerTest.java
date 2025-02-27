import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
        tempFile = File.createTempFile("taskManager", ".txt");
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    public void testSaveMultipleTasks() {
        taskManager.createTask(new Task("Task 1", "Description 1", TaskStatus.NEW));
        taskManager.createTask(new Task("Task 2", "Description 2", TaskStatus.NEW));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getTasks().size(), "Загруженный менеджер должен содержать 2 задачи.");
    }

    @Test
    public void testSaveMultipleEpics() {
        taskManager.createEpic(new Epic("Epic 1", "Description 1", TaskStatus.NEW));
        taskManager.createEpic(new Epic("Epic 2", "Description 2", TaskStatus.NEW));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getEpics().size(), "Загруженный менеджер должен содержать 2 эпика.");
    }

    @Test
    public void testSaveMultipleSubTasks() {
        Epic epic = new Epic("Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        taskManager.createSubTask(new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, epic.getId()));
        taskManager.createSubTask(new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, epic.getId()));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getSubTasks().size(), "Загруженный менеджер должен содержать 2 подзадачи.");
    }

    @Test
    public void testLoadMultipleTasks() {
        taskManager.createTask(new Task("Task 1", "Description 1", TaskStatus.NEW));
        taskManager.createTask(new Task("Task 2", "Description 2", TaskStatus.NEW));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getTasks().size(), "Загруженный менеджер должен содержать 2 задачи.");
        Task task1 = loadedManager.getTaskById(1);
        assertNotNull(task1, "Задача 1 должна быть загружена.");
        assertEquals("Task 1", task1.getName(), "Имя задачи 1 должно совпадать.");

        Task task2 = loadedManager.getTaskById(2);
        assertNotNull(task2, "Задача 2 должна быть загружена.");
        assertEquals("Task 2", task2.getName(), "Имя задачи 2 должно совпадать.");
    }

    @Test
    public void testLoadMultipleEpics() {
        taskManager.createEpic(new Epic("Epic 1", "Description 1", TaskStatus.NEW));
        taskManager.createEpic(new Epic("Epic 2", "Description 2", TaskStatus.NEW));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getEpics().size(), "Загруженный менеджер должен содержать 2 эпика.");
        Epic epic1 = loadedManager.getEpicByID(1);
        assertNotNull(epic1, "Эпик 1 должен быть загружен.");
        assertEquals("Epic 1", epic1.getName(), "Имя эпика 1 должно совпадать.");

        Epic epic2 = loadedManager.getEpicByID(2);
        assertNotNull(epic2, "Эпик 2 должен быть загружен.");
        assertEquals("Epic 2", epic2.getName(), "Имя эпика 2 должно совпадать.");
    }

    @Test
    public void testLoadMultipleSubTasks() {
        Epic epic = new Epic("Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask sub1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, epic.getId());
        SubTask sub2 = new SubTask("SubTask 2", "Description 2", TaskStatus.NEW, epic.getId());
        taskManager.createSubTask(sub1);
        taskManager.createSubTask(sub2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getSubTasks().size(), "Загруженный менеджер должен содержать 2 подзадачи.");
        SubTask subTask1 = loadedManager.getSubTaskByID(sub1.getId());
        assertNotNull(subTask1, "Подзадача 1 должна быть загружена.");
        assertEquals("SubTask 1", subTask1.getName(), "Имя подзадачи 1 должно совпадать.");

        SubTask subTask2 = loadedManager.getSubTaskByID(sub2.getId());
        assertNotNull(subTask2, "Подзадача 2 должна быть загружена.");
        assertEquals("SubTask 2", subTask2.getName(), "Имя подзадачи 2 должно совпадать.");
    }
}