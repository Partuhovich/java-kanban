import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        final int[] newCounter = {0};

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            lines.stream().skip(1).forEach(line -> {
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
                    if (task.getId() > newCounter[0]) {
                        newCounter[0] = task.getId();
                    }
                }
            });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }
        manager.setIdCounter(newCounter[0]);
        return manager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");
            getTasks().forEach(task -> {
                try {
                    writer.write(toString(task) + "\n");
                } catch (IOException e) {
                    throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
                }
            });
            getEpics().forEach(epic -> {
                try {
                    writer.write(toString(epic) + "\n");
                } catch (IOException e) {
                    throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
                }
            });
            getSubTasks().forEach(subtask -> {
                try {
                    writer.write(toString(subtask) + "\n");
                } catch (IOException e) {
                    throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
                }
            });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    private String toString(Task task) {
        switch (task.getType()) {
            case SUBTASK:
                SubTask subtask = (SubTask) task;
                return String.format("%d,%s,%s,%s,%s,%s,%s,%d",
                        subtask.getId(),
                        subtask.getType(),
                        subtask.getName(),
                        subtask.getStatus(),
                        subtask.getDescription(),
                        subtask.getStartTime() != null ? subtask.getStartTime() : "",
                        subtask.getDuration() != null ? subtask.getDuration().toMinutes() : "",
                        subtask.getEpicId());
            case EPIC:
                Epic epic = (Epic) task;
                return String.format("%d,%s,%s,%s,%s,%s,%s,",
                        epic.getId(),
                        epic.getType(),
                        epic.getName(),
                        epic.getStatus(),
                        epic.getDescription(),
                        epic.getStartTime() != null ? epic.getStartTime() : "",
                        epic.getDuration() != null ? epic.getDuration().toMinutes() : "");
            case TASK:
                return String.format("%d,%s,%s,%s,%s,%s,%s,",
                        task.getId(),
                        task.getType(),
                        task.getName(),
                        task.getStatus(),
                        task.getDescription(),
                        task.getStartTime() != null ? task.getStartTime() : "",
                        task.getDuration() != null ? task.getDuration().toMinutes() : "");
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
        LocalDateTime startTime = (parts.length > 5 && !parts[5].isEmpty()) ? LocalDateTime.parse(parts[5]) : null;
        Duration duration = (parts.length > 6 && !parts[6].isEmpty()) ? Duration.ofMinutes(Long.parseLong(parts[6])) : null;
        int epicId = parts.length > 7 ? Integer.parseInt(parts[7]) : -1;

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;
            case SUBTASK:
                SubTask subtask = new SubTask(name, description, status, epicId, duration, startTime);
                subtask.setId(id);
                return subtask;
            default:
                return null;
        }
    }
}
