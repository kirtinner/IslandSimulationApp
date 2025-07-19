package General;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import Entity.AnimalClasses.Animal;
import Entity.PlantClasses.Plant;

public class Statistics {
    private Island island;

    public Statistics(Island island) {
        this.island = island;
    }

    public void printStatistics(int tick) {
        if (tick == 0) System.out.println("=== Первоначальное заполнение ===");
        else System.out.println("============= Такт " + tick + " =============");
        Map<SimulationSettings.AnimalType, Integer> animalCounts = new HashMap<>();
        Map<SimulationSettings.PlantType, Integer> plantCounts = new HashMap<>();

        for (SimulationSettings.AnimalType type : SimulationSettings.AnimalType.values()) {
            animalCounts.put(type, 0);
        }
        for (SimulationSettings.PlantType type : SimulationSettings.PlantType.values()) {
            plantCounts.put(type, 0);
        }

        for (Location[] row : island.getLocations()) {
            for (Location location : row) {
                // подсчет растений
                for (Plant plant : location.getPlants()) {
                    for (SimulationSettings.PlantType type : SimulationSettings.PlantType.values()) {
                        if (type.getPlantClass() == plant.getClass()) {
                            plantCounts.merge(type, 1, Integer::sum);
                            break;
                        }
                    }
                }
                // подсчет животных
                for (Animal animal : location.getAnimals()) {
                    if (animal.getIsAlive()) {
                        for (SimulationSettings.AnimalType type : SimulationSettings.AnimalType.values()) {
                            if (type.getAnimalClass() == animal.getClass()) {
                                animalCounts.merge(type, 1, Integer::sum);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Вывод статистики
        System.out.println("Всего животных: " + animalCounts.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println("Всего растений: " + plantCounts.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println("\nПопуляция по видам животных:");
        for (SimulationSettings.AnimalType type : SimulationSettings.AnimalType.values()) {
            System.out.printf("%s %s: %d%n", type.getIcon(), type.name(), animalCounts.get(type));
        }
        System.out.println("\nПопуляция по видам растений:");
        for (SimulationSettings.PlantType type : SimulationSettings.PlantType.values()) {
            System.out.printf("%s %s: %d%n", type.getIcon(), type.name(), plantCounts.get(type));
        }

        // Псевдографика
        System.out.println("\nКарта острова:");
        for (int y = 0; y < SimulationSettings.ISLAND_HEIGHT; y += 5) {
            for (int x = 0; x < SimulationSettings.ISLAND_WIDTH; x += 5) {
                Location location = island.getLocation(x, y);
                String icon = "⬜";
                // если в локации есть животные - выберем самое многочисленное и напечатаем его иконку
                if (!location.getAnimals().isEmpty()) {
                    Map<Animal, Long> animalMap = location.getAnimals().stream()
                            .filter(e -> e.getIsAlive())
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                    Optional<Map.Entry<Animal, Long>> maxCount =
                            animalMap.entrySet().stream().max(Map.Entry.comparingByValue());
                    if (maxCount.isPresent()) icon = maxCount.get().getKey().getIcon();
                }
                // если в локации нет животных, но есть растения - выберем самое многочисленное и напечатаем его иконку
                else if (!location.getPlants().isEmpty()) {
                    Map<Plant, Long> plantMap = location.getPlants().stream()
                            .filter(e -> e.getWeight() > 0)
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                    Optional<Map.Entry<Plant, Long>> maxCount =
                            plantMap.entrySet().stream().max(Map.Entry.comparingByValue());
                    if (maxCount.isPresent()) icon = maxCount.get().getKey().getIcon();
                };

                System.out.print(icon + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}