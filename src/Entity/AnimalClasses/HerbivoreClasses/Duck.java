package Entity.AnimalClasses.HerbivoreClasses;

import Entity.AnimalClasses.Animal;

public class Duck extends Herbivore {
    public Duck(double weight, int maxPerCell, int speed, double foodNeeded) {
        super(weight, maxPerCell, speed, foodNeeded);
    }

    @Override
    public void eat() {
        if (!isAlive || currentSatiety >= foodNeeded) return;

        // Сначала пытается съесть гусеницу
        int countOfMeals = 5; // сьест несколько гусениц за раз
        for (Animal animal : location.getAnimals()) {
            if (animal.getClass() == Caterpillar.class && animal.getIsAlive()) {
                if (random.nextInt(100) < 90) {
                    double foodGained = Math.min(animal.getWeight(), foodNeeded - currentSatiety);
                    currentSatiety += foodGained;
                    animal.setIsAlive(false);
                    location.removeAnimal(animal);
                    countOfMeals--;
                    if (countOfMeals == 0) return;
                }
            }
        }

        // Если нет гусениц, ест растения
        if (countOfMeals == 10) super.eat();
    }
}