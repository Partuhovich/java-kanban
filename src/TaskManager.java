import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    void createTask(Task newTask);
    void createEpic(Epic newEpic);
    void createSubTask(SubTask newSubTask);

    void updateTask(Task updatedTask);
    void updateEpic(Epic updatedEpic);
    void updateSubTask(SubTask updatedSubTask);

    void deleteTaskById(Integer taskId);
    void deleteEpicById(Integer epicId);
    void deleteSubtaskById(Integer subtaskId);

    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubTasks();

    Task getTaskById(Integer taskId);
    Epic getEpicByID(Integer epicId);
    SubTask getSubTaskByID(Integer subTaskId);

    ArrayList<SubTask> getSubTasksInEpic(Integer epicId);

    ArrayList<Task> getTasks();
    ArrayList<Epic> getEpics();
    ArrayList<SubTask> getSubTasks();

    ArrayList<Task> getHistory();
}
