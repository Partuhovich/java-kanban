public class SubTask extends Task {
    private Integer epicId;
    private TaskStatus status;
    private TaskType type = TaskType.SUBTASK;

    public SubTask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK; // Переопределяем метод для возврата правильного типа
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}
