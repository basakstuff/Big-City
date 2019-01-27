package bus.entities;

import enigma.console.TextWindow;

public class Line extends Entity {

    private char name;
    private Stop[] stops;
    private Bus[] buses;

    public Line(Stop[] stops) {
        super();

        this.stops = stops;
        name = stops[0].getName();

        // ====

        String nf = "%c%d";

        buses = new Bus[]{
                new Bus(String.format(nf, name, 1), this, Bus.FORWARD),
                new Bus(String.format(nf, name, 2), this, Bus.BACKWARD)
        };
    }

    @Override
    public void update() {
        for (Bus b : buses)
            b.update();
    }

    @Override
    public void render(TextWindow map) {
        for (Stop s : stops)
            s.render(map);

        for (Bus b : buses)
            b.render(map);
    }

    public Bus[] getBuses() {
        return buses;
    }

    public char getName() {
        return name;
    }

    public Stop[] getStops() {
        return stops;
    }
}
