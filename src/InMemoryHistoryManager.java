import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private boolean isChanged = false;
    private final HashMap<Integer, Node> taskMap = new HashMap<>();
    private final ArrayList<Task> tasksList = new ArrayList<>();

    @Override
    public void add(Task task) {
        remove(task.getId());
        Node newNode = new Node(new Task(task));
        linkLast(newNode);
        taskMap.put(task.getId(), newNode);
        isChanged = true;
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = taskMap.remove(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
        }
        isChanged = true;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = getTasks();
        return historyList;
    }

    private void linkLast(Node newNode) {
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.prev = null;
        node.next = null;
    }

    private ArrayList<Task> getTasks() {
        if (isChanged) {
            tasksList.clear();
            for (Node current = head; current != null; current = current.next) {
                tasksList.add(current.task);
            }
        }
        return tasksList;
    }
}