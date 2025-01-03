public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        printAllTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        printAllTasks(taskManager);

        taskManager.getSubTaskByID(subTask1.getId());
        printAllTasks(taskManager);

        taskManager.getEpicByID(epic1.getId());
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);

            for (SubTask subTask : manager.getSubTasksInEpic(epic.getId())) {
                System.out.println("--> " + subTask);
            }
        }
        System.out.println("Подзадачи:");
        for (SubTask subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}
