package bus.entities;

import enigma.console.TextWindow;

import java.util.concurrent.ThreadLocalRandom;

public class Passenger extends Entity {

    private Line line;
    private Stop departure;
    private Stop destination;

    private int luggageCount;
    private Luggage[] luggages;

    public Passenger(Line line, Stop departure, Stop destination) {
        super();

        luggageCount = ThreadLocalRandom.current().nextInt(3);
        luggages = new Luggage[luggageCount];

        for (int i = 0; i < luggages.length; i++)
            luggages[i] = new Luggage();

        this.line = line;
        this.departure = departure;
        this.destination = destination;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(TextWindow map) {

    }

    public Stop getDeparture() {
        return departure;
    }

    public Stop getDestination() {
        return destination;
    }

    public Luggage[] getLuggages() {
        return luggages;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(id);
        sb.append(": ");
        
        sb.append("Line");
        sb.append(line.getName());
        sb.append(" ");

        sb.append(departure.getName());
        sb.append("-");
        sb.append(destination.getName());

        sb.append(" (L:");
        for (int i = 0; i < luggageCount; i++) {
            sb.append(luggages[i].toString());
            sb.append(",");
        }
        sb.append(")");

        return sb.toString();
    }
}

