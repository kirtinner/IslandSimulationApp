package Entity.PlantClasses;

import Entity.EntityFactory;
import General.SimulationSettings;
import java.lang.reflect.Constructor;

public class PlantFactory implements EntityFactory<Plant> {
    private final SimulationSettings.PlantType plantType;

    public PlantFactory(SimulationSettings.PlantType plantType) {
        this.plantType = plantType;
    }

    @Override
    public Plant create() {
        try {
            Constructor<? extends Plant> plantConstructor =
                    plantType.getPlantClass().getDeclaredConstructor(double.class);
            return plantConstructor.newInstance(plantType.getWeight());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания растения: " + plantType.name(), e);
        }
    }
}