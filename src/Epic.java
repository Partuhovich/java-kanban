import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status, null, null);
        this.subTasks = new ArrayList<>();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubTask(SubTask newSubTask) {
        subTasks.add(newSubTask);
        updateEpicTiming();
        updateEpicStatus();
    }

    public void updateSubTask(SubTask updatedSubTask) {
        subTasks.removeIf(subTask -> subTask.getId().equals(updatedSubTask.getId()));
        subTasks.add(updatedSubTask);
        updateEpicStatus();
        updateEpicTiming();
    }

    public ArrayList<SubTask> getSubTasks() {
        return (subTasks != null) ? subTasks : new ArrayList<>();
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
        updateEpicStatus();
        updateEpicTiming();
    }

    public void cleatAllSubTasks() {
        subTasks.clear();
        updateEpicTiming();
        updateEpicStatus();
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
        updateEpicStatus();
        updateEpicTiming();
    }

    private void updateEpicStatus() {
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

    private void updateEpicTiming() {
        if (subTasks.isEmpty()) {
            this.setStartTime(null);
            this.setDuration(null);
            this.endTime = null;
            return;
        }

        LocalDateTime earliestStart = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(startTime -> startTime != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(endTime -> endTime != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(duration -> duration != null)
                .reduce(Duration.ZERO, Duration::plus);

        this.setStartTime(earliestStart);
        this.setDuration(totalDuration);
        this.endTime = latestEnd;
    }
}

