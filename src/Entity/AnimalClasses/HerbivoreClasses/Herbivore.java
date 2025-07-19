package Entity.AnimalClasses.HerbivoreClasses;

import Entity.AnimalClasses.Animal;
import Entity.PlantClasses.Plant;
import General.SimulationSettings;

public abstract class Herbivore extends Animal {
    public Herbivore(double weight, int maxPerCell, int speed, double foodNeeded) {
        super(weight, maxPerCell, speed, foodNeeded);
    }

    @Override
    public void eat() {
        // не ест мертвый и сытый
        if (!isAlive || currentSatiety >= foodNeeded) return;

        // индекс строки таблицы вероятности для травоядного
        int animalIndex = getAnimalIndex(this.getClass());
        if (animalIndex == -1) return;

        // травоядное пытается сьесть растение
        for (Plant plant : location.getPlants()) {
            if (plant.getWeight() == 0) continue;

            // индекс колонки таблицы вероятности для растения
            int plantIndex = plant.getPlantIndex(plant.getClass());
            if (plantIndex == -1) continue;
            plantIndex += SimulationSettings.AnimalType.values().length;

            // определим ест ли травоядное текущее растение, и если да - сьедим, насытимся, уменьшим вес растения
            // или удалим растение из локации если его полностью сьели
            int probability = SimulationSettings.PREDATION_PROBABILITIES[animalIndex][plantIndex];

            if (probability == 100) {
                double foodGained = Math.min(plant.getWeight(), foodNeeded - currentSatiety);
                currentSatiety += foodGained;
                if (plant.getWeight() == foodGained) location.removePlant(plant);
                else plant.setWeight(plant.getWeight() - foodGained);
                break;
            }
        }
    }
}