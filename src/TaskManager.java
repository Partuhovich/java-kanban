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

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void createTask(Task newTask) {
        idCounter++;
        tasks.put(idCounter, newTask);
        newTask.setId(idCounter);
    }

    public void createEpic(Epic newEpic) {
        idCounter++;
        epics.put(idCounter, newEpic);
        newEpic.setId(idCounter);
    }

    public void createSubTask(SubTask newSubTask, Epic epic) {
        idCounter++;
        subTasks.put(idCounter, newSubTask);
        newSubTask.setEpic(epic);
        newSubTask.setId(idCounter);
        epic.addSubTusk(newSubTask);
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
        subTasks.remove(subtaskId);
    }

    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
    }

    public Task getTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        } else if (epics.containsKey(taskId)) {
            return epics.get(taskId);
        } else if (subTasks.containsKey(taskId)) {
            return subTasks.get(taskId);
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

    public ArrayList<String> getAllTasksNames() {
        ArrayList<String> allTaskNames = new ArrayList<>();

        for (Task task : tasks.values()) {
            allTaskNames.add(task.getName());
        }

        for (Epic epic : epics.values()) {
            allTaskNames.add(epic.getName());
        }

        for (SubTask subTask : subTasks.values()) {
            allTaskNames.add(subTask.getName());
        }

        return allTaskNames;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subTasks.values());

        return allTasks;
    }
}