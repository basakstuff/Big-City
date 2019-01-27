package bus.entities;

import bus.Utils;
import enigma.console.TextWindow;

public class Stop extends Entity {

    private char name;
    private int passengerCount;
    private Passenger[] passengers;

    public Stop(char name, int x, int y) {
        super();

        this.name = name;
        passengerCount = 0;
        passengers = new Passenger[9];

        this.x = x;
        this.y = y;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(TextWindow map) {
        int x = (int) this.x;
        int y = (int) this.y;

        map.output(x, y, name);
        map.output(x + 1, y + 1, Character.forDigit(passengerCount, 10));
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public Passenger[] getPassengers() {
        return passengers;
    }

    public void addPassenger(Passenger p) {
        passengerCount = Utils.addPassenger(passengers, passengerCount, p);

        boolean f = true;
        while (f) {
            f = false;

            for (int i = 0; i < passengerCount - 1; i++) {
                if (passengers[i].getLuggages().length < passengers[i + 1].getLuggages().length) {
                    Passenger tmp = passengers[i];
                    passengers[i] = passengers[i + 1];
                    passengers[i + 1] = tmp;
                    f = true;
                }
            }
        }
    }

    public Passenger removePassenger(Passenger p) {
        passengerCount = Utils.removePassenger(passengers, passengerCount, p);
        return p;
    }

    public char getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
