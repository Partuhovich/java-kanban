import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class TaskTest {
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    public void setUp() {
        task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task2 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        epic1 = new Epic("Epic 1", "Description 1", TaskStatus.NEW);
        epic2 = new Epic("Epic 2", "Description 2", TaskStatus.NEW);
        subTask1 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, epic1.getId());
        subTask2 = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, epic1.getId());
    }


    @Test
    public void testTasksAreEqualById() {
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи не равны");
    }

    @Test
    public void testEpicsAreEqualById() {
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2, "Задачи не равны");
    }

    @Test
    public void testSubtasksAreEqualById() {
        subTask1.setId(1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2, "Задачи не равны");
    }

}