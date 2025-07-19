package Entity.AnimalClasses;

import java.lang.reflect.Constructor;
import Entity.EntityFactory;
import General.SimulationSettings;

public class AnimalFactory implements EntityFactory<Animal> {
    private final SimulationSettings.AnimalType animalType;

    public AnimalFactory(SimulationSettings.AnimalType animalType) {
        this.animalType = animalType;
    }

    @Override
    public Animal create() {
        try {
            Constructor<? extends Animal> animalConstructor =
                    animalType.getAnimalClass().getDeclaredConstructor(
                            double.class, int.class, int.class, double.class);
            return animalConstructor
                    .newInstance(
                            animalType.getWeight(),
                            animalType.getMaxPerCell(),
                            animalType.getSpeed(),
                            animalType.getFoodNeeded()
                    );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания животного: " + animalType.name(), e);
        }
    }
}