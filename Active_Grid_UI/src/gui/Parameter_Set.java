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
    Color base_color;
    int set_number;

    Parameter_Set(int set_number, Color button_color) {
        this.button_color= button_color;
        this.set_number= set_number;
        base_color= button_color;
    }

    public void update() {
        if (button_set != null) {
            Enumeration<Integer> e= button_set.keys();
            while (e.hasMoreElements()) {
                int iter= e.nextElement();
                button_set.get(iter).setBackground(button_color);
                button_set.get(iter).setSet(set_number);

            }
        }
    }

    public void setColor(Color color) {
        button_color= color;
        update();
    }

    public Color getColor() {
        return button_color;
    }

    public void setMovement(int[][] mov_vector) {
        movement= mov_vector;

    }

    public int[][] getMovement() {
        return movement;
    }

    public void addButton(int i, Button button) {
        button_set.put(i, button);
        update();

    }

    public void removeButton(int i) {
        button_set.get(i).setBackground(original_color);
        button_set.get(i).setSet(0);
        button_set.remove(i);

    }

    public Hashtable<Integer, Button> getSet() {
        return button_set;
    }

    public void setNumber(int num) {
        set_number= num;
        update();
    }

    public int getNumber() {
        return set_number;
    }

//    public void change_button_set(Hashtable<Integer, Button> new_set) {
//        button_set= new_set;
//        update();
//    }

}
