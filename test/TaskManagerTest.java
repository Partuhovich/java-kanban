import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testCreateEpic() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicByID(epic.getId()));
    }

    @Test
    public void testCreateSubTask() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTaskByID(subTask.getId()));
    }

    @Test
    public void testEpicStatusAllNew() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void testEpicStatusAllDone() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.DONE, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.DONE, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void testEpicStatusNewAndDone() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.DONE, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testEpicStatusInProgress() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testTaskOverlap() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(15));
        taskManager.createTask(task1);
        assertThrows(IllegalStateException.class, () -> taskManager.createTask(task2), "Задачи пересекаются по времени");
    }
}