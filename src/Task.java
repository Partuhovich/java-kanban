import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private TaskStatus status;
    public Integer id;


    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.id = task.id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(id, task.id);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}
