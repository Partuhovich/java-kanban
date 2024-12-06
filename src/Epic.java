import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;
    private TaskStatus status;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subTasks = new ArrayList<>();
    }

    public void addSubTusk(SubTask newSubTask) {
        subTasks.add(newSubTask);
        updateEpicStatus();
    }

    public ArrayList<SubTask> getSubTasks() {
        return (subTasks != null) ? subTasks : new ArrayList<>();
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void updateSubTask(SubTask updatedSubTask) {
        int counter = 0;

        for (SubTask subTask : subTasks) {

            if (subTask.getId().equals(updatedSubTask.getId())) {
                subTasks.remove(subTask);
                subTasks.add(counter, updatedSubTask);
                updateEpicStatus();
                break;
            }
            counter++;
        }
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

