import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private int idCounter = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void createTask(Task newTask) {
        idCounter++;
        newTask.setId(idCounter);
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void createEpic(Epic newEpic) {
        idCounter++;
        newEpic.setId(idCounter);
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        idCounter++;
        newSubTask.setId(idCounter);
        subTasks.put(newSubTask.getId(), newSubTask);
        newSubTask.getEpic().addSubTusk(newSubTask);
    }

    @Override
    public void updateTask(Task updatedTask) {
        Integer updatedTaskId = updatedTask.getId();
        if (tasks.containsKey(updatedTaskId)) {
            tasks.replace(updatedTaskId, updatedTask);
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Integer updatedEpicId = updatedEpic.getId();
        if (epics.containsKey(updatedEpicId)) {
            ArrayList<SubTask> subTasks = epics.get(updatedEpicId).getSubTasks();
            updatedEpic.setSubTasks(subTasks);
            epics.replace(updatedEpicId, updatedEpic);
            updatedEpic.updateEpicStatus();
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        Integer updatedSubTaskId = updatedSubTask.getId();
        if (subTasks.containsKey(updatedSubTaskId)) {
            subTasks.replace(updatedSubTaskId, updatedSubTask);
            updatedSubTask.getEpic().updateSubTask(updatedSubTask);
        }
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        ArrayList<SubTask> epicSubTasks = epics.get(epicId).getSubTasks();

        for (SubTask subTask : epicSubTasks) {
            subTasks.remove(subTask.id);
        }

        epics.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(Integer subtaskId) {
        SubTask subTask = subTasks.get(subtaskId);
        subTask.getEpic().getSubTasks().remove(subTask);
        subTask.getEpic().updateEpicStatus();
        subTasks.remove(subtaskId);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.updateEpicStatus();
        }
        subTasks.clear();
    }

    @Override
    public Task getTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicByID(Integer epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public SubTask getSubTaskByID(Integer subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            SubTask subTask = subTasks.get(subTaskId);
            historyManager.add(subTask);
            return subTask;
        }
        return null;
    }

    @Override
    public ArrayList<SubTask> getSubTasksInEpic(Integer epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            return epic.getSubTasks();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory(); // Возвращаем копию списка истории
    }
}