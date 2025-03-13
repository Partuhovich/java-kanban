package tasks;

import java.time.LocalDateTime;
import java.time.Duration;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, TaskStatus status, Integer epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + this.getId() +
                ", name:'" + this.getName() + '\'' +
                ", type:'" + this.getType() + '\'' +
                ", description:'" + this.getDescription() + '\'' +
                ", duration:'" + this.getDuration() + '\'' +
                ", startTime:'" + this.getStartTime() + '\'' +
                ", epicId:'" + this.getEpicId() + '\'' +
                ", status:" + this.getStatus() +
                '}';
    }
}
