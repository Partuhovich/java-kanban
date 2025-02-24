import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;
    private TaskStatus status;
    private LocalDateTime endTime; // Время завершения эпика

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status, null, null);
        this.subTasks = new ArrayList<>();
    }

    public void updateEpicTiming() {
        if (subTasks.isEmpty()) {
            this.setStartTime(null);
            this.setDuration(null);
            this.endTime = null;
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (SubTask subTask : subTasks) {
            if (subTask.getStartTime() != null) {
                if (earliestStart == null || subTask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subTask.getStartTime();
                }
                LocalDateTime subTaskEnd = subTask.getEndTime();
                if (latestEnd == null || subTaskEnd.isAfter(latestEnd)) {
                    latestEnd = subTaskEnd;
                }
                if (subTask.getDuration() != null) {
                    totalDuration = totalDuration.plus(subTask.getDuration());
                }
            }
        }

        this.setStartTime(earliestStart);
        this.setDuration(totalDuration);
        this.endTime = latestEnd;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubTusk(SubTask newSubTask) {
        subTasks.add(newSubTask);
        updateEpicStatus();
        updateEpicTiming();
    }

    public void updateSubTask(SubTask updatedSubTask) {
        int counter = 0;

        for (SubTask subTask : subTasks) {

            if (subTask.getId().equals(updatedSubTask.getId())) {
                subTasks.remove(subTask);
                subTasks.add(counter, updatedSubTask);
                updateEpicStatus();
                updateEpicTiming();
                break;
            }
            counter++;
        }
    }

    public ArrayList<SubTask> getSubTasks() {
        return (subTasks != null) ? subTasks : new ArrayList<>();
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC; // Переопределяем метод для возврата правильного типа
    }

    public void updateEpicStatus() {
        boolean allDone = true;
        boolean allNew = true;

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subTask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            this.status = TaskStatus.DONE;
        } else if (allNew) {
            this.status = TaskStatus.NEW;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }
}

