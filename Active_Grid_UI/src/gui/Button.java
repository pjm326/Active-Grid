package gui;

import javax.swing.JButton;

public class Button extends JButton {

    private int set_number;
    private int button_number;

    Button() {
        set_number= 0;
    }

    public int getSet() {
        return set_number;
    }

    public void setSet(int set) {
        set_number= set;
    }

    public int getNum() {
        return button_number;
    }

    public void setNum(int number) {
        button_number= number;
    }

}
