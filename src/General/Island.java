package General;

import Entity.AnimalClasses.*;
import Entity.PlantClasses.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Island {
    private Location[][] locations; // массив локаций (ячеек) на острове
    private Random random = ThreadLocalRandom.current();

    public Island(int width, int height) {
        // установим размеры массива локаций
        locations = new Location[width][height];

        // инициализуем массив локаций - для каждой ячейки заполним ее координаты
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y);
            }
        }

        // заселим животными и расстениями
        initializeIsland();
    }

    /**
     * Инициализация острова - заполнение ячеек животными и растениями
     */
    private void initializeIsland() {
        // Инициализация животных
        for (SimulationSettings.AnimalType animalType : SimulationSettings.AnimalType.values()) {
            int initialCount = animalType.getInitialCount();
            placeAnimals(animalType, initialCount);
        }
        // Инициализация растений
        for (SimulationSettings.PlantType plantType : SimulationSettings.PlantType.values()) {
            int initialCount = plantType.getInitialCount();
            placePlants(plantType, initialCount);
        }
    }

    /**
     * Первоначальное заполнение ячеек животными
     */
    private void placeAnimals(SimulationSettings.AnimalType animalType, int count) {
        AnimalFactory factory = new AnimalFactory(animalType);
        for (int i = 0; i < count; i++) {
            Animal animal = factory.create();
            boolean animalCreated = false;
            while (!animalCreated) {
                int x = random.nextInt(SimulationSettings.ISLAND_WIDTH);
                int y = random.nextInt(SimulationSettings.ISLAND_HEIGHT);
                Location location = locations[x][y];
                long countAnimalInCell = location.getAnimals().stream()
                        .filter(p -> p.getClass() == animalType.getAnimalClass())
                        .count();
                if (countAnimalInCell < animalType.getMaxPerCell()) {
                    location.addAnimal(animal);
                    animalCreated = true;
                }
            }
        }
    }

    /**
     * Первоначальное заполнение ячеек растениями
     */
    private void placePlants(SimulationSettings.PlantType plantType, int count) {
        PlantFactory factory = new PlantFactory(plantType);
        for (int i = 0; i < count; i++) {
            Plant plant = factory.create();
            boolean plantCreated = false;
            while (!plantCreated) {
                int x = random.nextInt(SimulationSettings.ISLAND_WIDTH);
                int y = random.nextInt(SimulationSettings.ISLAND_HEIGHT);
                Location location = locations[x][y];
                long countPlantInCell = location.getPlants().stream()
                        .filter(p -> p.getClass() == plantType.getPlantClass())
                        .count();
                if (countPlantInCell < plantType.getMaxPerCell()) {
                    location.addPlant(plant);
                    plantCreated = true;
                }
            }
        }
    }

    /**
     * Возвращает локацию по ее координатам
     */
    public Location getLocation(int x, int y) {
        if (x >= 0 && x < SimulationSettings.ISLAND_WIDTH && y >= 0 && y < SimulationSettings.ISLAND_HEIGHT) {
            return locations[x][y];
        }
        return null;
    }

    /**
     * Возвращает массив локаций
     */
    public Location[][] getLocations() {
        return locations;
    }

    /**
     * Первоначальное заполнение ячеек растениями
     */
    public void growPlants() {
        for (SimulationSettings.PlantType plantType : SimulationSettings.PlantType.values()) {
            PlantFactory factory = new PlantFactory(plantType);
            for (Location[] row : locations) {
                for (Location location : row) {
                    // вырастают новые растения
                    long currentCount = location.getPlants().stream()
                            .filter(p -> p.getClass() == plantType.getPlantClass())
                            .count();
                    if (random.nextDouble() < SimulationSettings.PLANT_GROWTH_PROBABILITY &&
                            currentCount < plantType.getMaxPerCell()) {
                        location.addPlant(factory.create());
                    }
                    // добавляют в весе имеющиеся растения
                    for (Plant plant : location.getPlants()) {
                        if (random.nextDouble() < SimulationSettings.PLANT_GROWTH_PROBABILITY) {
                            double newWeight = plant.getWeight();
                            newWeight += newWeight * SimulationSettings.PLANT_GROWTH_SPEED;
                            plant.setWeight(newWeight);
                        }
                    }
                }
            }
        }
    }
}