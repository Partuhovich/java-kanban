import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

public class InMemoryTaskManagerTest {
    private static InMemoryTaskManager taskManager;

    @BeforeAll
    public static void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testTaskManagerReturnsInitializedManagers() {
        assertNotNull(Managers.getDefault(), "Менеджер задач не создан");
        assertNotNull(Managers.getDefaultHistory(), "Менеджер истории не создан");
    }

    @Test
    public void testAddAndGetDifferentTaskTypes() {
        Task task = new Task("Task", "Description", TaskStatus.NEW);
        Epic epic = new Epic("Epic", "Description", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Description", TaskStatus.NEW, epic.getId());
        taskManager.createSubTask(subTask);

        assertEquals(task, taskManager.getTaskById(task.getId()), "Задача не была найдена");
        assertEquals(epic, taskManager.getEpicByID(epic.getId()), "Задача не была найдена");
        assertEquals(subTask, taskManager.getSubTaskByID(subTask.getId()), "Задача не была найдена");
    }

    @Test
    public void testTaskIdsDoNotConflict() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);
        task2.setId(1);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        task2.setId(4);
        Task retrievedTask = taskManager.getTaskById(task1.getId());
        Task retrievedTask2 = taskManager.getTaskById(task2.getId());

        assertEquals(retrievedTask.getDescription(), retrievedTask2.getDescription(), "Задачи конфликтуют");
    }
}