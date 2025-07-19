package Entity.AnimalClasses;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import Entity.AnimalClasses.HerbivoreClasses.Herbivore;
import Entity.Entity;
import General.*;

public abstract class Animal implements Entity, Eatable, Movable, Reproducible {

    protected double weight;
    protected int maxPerCell;
    protected int speed;
    protected double foodNeeded;

    protected double currentSatiety;
    protected Location location;
    protected boolean isAlive = true;
    protected Random random = ThreadLocalRandom.current();

    public Animal(double weight, int maxPerCell, int speed, double foodNeeded) {
        this.weight = weight;
        this.maxPerCell = maxPerCell;
        this.speed = speed;
        this.foodNeeded = foodNeeded;
        this.currentSatiety = foodNeeded * 0.5; // животное рождается сытое наполовину
    }

    @Override
    public abstract void eat();

    @Override
    public void move(Island island) {
        if (!isAlive || speed == 0) return;

        int newX = location.getX();
        int newY = location.getY();
        int steps = random.nextInt(speed + 1);

        // делаем хаотичные шаги, проверяем что находимся в пределах поля симуляции, проверяем не превышено ли
        // количество животных этого типа в ячейке - если все норм, то меняем локацию
        for (int i = 0; i < steps; i++) {
            int direction = random.nextInt(4); // 0: вверх, 1: вправо, 2: вниз, 3: влево
            switch (direction) {
                case 0:
                    newY--;
                    break;
                case 1:
                    newX++;
                    break;
                case 2:
                    newY++;
                    break;
                case 3:
                    newX--;
                    break;
            }

            Location newLocation = island.getLocation(newX, newY);
            if (newLocation != null && newLocation.getAnimals().stream()
                    .filter(a -> a.getClass() == this.getClass()).count() < maxPerCell) {
                location.removeAnimal(this);
                newLocation.addAnimal(this);
                this.location = newLocation;
                newX = location.getX();
                newY = location.getY();
            }
        }
    }

    @Override
    public void reproduce() {
        // размножаются живые и сильные
        if (!isAlive || currentSatiety < foodNeeded * 0.7) return;
        // количество животных этого типа в локации
        long sameSpeciesCount = location.getAnimals().stream()
                .filter(a -> a.getClass() == this.getClass() && a.isAlive && a != this).count();
        // рождение младенца животного
        if (sameSpeciesCount >= 1 && location.getAnimals().stream()
                .filter(a -> a.getClass() == this.getClass()).count() < maxPerCell) {
            // вероятность рождения травоядного сделаем обратно пропорциональной количеству животных в ячейке
            boolean doThis = true;
            if (this instanceof Herbivore) {
                doThis = random.nextInt(100) < (maxPerCell - sameSpeciesCount) / maxPerCell * 100;
            }
            if (doThis) {
                AnimalFactory factory = new AnimalFactory(SimulationSettings.AnimalType.valueOf(
                        this.getClass().getSimpleName().toUpperCase()));
                location.addAnimal(factory.create());
                currentSatiety -= foodNeeded * SimulationSettings.ANIMAL_REPRODUCTION_FEE;
            }
        }
    }

    public void checkStarvation() {
        currentSatiety -= foodNeeded * SimulationSettings.ANIMAL_PRICE_OF_LIFE;
        if (currentSatiety <= 0) {
            isAlive = false;
            location.removeAnimal(this);
        }
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean alive) {
        isAlive = alive;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String getIcon() {
        for (SimulationSettings.AnimalType type : SimulationSettings.AnimalType.values()) {
            if (type.getAnimalClass() == this.getClass()) {
                return type.getIcon();
            }
        }
        return "❓";
    }

    public int getAnimalIndex(Class<?> animalClass) {
        for (int i = 0; i < SimulationSettings.AnimalType.values().length; i++) {
            if (SimulationSettings.AnimalType.values()[i].getAnimalClass() == animalClass) {
                return i;
            }
        }
        return -1;
    }
}