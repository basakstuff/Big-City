import bus.Utils;
import bus.entities.Line;
import bus.entities.Passenger;
import bus.entities.Stop;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        Stop s1 = new Stop('a', 1, 1);
        Stop s2 = new Stop('b', 2, 1);
        Passenger pp = new Passenger(new Line(new Stop[]{
                s1, s2
        }), new Stop('b', 0, 0), new Stop('a', 0, 0));

        Passenger[] p = new Passenger[]{
                new Passenger(new Line(new Stop[]{
                        s1, s2
                }), new Stop('b', 0, 0), new Stop('a', 0, 0)),
                new Passenger(new Line(new Stop[]{
                        s1, s2
                }), new Stop('b', 0, 0), new Stop('a', 0, 0)),
                pp,
                new Passenger(new Line(new Stop[]{
                        s1, s2
                }), new Stop('b', 0, 0), new Stop('a', 0, 0)),
                null
        };

        System.out.println(Arrays.toString(p));

        Utils.removePassenger(p, 4, pp);
        System.out.println(Arrays.toString(p));
    }

}
