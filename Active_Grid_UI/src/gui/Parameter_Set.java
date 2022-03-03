package gui;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

/* Parameter_Set is a collection of Button Objects. The Button objects themselves are extensions
 * // of the JButton object
 */
public class Parameter_Set {

    int[][] movement;
    Hashtable<Integer, Button> button_set= new Hashtable<>();
    Color original_color= Main.original_color;
    Color button_color;
    boolean lock= false;
    int set_number;
    // Hashtable<Integer, Button> panel_buttons= Main.panel_buttons;

    Parameter_Set(int set_number, Color button_color) {
        this.button_color= button_color;
        this.set_number= set_number;
    }

    public void update_colors() {
        if (button_set != null) {
            Enumeration<Integer> e= button_set.keys();
            while (e.hasMoreElements()) {
                button_set.get(e.nextElement()).setBackground(button_color);
            }

            // need to include a color change

        }

    }

    public void update_set() {
        if (button_set != null) {
            Enumeration<Integer> e= button_set.keys();
            while (e.hasMoreElements()) {
                button_set.get(e.nextElement()).setSet(set_number);
            }

            // need to include a color change

        }

    }

    public void setColor(Color color) {
        button_color= color;
        update_colors();
    }

    public Color getColor() {
        return button_color;
    }

    public void setMovement(int[][] mov_vector) {
        // System.out.println("here in " + Integer.toString(set_number) + " the vector is");
        // System.out.println(mov_vector);
        movement= mov_vector;
        // System.out.println("for some reason movement is saving to:");
        // System.out.println(movement);
    }

    public int[][] getMovement() {
        // System.out.println("for some reason movement is updated to:");
        // System.out.println(movement);
        return movement;
    }

    public void addButton(int i, Button button) {
        button_set.put(i, button);
        update_set();
        update_colors();
    }

    public void removeButton(int i) {
        button_set.get(i).setBackground(original_color);
        button_set.get(i).setSet(0);
        button_set.remove(i);

    }

    public Hashtable<Integer, Button> getSet() {
        return button_set;
    }

}
