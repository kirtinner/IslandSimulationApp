package Entity.PlantClasses;

import Entity.Entity;
import General.SimulationSettings;

public abstract class Plant implements Entity {
    protected double weight;

    public Plant(double weight) {
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String getIcon() {
        for (SimulationSettings.PlantType type : SimulationSettings.PlantType.values()) {
            if (type.getPlantClass() == this.getClass()) {
                return type.getIcon();
            }
        }
        return "🌱";
    }

    public int getPlantIndex(Class<?> plantClass) {
        for (int i = 0; i < SimulationSettings.PlantType.values().length; i++) {
            if (SimulationSettings.PlantType.values()[i].getPlantClass() == plantClass) {
                return i;
            }
        }
        return -1;
    }
}