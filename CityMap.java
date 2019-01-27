package bus.core;

import bus.Main;
import bus.Utils;
import bus.entities.*;
import enigma.console.Console;
import enigma.console.TextWindow;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CityMap implements TextMouseListener {

    private static final int INFO_NONE = 0;
    private static final int INFO_STOP = 1;
    private static final int INFO_BUS = 2;

    private int currenState = INFO_NONE;
    private Stop inspectStop;
    private Bus inspectBus;

    private char[][] map;
    private int timestep;

    private Map<Character, Stop> stops;
    private Line[] lines;
    private Queue<Passenger> awaitingPassengers;

    public CityMap() {
        initialize();
    }

    private void initialize() {

        // load map
        map = loadMap();

        // extract stops
        Map<Character, Stop> _s = stops = new HashMap<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (Character.isLetter(map[i][j])) {                	
                    Stop bs = new Stop(map[i][j], j, i);
                    _s.put(bs.getName(), bs);
                    
                    map[i][j] = ' ';
                }
            }
        }

        // create the lines

        lines = new Line[]{
                new Line(
                        new Stop[]{
                                _s.get('A'), _s.get('E'), _s.get('I'),
                                _s.get('J'), _s.get('K'), _s.get('L')
                        }
                ),
                new Line(
                        new Stop[]{
                                _s.get('B'), _s.get('F'), _s.get('E'),
                                _s.get('I'), _s.get('J'), _s.get('N'),
                                _s.get('M')
                        }
                ),
                new Line(
                        new Stop[]{
                                _s.get('C'), _s.get('G'), _s.get('K'),
                                _s.get('J'), _s.get('F'), _s.get('E'),
                                _s.get('A')
                        }
                ),
                new Line(
                        new Stop[]{
                                _s.get('D'), _s.get('C'), _s.get('G'),
                                _s.get('G'), _s.get('K'), _s.get('J'),
                                _s.get('N'), _s.get('M')
                        }
                ),
                new Line(
                        new Stop[]{
                                _s.get('L'), _s.get('P'), _s.get('O'),
                                _s.get('K'), _s.get('G'), _s.get('C'),
                                _s.get('D'), _s.get('H')
                        }
                ),
                new Line(
                        new Stop[]{
                                _s.get('M'), _s.get('N'), _s.get('J'),
                                _s.get('F'), _s.get('G'), _s.get('K'),
                                _s.get('O'), _s.get('P')
                        }
                )
        };

        // awaiting passengers
        awaitingPassengers = new ArrayDeque<>(15);
        for (int i = 0; i < 15; i++)
            spawnPassenger();

    }

    public void update() {
        timestep++;

        if (ThreadLocalRandom.current().nextInt(1, 3) % 2 == 0)
            spawnPassenger();

        for (Line l : lines)
            l.update();
    }

    public void render(Console console) {
        TextWindow tw = console.getTextWindow();
        tw.setCursorPosition(0, 0);

        // clear
        clear(console, ' ');

        // map itself
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                console.getTextWindow().output(j, i, map[i][j]);
            }
        }

        // lines, stops and buses
        for (Line l : lines)
            l.render(tw);

        // info
        renderInfo(tw);
    }

    private void renderInfo(TextWindow tw) {
        tw.setCursorPosition(61, 0);

        // ==== general info
        // timestep
        tw.output(String.format("Time: %d", timestep));
        // awaiting passengers
        tw.setCursorPosition(61, 2);
        tw.output("New Passengers");
        tw.setCursorPosition(61, 3);
        tw.output("---------------");

        tw.setCursorPosition(59, 4);

        StringBuilder sb = new StringBuilder();
        for (Passenger p : awaitingPassengers)
            sb.append(p.getDeparture().getName());
        tw.output("> " + Utils.reverse(sb.toString()) + " >");

        tw.setCursorPosition(61, 5);
        tw.output("---------------");

        // waiting passengers at stops
        tw.setCursorPosition(61, 7);

        int a = 0, bb;
        for (Map.Entry<Character, Stop> s : stops.entrySet()) {
            a += s.getValue().getPassengerCount();
        }

        tw.output(String.format("Waiting    : %2d", a));

        a = 0;
        for (Line l : lines) {
            for (Bus b : l.getBuses())
                a += b.getPassengerCount();
        }

        tw.setCursorPosition(61, 8);
        tw.output(String.format("Travelling : %2d", a));

        tw.setCursorPosition(61, 9);
        bb = (int) Math.ceil(a * 100.0 / 96.0);
        tw.output(String.format("Bus fullness: %2d%c", bb, '%'));


        // ==== entity specific info

        int ly = 11;
        switch (currenState) {
            case INFO_STOP:

                tw.setCursorPosition(60, ly);

                tw.output(String.format("Bus Stop [%c] Passengers:", inspectStop.getName()));

                ly++;
                for (int i = 0; i < inspectStop.getPassengerCount(); i++) {
                    tw.setCursorPosition(60, ly + i);
                    tw.output(inspectStop.getPassengers()[i].toString());
                }

                break;
            case INFO_BUS:

                tw.setCursorPosition(60, ly);

                String busName = String.format("Bus [%s] ", inspectBus.getName());
                tw.output(String.format("%sPassengers:", busName));

                ly++;
                for (int i = 0; i < inspectBus.getPassengerCount(); i++) {
                    tw.setCursorPosition(60, ly + i);
                    tw.output(inspectBus.getPassengers()[i].toString().replaceAll("Line .", ""));
                }

                ly += 9;
                tw.setCursorPosition(60, ly);
                tw.output(String.format("%sLuggage:", busName));

                tw.setCursorPosition(tw.getCursorX() + 10, ly + 1);
                tw.output("-----");

                ly = tw.getCursorY() - 1;
                Luggage[] luggages = inspectBus.getLuggages().toArray(new Luggage[]{});
                tw.setCursorPosition(tw.getCursorX(), ly);
                for (int i = 0; i < 9; i++) {
                    tw.setCursorPosition(tw.getCursorX() - 5, ly--);
                    if (i < luggages.length) {
                        tw.output(String.format("|%3d|", luggages[i].getId()));
                    } else {
                        tw.output("|   |");
                    }
                }

                break;
        }

    }

    private void clear(Console console, char c) {
        TextWindow tw = console.getTextWindow();

        for (int i = 0; i < tw.getRows(); i++) {
            for (int j = 0; j < tw.getColumns(); j++) {
                console.getTextWindow().output(j, i, ' ');
            }
        }
    }

    private void spawnPassenger() {
        if (awaitingPassengers.size() == 15) {
            // pop!
            Passenger p = awaitingPassengers.remove();
            if (p.getDeparture().getPassengerCount() <= 8)
                p.getDeparture().addPassenger(p);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        Line l = lines[random.nextInt(lines.length)];
        Stop[] randLine = l.getStops();
        Stop randStop = randLine[random.nextInt(randLine.length)];
        Stop randStop2;

        do {
            randStop2 = randLine[random.nextInt(randLine.length)];
        } while (randStop.equals(randStop2));

        Passenger p = new Passenger(l, randStop, randStop2);
        awaitingPassengers.add(p);
    }

    private char[][] loadMap() {
        URL url = Main.class.getClassLoader().getResource("bus/res/map.txt");
        File f = new File(url.getPath());

        int i = 0;
        char[][] map = new char[22][];

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = Utils.rtrim(line);
                if (line.length() > 0)
                    map[i++] = line.toCharArray();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return map;
    }

    @Override
    public void mouseClicked(TextMouseEvent textMouseEvent) {

    }

    @Override
    public void mousePressed(TextMouseEvent textMouseEvent) {

        Point p = textMouseEvent.getPoint();
        for (Map.Entry<Character, Stop> e : stops.entrySet()) {
            Stop s = e.getValue();

            if ((int) s.getX() == p.x && (int) s.getY() == p.y) {
                inspectStop = s;
                currenState = INFO_STOP;
                return;
            }
        }

        for (Line l : lines) {
            for (Bus b : l.getBuses())
                if ((int) b.getX() == p.x && (int) b.getY() == p.y) {
                    inspectBus = b;
                    currenState = INFO_BUS;
                    return;
                }
        }

        inspectBus = null;
        inspectStop = null;
        currenState = INFO_NONE;

    }

    @Override
    public void mouseReleased(TextMouseEvent textMouseEvent) {

    }

}
