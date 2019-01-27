package bus.entities;

import bus.Utils;
import enigma.console.TextWindow;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

public class Bus extends Entity {

    private static final double SPEED = 0.5;

    public static final int FORWARD = 1;
    public static final int BACKWARD = -1;

    private String name;
    private int direction;

    private Line line;
    private Queue<Stop> nextStops;

    private int passengerCount;
    private Passenger[] passengers;

    private Stack<Luggage> luggages;
    private Stack<Luggage> luggageToLoad;
    private Stack<Luggage> luggageToUnload;

    public Bus(String name, Line line, int direction) {
        super();

        this.direction = direction * -1;
        this.name = name;
        this.line = line;

        passengerCount = 0;
        nextStops = new ArrayDeque<>();
        passengers = new Passenger[8];

        luggages = new Stack<>();
        luggageToLoad = new Stack<>();
        luggageToUnload = new Stack<>();

        initializeStops();
        initializePosition();
    }

    @Override
    public void update() {
        if (!luggageToUnload.empty()) {
            Luggage removeMe = luggageToUnload.pop();
            Stack<Luggage> tmp = new Stack<>();
            while (!luggages.peek().equals(removeMe)) {
                tmp.push(luggages.pop());
            }

            luggages.pop();

            while (!tmp.empty())
                luggages.push(tmp.pop());
            return;
        }

        if (!luggageToLoad.empty()) {
            luggages.push(luggageToLoad.pop());
            return;
        }

        if (nextStops.size() > 0) {
            Stop s = nextStops.peek();
            if (x == s.x && y == s.y) {
                // LOAD THE PASSENGERS
                s = nextStops.remove();

                if (passengerCount > 0) {
                    for (Passenger p : passengers) {
                        if (p == null)
                            continue;

                        if (p.getDestination().equals(s)) {
                            removePassenger(p);
                            for (Luggage l : p.getLuggages())
                                luggageToUnload.push(l);
                        }
                    }
                }

                if (passengerCount < passengers.length) {
                    for (Passenger p : s.getPassengers()) {
                        if (passengerCount == passengers.length)
                            break;

                        if (p == null)
                            continue;

                        if (nextStops.contains(p.getDestination()) && luggages.size() + p.getLuggages().length <= 8) {
                            p.getDeparture().removePassenger(p);
                            addPassenger(p);
                            for (Luggage l : p.getLuggages())
                                luggageToLoad.push(l);
                        }
                    }
                }

            }

            if (luggageToLoad.empty() && luggageToUnload.empty())
                if (x == s.x) {
                    // move up & down
                    y -= SPEED * Double.compare(y, s.y);
                } else if (y == s.y) {
                    // move left & right
                    x -= SPEED * Double.compare(x, s.x);
                }
        } else {
            initializeStops();
        }

    }

    @Override
    public void render(TextWindow map) {
        map.output((int) x, (int) y, Character.forDigit(passengerCount, 10));
    }

    public String getName() {
        return name;
    }

    public Stack<Luggage> getLuggages() {
        return luggages;
    }

    public Passenger[] getPassengers() {
        return passengers;
    }

    public void addPassenger(Passenger p) {
        passengerCount = Utils.addPassenger(passengers, passengerCount, p);
    }

    public void removePassenger(Passenger p) {
        passengerCount = Utils.removePassenger(passengers, passengerCount, p);
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    private void initializePosition() {
        Stop[] stops = line.getStops();

        if (direction == FORWARD) {
            x = stops[0].x;
            y = stops[0].y;
        } else {
            x = stops[stops.length - 1].x;
            y = stops[stops.length - 1].y;
        }
    }

    private void initializeStops() {
        direction *= -1;

        Stop[] stops = Arrays.copyOf(line.getStops(), line.getStops().length);

        if (direction < 0)
            stops = Utils.reverse(stops);

        for (Stop s : stops)
            if (s != null)
                nextStops.add(s);
    }

}
