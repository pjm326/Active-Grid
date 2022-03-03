package gui;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

// handles all updates required when the active set number changes
public class Active_Set {

    public static int active_set= -1;
    public static Hashtable<Integer, Parameter_Set> Parameter_Sets= Main.Parameter_Sets;
    public static Hashtable<Integer, JButton> parameter_set_buttons= Main.parameter_set_buttons;
    public static Hashtable<Integer, Color> button_colors= Main.button_colors;
    public static DefaultTableModel default_table= Main.default_table;

    public static int get_active_set() {
        return active_set;
    }

    public static void set_active_set(int set) {
        // System.out.println("set_active_set called");
        save_movements();
        active_set= set;
        load_movements();
        update();

    }

    public static void update() {
        // update all button colors according to their set
        Main.update_click_color(); // I think this can be removed
        if (Parameter_Sets != null) {
            Enumeration<Integer> e= Parameter_Sets.keys();
            while (e.hasMoreElements()) {
                int key_number= e.nextElement();
                // System.out.print(key_number);
                Color start_color= button_colors.get(key_number);
                if (key_number != active_set) {
                    float hsbVals[]= Color.RGBtoHSB(start_color.getRed(),
                        start_color.getGreen(), start_color.getBlue(), null);
                    Color lighter= Color.getHSBColor(hsbVals[0], hsbVals[1],
                        0.4f * hsbVals[2]);
                    parameter_set_buttons.get(key_number).setBackground(lighter);
                    Parameter_Sets.get(key_number).setColor(lighter);

                } else {
                    parameter_set_buttons.get(key_number).setBackground(start_color);
                    Parameter_Sets.get(key_number).setColor(start_color);
                }

            }

            // need to include a color change

        }
    }

    public static void save_movements() {
        // System.out.println(active_set);

        int cols= default_table.getColumnCount();
        int rows= default_table.getRowCount();
        int[][] save_movements= new int[rows][cols];
        for (int i= 0; i < cols; i++ ) {
            for (int j= 0; j < rows; j++ ) {

                try {
                    String table_value= default_table.getValueAt(j, i).toString();
                    int table_number= Integer.parseInt(table_value);
                    save_movements[j][i]= table_number;

                } catch (Exception e) {

                    save_movements[j][i]= 0;
                }

            }

            // add code here to check that all values are possible and adjust matrix
        }

        if (active_set != -1) {

            Parameter_Sets.get(active_set).setMovement(save_movements);
            // System.out.println("setMovement was called for " + Integer.toString(active_set));

        }

    }

    public static void load_movements() {

        if (active_set != -1) {
            int[][] load_movements= Parameter_Sets.get(active_set).getMovement();
            // System.out.println(Parameter_Sets.get(active_set));
            // System.out.println("getMovement was called for " + Integer.toString(active_set));

            if (load_movements != null) {
                // System.out.println("here");
                int rows= load_movements.length;
                int cols= default_table.getColumnCount();
                default_table.setRowCount(rows);
                // default_table.setDataVector(load_mov_vector, columnNames);
                for (int i= 0; i < cols; i++ ) {
                    for (int j= 0; j < rows; j++ ) {
                        default_table.setValueAt(load_movements[j][i], j, i);
                    }

                }

            } else {
                // System.out.println("null detected");
                default_table.setRowCount(0);
                default_table.setRowCount(5);
            }
        }
    }

}
