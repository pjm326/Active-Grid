package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;

public class MKeyListener extends KeyAdapter {

    public static OutputStream out= Main.out;

    @Override
    public void keyPressed(KeyEvent event) {

        char ch= event.getKeyChar();

        if (ch == 'a' || ch == 'b' || ch == 'c') {

            System.out.println(event.getKeyChar());

        }

        if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            try {

                int set= 1500;
                int move= 4;
                set= set + move;
                out.write(open_panel(set, 1));

            } catch (IOException z) {
                // TODO Auto-generated catch block
                z.printStackTrace();
            }

        }
    }
}