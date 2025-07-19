package Entity;

public interface EntityFactory<T extends Entity> {
    T create();
}