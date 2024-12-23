import java.util.ArrayList;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.removeIf(t -> Objects.equals(t.getId(), task.getId()));
        history.addFirst(new Task(task));
        if (history.size() > 10) {
            history.removeLast();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}