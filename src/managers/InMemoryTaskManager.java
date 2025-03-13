package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

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
        if (epics.containsKey(newSubTask.getEpicId())) {
            subTasks.put(newSubTask.getId(), newSubTask);
            Epic epic = epics.get(newSubTask.getEpicId());
            epic.addSubTask(newSubTask);
            addToPrioritizedTasks(newSubTask);
            epic.updateEpicStatusAndTiming(epic.getEpicSubTasks(getSubTasks()));
        } else {
            throw new NoSuchElementException("Epic с ID " + newSubTask.getEpicId() + " отсутствует.");
        }
    }

    @Override
    public void updateTask(Task updatedTask, Integer replacedTaskId) {
        validateTaskOverlap(updatedTask);
        if (tasks.containsKey(replacedTaskId)) {
            Task replacedTask = tasks.get(replacedTaskId);
            tasks.replace(replacedTaskId, updatedTask);
            updatedTask.setId(replacedTaskId);
            prioritizedTasks.remove(replacedTask);
            addToPrioritizedTasks(updatedTask);
        } else {
            throw new NoSuchElementException("Task с ID " + replacedTaskId + " отсутствует.");
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic, Integer replacedEpicId) {
        if (epics.containsKey(replacedEpicId)) {
            Epic replacedEpic = epics.get(replacedEpicId);
            epics.replace(replacedEpicId, updatedEpic);
            updatedEpic.setSubTasksIds(replacedEpic.getSubTasksIds());
            updatedEpic.setId(replacedEpicId);
            updatedEpic.updateEpicStatusAndTiming(updatedEpic.getEpicSubTasks(getSubTasks()));
        } else {
            throw new NoSuchElementException("Epic с ID " + replacedEpicId + " отсутствует.");
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask, Integer replacedSubTaskId) {
        validateTaskOverlap(updatedSubTask);
        if (subTasks.containsKey(replacedSubTaskId)) {
            SubTask replacedSubTask = subTasks.get(replacedSubTaskId);
            subTasks.replace(replacedSubTaskId, updatedSubTask);
            Epic epic = epics.get(replacedSubTask.getEpicId());
            updatedSubTask.setId(replacedSubTask.getId());
            updatedSubTask.setEpicId(replacedSubTask.getEpicId());
            epic.updateSubTask(updatedSubTask, replacedSubTaskId);
            prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId().equals(replacedSubTaskId));
            addToPrioritizedTasks(updatedSubTask);
            epic.updateEpicStatusAndTiming(epic.getEpicSubTasks(getSubTasks()));
        } else {
            throw new NoSuchElementException("SubTask с ID " + replacedSubTaskId + " отсутствует.");
        }
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId().equals(task.getId()));
            historyManager.remove(taskId);
            tasks.remove(taskId);
        } else {
            throw new NoSuchElementException("Task с ID " + taskId + " отсутствует.");
        }
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> epicSubTasksIds = epic.getSubTasksIds();
            epicSubTasksIds.forEach(subTaskId -> {
                SubTask subTask = subTasks.get(subTaskId);
                prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId().equals(subTask.getId()));
                historyManager.remove(subTaskId);
                subTasks.remove(subTaskId);
            });
            historyManager.remove(epicId);
            epics.remove(epicId);
        } else {
            throw new NoSuchElementException("Epic с ID " + epicId + " отсутствует.");
        }
    }

    @Override
    public void deleteSubtaskById(Integer subtaskId) {
        if (subTasks.containsKey(subtaskId)) {
            SubTask subTask = subTasks.get(subtaskId);
            Epic epic = epics.get(subTask.getEpicId());
            epic.removeSubTask(subTask);
            epic.updateEpicStatusAndTiming(epic.getEpicSubTasks(getSubTasks()));
            prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId().equals(subTask.getId()));
            historyManager.remove(subtaskId);
            subTasks.remove(subtaskId);
        } else {
            throw new NoSuchElementException("SubTask с ID " + subtaskId + " отсутствует.");
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(taskId -> prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId().equals(taskId)));
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().stream()
                .flatMap(epic -> epic.getSubTasksIds().stream())
                .forEach(subTaskId -> prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId().equals(subTaskId)));
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.keySet().forEach(subTaskId -> prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId().equals(subTaskId)));
        epics.values().forEach(Epic::cleatAllSubTasks);
        subTasks.clear();
    }

    @Override
    public Task getTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            historyManager.add(task);
            return task;
        } else {
            throw new NoSuchElementException("Task с ID " + taskId + " отсутствует.");
        }
    }

    @Override
    public Epic getEpicByID(Integer epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            historyManager.add(epic);
            return epic;
        } else {
            throw new NoSuchElementException("Epic с ID " + epicId + " отсутствует.");
        }
    }

    @Override
    public SubTask getSubTaskByID(Integer subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            SubTask subTask = subTasks.get(subTaskId);
            historyManager.add(subTask);
            return subTask;
        } else {
            throw new NoSuchElementException("SubTask с ID " + subTaskId + " отсутствует.");
        }
    }

    @Override
    public ArrayList<SubTask> getSubTasksInEpic(Integer epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            return epic.getEpicSubTasks(getSubTasks());
        } else {
            throw new IllegalArgumentException("Epic с ID " + epicId + " отсутствует.");
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task2.getEndTime().isBefore(task1.getStartTime()));
    }

    private void validateTaskOverlap(Task newTask) {
        if (newTask.getStartTime() == null) {
            return;
        }

        boolean isOverlap = prioritizedTasks.stream()
                .anyMatch(existingTask -> isTasksOverlap(newTask, existingTask));

        if (isOverlap) {
            throw new IllegalStateException("Задача пересекается по времени с другой задачей.");
        }
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            prioritizedTasks.add(task);
        }
    }
}