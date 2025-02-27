import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);

        LocalDateTime startTime = LocalDateTime.of(2023, 10, 10, 10, 0); // Начальное время
        Duration duration = Duration.ofMinutes(30);

        for (int i = 1; i <= 5; i++) {

            SubTask subTask = new SubTask(
                    "Подзадача " + i,
                    "Описание подзадачи " + i,
                    TaskStatus.NEW,
                    epic.getId(),
                    duration,
                    startTime.plusMinutes(30)
            );
            taskManager.createSubTask(subTask);

            startTime = startTime.plusHours(1);
        }

        taskManager.getPrioritizedTasks().stream()
                .forEach(System.out::println);

        System.out.println(epic);

    }
}