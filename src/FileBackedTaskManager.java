import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager{
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void createTask(Task newTask) {
        super.createTask(newTask);
        save();
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save();
    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        super.createSubTask(newSubTask);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        super.updateSubTask(updatedSubTask);
        save();
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }


    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (SubTask subtask : getSubTasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                Task task = fromString(line);
                if (task != null) {
                    switch (task.getType()) {
                        case TASK:
                            manager.createTask(task);
                            break;
                        case EPIC:
                            manager.createEpic((Epic) task);
                            break;
                        case SUBTASK:
                            manager.createSubTask((SubTask) task);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }
        return manager;
    }

    private String toString(Task task) {
        switch (task.getType()) {
            case SUBTASK:
                SubTask subtask = (SubTask) task;
                return String.format("%d,%s,%s,%s,%s,%d",
                        subtask.getId(), subtask.getType(), subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
            case EPIC:
                Epic epic = (Epic) task;
                return String.format("%d,%s,%s,%s,%s,",
                        epic.getId(), epic.getType(), epic.getName(), epic.getStatus(), epic.getDescription());
            case TASK:
                return String.format("%d,%s,%s,%s,%s,",
                        task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription());
            default:
                return "";
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        int epicId = parts.length > 5 ? Integer.parseInt(parts[5]) : -1;

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                return epic;
            case SUBTASK:
                SubTask subtask = new SubTask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                return null;
        }
    }
}
