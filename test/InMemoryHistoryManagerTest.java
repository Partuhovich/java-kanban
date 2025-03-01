import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private Task task;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Task", "Description", TaskStatus.NEW);
        task.setId(1);
    }

    @Test
    public void testAddTaskToHistory() {
        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size(), "Задача не была добавлена");
    }

    @Test
    public void testRemoveTaskToHistory() {
        historyManager.add(task);
        historyManager.remove(task.getId());
        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "Задача не была добавлена");
    }

    @Test
    public void testTaskHistoryMaintainsPreviousVersion() {
        historyManager.add(task);
        task.setId(5);
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.getLast().getId(), "Предыдущая версия не была сохраненна");
    }

    @Test
    public void testTaskImmutabilityOnAdd() {
        String originalName = task.getName();
        String originalDescription = task.getDescription();
        TaskStatus originalStatus = task.getStatus();
        Integer originalId = task.getId();

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);

        assertEquals(originalName, task.getName(), "Имя задачи было измененно");
        assertEquals(originalDescription, task.getDescription(), "Описание задачи было изменено");
        assertEquals(originalStatus, task.getStatus(), "Статус задачи был изменён");
        assertEquals(originalId, task.getId(), "Айди задачи был изменен");
    }

    @Test
    public void testTaskHistoryHasNoDuplicates() {
        historyManager.add(task);
        historyManager.add(task);
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дупликат не удалился");
    }

    @Test
    public void testEmptyHistory() {
        final ArrayList<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    public void testRemoveFromBeginning() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId()); // Удаляем задачу из начала

        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task2.getId(), history.get(0).getId(), "Первая задача должна быть task2");
        assertEquals(task3.getId(), history.get(1).getId(), "Вторая задача должна быть task3");
    }

    @Test
    public void testRemoveFromMiddle() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId()); // Удаляем задачу из середины

        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task1.getId(), history.get(0).getId(), "Первая задача должна быть task1");
        assertEquals(task3.getId(), history.get(1).getId(), "Вторая задача должна быть task3");
    }

    @Test
    public void testRemoveFromEnd() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId()); // Удаляем задачу из конца

        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task1.getId(), history.get(0).getId(), "Первая задача должна быть task1");
        assertEquals(task2.getId(), history.get(1).getId(), "Вторая задача должна быть task2");
    }
}