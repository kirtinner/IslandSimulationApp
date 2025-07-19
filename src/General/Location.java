package General;

import Entity.AnimalClasses.Animal;
import Entity.PlantClasses.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс описывает локацию - ячейку на острове
 * Локация содержит координаты ячейки (x, y), список животных и список растений в ячейке
 */
public class Location {
    private int x, y;
    private List<Animal> animals;
    private List<Plant> plants;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        this.animals = new CopyOnWriteArrayList<>();
        this.plants = new CopyOnWriteArrayList<>();
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
        animal.setLocation(this);
    }

    public void removeAnimal(Animal animal) { animals.remove(animal); }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public void removePlant(Plant plant) {
        plants.remove(plant);
    }

    public List<Animal> getAnimals() {
        return new ArrayList<>(animals);
    }

    public List<Plant> getPlants() {
        return new ArrayList<>(plants);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}