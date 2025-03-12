package tasks;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Epic extends Task {
    private ArrayList<Integer> subTasksIds;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status, null, null);
        this.subTasksIds = new ArrayList<>();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubTask(SubTask newSubTask) {
        subTasksIds.add(newSubTask.getId());
        updateEpicTiming();
        updateEpicStatus();
    }

    public void updateSubTask(SubTask updatedSubTask, Integer subTaskToUpdateId) {
        subTasksIds.removeIf(subTaskId -> subTaskId.equals(subTaskToUpdateId));
        subTasksIds.add(updatedSubTask.getId());
        updateEpicStatus();
        updateEpicTiming();
    }

    public ArrayList<Integer> getSubTasksIds() {
        return (subTasksIds != null) ? subTasksIds : new ArrayList<>();
    }

    public void removeSubTask(SubTask subTask) {
        subTasksIds.removeIf(id -> id.equals(subTask.getId()));
        updateEpicStatus();
        updateEpicTiming();
    }

    public void cleatAllSubTasks() {
        subTasksIds.clear();
        updateEpicTiming();
        updateEpicStatus();
    }

    public void setSubTasksIds(ArrayList<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
        updateEpicStatus();
        updateEpicTiming();
    }

    public ArrayList<SubTask> getEpicSubTasks(HashMap<Integer, SubTask> allSubTasks) {
        return subTasksIds.stream()
                .filter(allSubTasks::containsKey)
                .map(allSubTasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void updateEpicStatusAndTiming(ArrayList<SubTask> subTasks) {
        updateEpicStatus(subTasks);
        updateEpicTiming(subTasks);
    }

    private void updateEpicStatus(ArrayList<SubTask> subTasks) {
        boolean allDone = subTasks.stream().allMatch(subTask -> subTask.getStatus() == TaskStatus.DONE);
        boolean allNew = subTasks.stream().allMatch(subTask -> subTask.getStatus() == TaskStatus.NEW);

        if (allDone) {
            this.status = TaskStatus.DONE;
        } else if (allNew) {
            this.status = TaskStatus.NEW;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    private void updateEpicTiming(ArrayList<SubTask> subTasks) {
        if (subTasks.isEmpty()) {
            this.setStartTime(null);
            this.setDuration(null);
            return;
        }

        LocalDateTime earliestStart = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(startTime -> startTime != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(duration -> duration != null)
                .reduce(Duration.ZERO, Duration::plus);

        this.setStartTime(earliestStart);
        this.setDuration(totalDuration);
    }
}

