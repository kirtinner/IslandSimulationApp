
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import Entity.AnimalClasses.Animal;
import General.*;

public class IslandSimulationApp {
    private Island island;
    private Statistics statistics;
    private ScheduledExecutorService schedulerExecutor;
    private ExecutorService animalExecutor;
    private int currentTick = 0;
    private AtomicBoolean running = new AtomicBoolean(false);

    public IslandSimulationApp() {
        island = new Island(SimulationSettings.ISLAND_WIDTH, SimulationSettings.ISLAND_HEIGHT);
        statistics = new Statistics(island);
        schedulerExecutor = Executors.newScheduledThreadPool(3);
        animalExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        statistics.printStatistics(0);
    }

    public void start() {
        running.set(true);

        // Задача роста растений
        schedulerExecutor.scheduleAtFixedRate(
                () -> {
                    if (!running.get()) return;
                    island.growPlants();
                }, 0, SimulationSettings.TICK_DURATION, TimeUnit.MILLISECONDS);

        // Задача жизненного цикла животных
        schedulerExecutor.scheduleAtFixedRate(
                () -> {
                    if (!running.get()) return;
                    currentTick++;
                    for (Location[] row : island.getLocations()) {
                        for (Location location : row) {
                            for (Animal animal : location.getAnimals()) {
                                if (animal.getIsAlive()) {
                                    animalExecutor.submit(() -> {
                                        animal.eat();
                                        animal.move(island);
                                        animal.reproduce();
                                        animal.checkStarvation();
                                    });
                                }
                            }
                        }
                    }
                    statistics.printStatistics(currentTick);

                    // Условие остановки - либо прошли все циклы либо на острове не осталось животных
                    if (currentTick >= SimulationSettings.MAX_TICKS || isSimulationOver()) {
                        stop();
                    }
                }, 0, SimulationSettings.TICK_DURATION, TimeUnit.MILLISECONDS);
    }

    /**
     * Проверка присутствия животных на острове
     */
    private boolean isSimulationOver() {
        for (Location[] row : island.getLocations()) {
            for (Location location : row) {
                if (!location.getAnimals().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Метод остановки симуляции
     */
    public void stop() {
        running.set(false);
        schedulerExecutor.shutdown();
        animalExecutor.shutdown();
        try {
            schedulerExecutor.awaitTermination(5, TimeUnit.SECONDS);
            animalExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Симуляция завершена!");
    }

    public static void main(String[] args) {
        System.out.println("============================== Симуляция экосистемы острова ==============================");
        System.out.println("Добро пожаловать в симуляцию экосистемы!");
        System.out.println("В этой симуляции вы увидите взаимодействие животных и растений на острове размером " +
                SimulationSettings.ISLAND_WIDTH + "x" + SimulationSettings.ISLAND_HEIGHT + ".");
        System.out.println("Животные двигаются, едят, размножаются и умирают от голода.");
        System.out.println("Растения (трава, кустарники, деревья) растут с вероятностью " +
                (SimulationSettings.PLANT_GROWTH_PROBABILITY * 100) + "% за такт.");
        System.out.println("В псевдографике показана:");
        System.out.println(" - только каждая 5-ая строка и каждая 5-ая колонка, ");
        System.out.println(" - иконка животного (растения при отсутствии животных) с макмимальной популяцией в локации.");
        System.out.println("Для запуска симуляции нажмите Enter.");
        System.out.println("Для завершения симуляции введите в консоли 'q' (в любом регистре) и нажмите клавишу Enter.");
        System.out.println("==========================================================================================");

        // Ожидание нажатия Enter
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // Инициализация острова
        IslandSimulationApp simulation = new IslandSimulationApp();

        // Запуск потока для мониторинга действий пользователя по выходу из проекта
        Thread inputThread = new Thread(() -> {
            while (simulation.running.get()) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("q")) {
                        simulation.stop();
                        break;
                    }
                }
            }
            scanner.close();
        });
        inputThread.setDaemon(true);
        inputThread.start();

        // Запуск симуляции
        simulation.start();
    }
}