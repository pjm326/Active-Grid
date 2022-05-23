package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;

public class MKeyListener implements KeyListener {

    static OutputStream out= Calibrate.out;
    static int set= 1495;
    public static int move= 5;
    static int channel= 1;
    static int calib_array[]= Calibrate.calib_array;

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

        channel= Calibrate.get_channel();
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            set= set + move;
            System.out.println(set);
            try {
                out.write(Calibrate.open_panel(set, channel + 1));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            channel= Calibrate.get_channel();
            set= set - move;
            System.out.println(set);
            try {
                out.write(Calibrate.open_panel(set, channel + 1));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // channel= Calibrate.get_channel();
            // note than channel vs channel + 1 is a 1 unit discrpancy in the way the panel numbers
            // are kept track of - this can be
            System.out.println(channel);
            Calibrate.set_cal(set, channel);
            set= 1495;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
