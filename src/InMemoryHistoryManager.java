import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> taskMap = new HashMap<>();

    @Override
    public void add(Task task) {
        remove(task.getId());
        Node newNode = new Node(new Task(task));
        linkLast(newNode);
        taskMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = taskMap.remove(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
        }
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

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();
        for (Node current = head; current != null; current = current.next) {
            historyList.add(current.task);
        }
        return historyList;
    }
}