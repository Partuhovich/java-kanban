import java.util.ArrayList;
import java.util.HashMap;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    ArrayList<Task> getHistory();
}
