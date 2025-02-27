import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private int idCounter = 0;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
    public void setIdCounter(int newIdCounter) {
        idCounter = newIdCounter;
    }

    @Override
    public void createTask(Task newTask) {
        validateTaskOverlap(newTask);
        idCounter++;
        newTask.setId(idCounter);
        tasks.put(newTask.getId(), newTask);
        addToPrioritizedTasks(newTask);
    }

    @Override
    public void createEpic(Epic newEpic) {
        idCounter++;
        newEpic.setId(idCounter);
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        validateTaskOverlap(newSubTask);
        idCounter++;
        newSubTask.setId(idCounter);
        subTasks.put(newSubTask.getId(), newSubTask);
        Epic epic = epics.get(newSubTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(newSubTask);
            addToPrioritizedTasks(newSubTask);
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        validateTaskOverlap(updatedTask);
        Integer updatedTaskId = updatedTask.getId();
        if (tasks.containsKey(updatedTaskId)) {
            Task replacedTask = tasks.get(updatedTaskId);
            tasks.replace(updatedTaskId, updatedTask);
            prioritizedTasks.remove(replacedTask);
            addToPrioritizedTasks(updatedTask);
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Integer updatedEpicId = updatedEpic.getId();
        if (epics.containsKey(updatedEpicId)) {
            Epic replacedEpic = epics.get(updatedEpicId);
            ArrayList<SubTask> subTasks = replacedEpic.getSubTasks();
            updatedEpic.setSubTasks(subTasks);
            epics.replace(updatedEpicId, updatedEpic);
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        validateTaskOverlap(updatedSubTask);
        Integer updatedSubTaskId = updatedSubTask.getId();
        if (subTasks.containsKey(updatedSubTaskId)) {
            SubTask replacedSubTask = subTasks.get(updatedSubTaskId);
            subTasks.replace(updatedSubTaskId, updatedSubTask);
            epics.get(updatedSubTask.getEpicId()).updateSubTask(updatedSubTask);
            prioritizedTasks.remove(replacedSubTask);
            addToPrioritizedTasks(updatedSubTask);
        }
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        prioritizedTasks.remove(task);
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<SubTask> epicSubTasks = epic.getSubTasks();
        epicSubTasks.forEach(subTask -> {
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        });
        historyManager.remove(epicId);
        epics.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(Integer subtaskId) {
        SubTask subTask = subTasks.get(subtaskId);
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubTask(subTask);
        prioritizedTasks.remove(subTask);
        historyManager.remove(subtaskId);
        subTasks.remove(subtaskId);
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().stream()
                .flatMap(epic -> epic.getSubTasks().stream())
                .forEach(prioritizedTasks::remove);
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.values().forEach(prioritizedTasks::remove);
        epics.values().forEach(Epic::cleatAllSubTasks);
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

    @Override
    public boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task2.getEndTime().isBefore(task1.getStartTime()));
    }

    @Override
    public void validateTaskOverlap(Task newTask) {
        if (newTask.getStartTime() == null) {
            return;
        }

        boolean isOverlap = prioritizedTasks.stream()
                .anyMatch(existingTask -> isTasksOverlap(newTask, existingTask));

        if (isOverlap) {
            throw new IllegalStateException("Задача пересекается по времени с другой задачей.");
        }
    }
}