package General;

import Entity.AnimalClasses.Animal;
import Entity.AnimalClasses.PredatorClasses.Bear;
import Entity.PlantClasses.Plant;
import Entity.AnimalClasses.PredatorClasses.*;
import Entity.AnimalClasses.HerbivoreClasses.*;
import Entity.PlantClasses.*;

/**
 * Класс содержит настройки симуляции.
 * В нем можно добавлять / удалять сущности.
 * После добавления / удаления сущностей необходимо отредактировать таблицу вероятностей поедания
 */
public class SimulationSettings {
    // Размер острова
    public static final int ISLAND_WIDTH = 100;
    public static final int ISLAND_HEIGHT = 20;

    // Длительность такта симуляции (в миллисекундах)
    public static final long TICK_DURATION = 1000;

    // Условие остановки симуляции - делаем MAX_TICKS итераций, если пользователь до этого не остановил вручную
    public static final int MAX_TICKS = 100;

    // Количество детенышей при размножении
    //public static final int OFFSPRING_COUNT = 2;

    // "Плата" за размножение - доля уменьшения текущей насыщенности
    public static final double ANIMAL_REPRODUCTION_FEE = 0.2;

    // "Плата" за жизнь - доля уменьшения текущей насыщенности
    public static final double ANIMAL_PRICE_OF_LIFE = 0.05;

    // Вероятность роста растений за такт
    public static final double PLANT_GROWTH_PROBABILITY = 0.1;

    // Скорость роста (прибавления веса) растений за такт - доля от текущей массы
    public static final double PLANT_GROWTH_SPEED = 0.05;

    // Перечисление для типов животных
    public enum AnimalType {
        WOLF(Wolf.class, 50, 30, 5, 8, 50, "🐺"),
        BOA(Boa.class, 15, 30, 2, 3, 30, "🐍"),
        FOX(Fox.class, 8, 30, 4, 2, 50, "🦊"),
        BEAR(Bear.class, 500, 5, 2, 80, 10, "🐻"),
        EAGLE(Eagle.class, 6, 20, 3, 1, 20, "🦅"),
        HORSE(Horse.class, 400, 20, 4, 60, 20, "🐎"),
        DEER(Deer.class, 300, 20, 4, 50, 20, "🦌"),
        RABBIT(Rabbit.class, 2, 150, 2, 0.45, 150, "🐇"),
        MOUSE(Mouse.class, 0.05, 500, 1, 0.01, 500, "🐁"),
        GOAT(Goat.class, 60, 140, 3, 10, 60, "🐐"),
        SHEEP(Sheep.class, 70, 140, 3, 15, 70, "🐑"),
        BOAR(Boar.class, 400, 50, 2, 50, 50, "🐗"),
        BUFFALO(Buffalo.class, 700, 10, 3, 100, 10, "🐃"),
        DUCK(Duck.class, 1, 200, 4, 0.50, 200, "🦆"),
        CATERPILLAR(Caterpillar.class, 0.01, 1000, 0, 0.01, 1000, "🐛");

        private final Class<? extends Animal> animalClass;  // тип животного
        private final double weight;                        // вес
        private final int maxPerCell;                       // максимумальное количество в ячейке
        private final int speed;                            // скорость передвижения в такте
        private final double foodNeeded;                    // нужное количество еды для полного насыщения
        private final int initialCount;                     // количество животных этого типа при инициализации симуляции
        private final String icon;                          // иконка для типа животного

        // конструктор перечисления
        AnimalType(Class<? extends Animal> animalClass, double weight, int maxPerCell, int speed, double foodNeeded, int initialCount, String icon) {
            this.animalClass = animalClass;
            this.weight = weight;
            this.maxPerCell = maxPerCell;
            this.speed = speed;
            this.foodNeeded = foodNeeded;
            this.initialCount = initialCount;
            this.icon = icon;
        }

        // геттеры
        public Class<? extends Animal> getAnimalClass() {
            return animalClass;
        }

        public double getWeight() {
            return weight;
        }

        public int getMaxPerCell() {
            return maxPerCell;
        }

        public int getSpeed() {
            return speed;
        }

        public double getFoodNeeded() {
            return foodNeeded;
        }

        public int getInitialCount() {
            return initialCount;
        }

        public String getIcon() {
            return icon;
        }
    }

    // Перечисление для типов растений
    public enum PlantType {
        GRASS(Grass.class, 1, 200, 1000, "🌱"),
        BUSH(Bush.class, 5, 50, 200, "🌿"),
        TREE(Tree.class, 20, 10, 50, "🌳");

        private final Class<? extends Plant> plantClass;    // тип растения
        private final double weight;
        private final int maxPerCell;                       // максимумальное количество в ячейке
        private final int initialCount;                     // количество при инициализации симуляции
        private final String icon;                          // иконка для типа растения

        PlantType(Class<? extends Plant> plantClass, double weight, int maxPerCell, int initialCount, String icon) {
            this.plantClass = plantClass;
            this.weight = weight;
            this.maxPerCell = maxPerCell;
            this.initialCount = initialCount;
            this.icon = icon;
        }

        // геттеры
        public Class<? extends Plant> getPlantClass() {
            return plantClass;
        }

        public double getWeight() {
            return weight;
        }

        public int getMaxPerCell() {
            return maxPerCell;
        }

        public int getInitialCount() {
            return initialCount;
        }

        public String getIcon() {
            return icon;
        }
    }

    // Таблица вероятностей поедания сущностей друг другом (в процентах, целые числа)
    // Растений по вертикали нет, поскольку они никого не едят
    // @formatter:off
    public static final int[][] PREDATION_PROBABILITIES = {
        //    В   У   Л   М   О   Л   О   К   М   К   О   К   Б   У   Г    Т    К    Д
        //    о   д   и   е   р   о   л   р   ы   о   в   а   у   т   у    р    у    е
        //    л   а   с   д   е   ш   е   о   ш   з   ц   б   й   к   с    а    с    р
        //    к   в   а   в   л   а   н   л   ь   а   а   а   в   а   е    в    т    е
        //                е       д   ь   и               н   о       н    а         в
        //                д       д       к                   л       и              о
        //                ь       ь                                   ца
            { 0,  0,  0,  0,  0, 30, 50, 70, 80, 90, 85, 15, 20, 90,  0,   0,   0,   0}, // Волк
            { 0,  0,  0,  0,  0,  0,  0, 20, 40,  0,  0,  0,  0, 90,  0,   0,   0,   0}, // Удав
            { 0,  0,  0,  0,  0,  0,  0, 80, 90,  0,  0,  0,  0, 60,  0,   0,   0,   0}, // Лиса
            { 0,  0,  0,  0,  0, 80, 70, 80, 90, 70, 70, 50, 20, 10,  0,   0,   0,   0}, // Медведь
            { 0,  0,  0,  0,  0,  0,  0, 90, 90,  0,  0,  0,  0, 80,  0,   0,   0,   0}, // Орел
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100, 100}, // Лошадь
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100, 100}, // Олень
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100,  50}, // Кролик
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100,   0}, // Мышь
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100,  60}, // Коза
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100, 100}, // Овца
            { 0,  0,  0,  0,  0,  0,  0,  0, 50,  0,  0,  0,  0,  0,  0,   0, 100, 100}, // Кабан
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100, 100}, // Буйвол
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100, 100}, // Утка
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 100, 100, 100}, // Гусеница
    };
    // @formatter:on
}