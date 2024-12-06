import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private int idCounter = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void createTask(Task newTask) {
        idCounter++;
        newTask.setId(idCounter);
        tasks.put(newTask.getId(), newTask);
    }

    public void createEpic(Epic newEpic) {
        idCounter++;
        newEpic.setId(idCounter);
        epics.put(newEpic.getId(), newEpic);
    }

    public void createSubTask(SubTask newSubTask) {
        idCounter++;
        newSubTask.setId(idCounter);
        subTasks.put(newSubTask.getId(), newSubTask);
        newSubTask.getEpic().addSubTusk(newSubTask);
    }

    public void updateTask(Task updatedTask) {
        Integer updatedTaskId = updatedTask.getId();
        if (tasks.containsKey(updatedTaskId)) {
            tasks.replace(updatedTaskId, updatedTask);
        }
    }

    public void updateEpic(Epic updatedEpic) {
        Integer updatedEpicId = updatedEpic.getId();
        if (epics.containsKey(updatedEpicId)) {
            ArrayList<SubTask> subTasks = epics.get(updatedEpicId).getSubTasks();
            updatedEpic.setSubTasks(subTasks);
            epics.replace(updatedEpicId, updatedEpic);
            updatedEpic.updateEpicStatus();
        }
    }

    public void updateSubTask(SubTask updatedSubTask) {
        Integer updatedSubTaskId = updatedSubTask.getId();
        if (subTasks.containsKey(updatedSubTaskId)) {
            subTasks.replace(updatedSubTaskId, updatedSubTask);
            updatedSubTask.getEpic().updateSubTask(updatedSubTask);
        }
    }

    public void deleteTaskById(Integer taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpicById(Integer epicId) {
        ArrayList<SubTask> epicSubTasks = epics.get(epicId).getSubTasks();

        for (SubTask subTask : epicSubTasks) {
            subTasks.remove(subTask.id);
        }

        epics.remove(epicId);
    }

    public void deleteSubtaskById(Integer subtaskId) {
        SubTask subTask = subTasks.get(subtaskId);
        subTask.getEpic().getSubTasks().remove(subTask);
        subTask.getEpic().updateEpicStatus();
        subTasks.remove(subtaskId);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.updateEpicStatus();
        }
        subTasks.clear();
    }

    public Task getTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        }
        return null;
    }

    public Epic getEpicByID(Integer epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId);
        }
        return null;
    }

    public SubTask getSubTaskByID(Integer subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            return subTasks.get(subTaskId);
        }
        return null;
    }

    public ArrayList<SubTask> getSubTasksInEpic(Integer epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            return epic.getSubTasks();
        } else {
            return new ArrayList<>();
        }
    }
}