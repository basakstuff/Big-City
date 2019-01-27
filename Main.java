package bus;

import bus.core.CityMap;
import enigma.console.Console;
import enigma.console.TextAttributes;
import enigma.core.Enigma;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main implements KeyListener {

    private static final double UNIT_TIME = 1.0;

    private static final int STATE_ONE_STEP = 0;
    private static final int STATE_RUNNING = 1;
    private static final int STATE_PAUSED = 2;

    private int currentState = STATE_ONE_STEP;

    public void run() throws InterruptedException {
        CityMap map = new CityMap();

        Console console = Enigma.getConsole("Big City");
        TextAttributes txtAttr = new TextAttributes(Color.WHITE, Color.BLUE);

        console.setTextAttributes(txtAttr);
        console.getTextWindow().addKeyListener(this);
        console.getTextWindow().addTextMouseListener(map);

        // ===

        while (true) {
            if (currentState != STATE_PAUSED) {

                map.update();
                map.render(console);

                if (currentState == STATE_RUNNING) {
                    Thread.sleep((long) (UNIT_TIME * 500));
                } else if (currentState == STATE_ONE_STEP) {
                    currentState = STATE_PAUSED;
                }

            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        // ===

    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
            currentState = STATE_ONE_STEP;
        } else if (currentState == STATE_PAUSED && key == KeyEvent.VK_R) {
            currentState = STATE_RUNNING;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    public static void main(String[] args) throws InterruptedException {
        Main m = new Main();
        m.run(); 
    }
}
