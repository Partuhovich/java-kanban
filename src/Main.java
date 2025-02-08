public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, epic1.getId());


        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        printAllTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        printAllTasks(taskManager);

        taskManager.getTaskById(task2.getId());
        taskManager.getSubTaskByID(subTask1.getId());
        taskManager.getSubTaskByID(subTask2.getId());
        taskManager.getEpicByID(epic1.getId());
        printAllTasks(taskManager);

        taskManager.getEpicByID(epic2.getId());
        taskManager.deleteEpicById(epic1.getId());
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
