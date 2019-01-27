package bus.entities;

import enigma.console.TextWindow;

import java.util.Objects;

public abstract class Entity {

    private static int lastId = 1;

    protected int id;

    protected double x;
    protected double y;

    public Entity() {
        id = lastId++;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    abstract public void update();

    abstract public void render(TextWindow map);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
