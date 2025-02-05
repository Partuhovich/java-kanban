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

}