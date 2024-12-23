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
    }

    @Test
    public void testAddTaskToHistory() {
        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size(), "Задача не была добавлена");
    }

    @Test
    public void testTaskHistoryMaintainsPreviousVersion() {
        task.setId(1);
        historyManager.add(task);
        task.setId(5);
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.getFirst().getId(), "Предыдущая версия не была сохраненна");
    }

    @Test
    public void testTaskImmutabilityOnAdd() {
        Task originalTask = new Task("Original Task", "Original description", TaskStatus.NEW);
        originalTask.setId(22);

        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();
        TaskStatus originalStatus = originalTask.getStatus();
        Integer originalId = originalTask.getId();

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(originalTask);

        assertEquals(originalName, originalTask.getName(), "Имя задачи было измененно");
        assertEquals(originalDescription, originalTask.getDescription(), "Описание задачи было изменено");
        assertEquals(originalStatus, originalTask.getStatus(), "Статус задачи был изменён");
        assertEquals(originalId, originalTask.getId(), "Айди задачи был изменен");
    }

}