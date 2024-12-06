public class SubTask extends Task {
    private Epic epic;
    private TaskStatus status;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
