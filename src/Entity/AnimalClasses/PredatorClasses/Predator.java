package Entity.AnimalClasses.PredatorClasses;

import Entity.AnimalClasses.Animal;
import General.SimulationSettings;

public abstract class Predator extends Animal {
    public Predator(double weight, int maxPerCell, int speed, double foodNeeded) {
        super(weight, maxPerCell, speed, foodNeeded);
    }

    @Override
    public void eat() {
        // не ест мертвый и сытый
        if (!isAlive || currentSatiety >= foodNeeded) return;

        // индекс строки таблицы вероятности для хищника
        int predatorIndex = getAnimalIndex(this.getClass());
        if (predatorIndex == -1) return;

        // хищник может охотиться несколько раз за такт, пока не насытится
        int countOfMeals = 2;

        // хищник пытается сьесть добычу
        for (Animal prey : location.getAnimals()) {
            // не едим сами себя и падаль
            if (prey == this || !prey.getIsAlive()) continue;

            // индекс колонки таблицы вероятности для добычи
            int preyIndex = getAnimalIndex(prey.getClass());
            if (preyIndex == -1) continue;

            // определим вероятность поедания, в случае удачи - сьедим, насытимся, удалим добычу из локации
            int probability = SimulationSettings.PREDATION_PROBABILITIES[predatorIndex][preyIndex];

            if (random.nextInt(100) < probability) {
                double foodGained = Math.min(prey.getWeight(), foodNeeded - currentSatiety);
                currentSatiety += foodGained;
                prey.setIsAlive(false);
                location.removeAnimal(prey);

                countOfMeals--;
                if (countOfMeals == 0 || currentSatiety == foodNeeded) break;
            }
        }
    }
}