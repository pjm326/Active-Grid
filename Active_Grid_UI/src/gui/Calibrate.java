package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fazecast.jSerialComm.SerialPort;

//calibrate class handles creating and populating the calibrate window

public class Calibrate {

    public static int calib_button= 1;
    static Color button_select= new Color(0, 0, 200);
    public static int default_calib= 1495;
    static int calib_array[]= new int[64];
    public static JFrame calibrate_frame= new JFrame();
    static DefaultTableModel default_table= new DefaultTableModel(64, 2);
    static int size_x= 1200;
    static int size_y= 750;
    static int num_panels= 64;
    static int button_size= 50;
    static Color original_color= new Color(202, 172, 139);
    static int gap= 10;
    static Preferences calibrationData= Preferences.userRoot();
    static Hashtable<Integer, Button> calibration_buttons= new Hashtable<>();

    // related to arrow key input
    public static SerialPort port= Command_center.port;
    public static OutputStream out= Command_center.out;

    public static void run_calibration() {

        JTextField textField= new JTextField();
        calibrate_frame.setFocusable(true);
        calibrate_frame.addKeyListener(new MKeyListener());
        calibrate_frame.setFocusable(true);
        calibrate_frame.requestFocus();
        calibrate_frame.add(textField);

        Command_center.serialControl(port);

        calibrate_frame.setTitle("Calibrate and Test");
        calibrate_frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        calibrate_frame.setSize(1200, 750);
        calibrate_frame.setLayout(null);
        calibrate_frame.setResizable(false);
        calibrate_frame.revalidate();
        JScrollPane scroll_pane= new JScrollPane();
        String[] columnNames= { "Winglet",
                "'Zero'" };

        default_table.setColumnIdentifiers(columnNames);
        JTable movement_table= new JTable(default_table);
        scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_pane.setBounds(800, 150, 200, 400);
        calibrate_frame.add(scroll_pane);
        scroll_pane.getViewport().add(movement_table);
        movement_table.setEnabled(false);

        Button save_mov= new Button();
        save_mov.setBounds(size_x - 280, 580, 60, 30);
        save_mov.setBackground(new Color(153, 153, 153));
        save_mov.setBorderPainted(false);
        save_mov.setOpaque(true);
        save_mov.setFont(new Font("Arial", Font.BOLD, 14));
        save_mov.setText("Save");
        save_mov.setFocusPainted(false);
        save_mov.setMargin(new Insets(5, 5, 5, 5));

        Button edit_mov= new Button();
        edit_mov.setBounds(size_x - 210, 580, 60, 30);
        edit_mov.setBackground(new Color(153, 153, 153));
        edit_mov.setBorderPainted(false);
        edit_mov.setOpaque(true);
        edit_mov.setFont(new Font("Arial", Font.BOLD, 14));
        edit_mov.setText("Edit");
        edit_mov.setFocusPainted(false);
        edit_mov.setMargin(new Insets(5, 5, 5, 5));

        edit_mov.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                movement_table.setEnabled(true);
                calibrate_frame.requestFocus();
            }

        });

        save_mov.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // movement_table.setDefaultEditor(Object.class, null);
                movement_table.setEnabled(false);
                storeCalibration();
                save_calibration_table();
                save_mov.setBackground(new Color(153, 153, 153));
                calibrate_frame.requestFocus();

            }

        });

        for (int i= 0; i < num_panels; i++ ) {

            float[] pos= new float[2];
            pos= Main.button_location(i, gap, num_panels, button_size, size_x, size_y);
            Button btn= new Button();
            btn.setBounds((int) pos[0], (int) pos[1], button_size, button_size);
            btn.setBackground(original_color);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setNum(i);
            calibration_buttons.put(i, btn);

            calibrate_frame.add(btn);

            // add action listeners for all of them
            btn.addActionListener(new ActionListener() {

                @Override

                public void actionPerformed(ActionEvent e) {

                    Button other_button= calibration_buttons.get(calib_button);
                    other_button.setBackground(original_color);
                    calib_button= btn.getNum();
                    btn.setBackground(button_select);
                    save_mov.setBackground(new Color(200, 0, 0));
                    calibrate_frame.requestFocus();

                }
            });

        }

        // move this to main
        loadCalibration();

        calibrate_frame.add(edit_mov);
        calibrate_frame.add(save_mov);
        calibrate_frame.setVisible(true);

        try {
            // out.write(set_servo_mode_ch1);

            String set_mode= "AAA055040164";
            for (int i= 0; i < 64; i++ ) {
                set_mode= set_mode + "19";

            }

            byte[] set_mode_command= hexStringToByteArray(set_mode);

            out.write(set_mode_command);

            out.write(set_speed(255, 50));
            Thread.sleep(10);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // load the calibration data from memory into the local calibration data table
    public static void init_calibration_table() {

        for (int i= 0; i < 64; i++ ) {

            int calib= calibrationData.getInt("winglet" + Integer.toString(i), -999);
            if (calib == -999) {
                calibrationData.putInt("winglet" + Integer.toString(i), 1495);
            }
            calib= calibrationData.getInt("winglet" + Integer.toString(i), 0);
            calib_array[i]= calib;
            // default_table.setValueAt(i + 1, i, 0);
            // default_table.setValueAt(calib, i, 1);
        }
    }

    // save data in the calibration array to memory
    public static void save_calibration_table() {

        for (int i= 0; i < 64; i++ ) {

            int table_number;
            try {
                String table_value= default_table.getValueAt(i, 1).toString();
                table_number= Integer.parseInt(table_value);

            } catch (Exception e) {
                table_number= default_calib;
            }

            calib_array[i]= table_number;
            calibrationData.putInt("winglet" + Integer.toString(i), table_number);

        }
    }

    // take data from the calibration window and store it in the calibration array
    public static void storeCalibration() {

        for (int i= 0; i < 64; i++ ) {

            int table_number;
            try {
                String table_value= default_table.getValueAt(i, 1).toString();
                table_number= Integer.parseInt(table_value);
            } catch (Exception e) {
                table_number= default_calib;
            }

            calib_array[i]= table_number;

        }

    }

    // load in the calibration table in the calibration window
    public static void loadCalibration() {

        for (int i= 0; i < 64; i++ ) {

            default_table.setValueAt(i + 1, i, 0);
            default_table.setValueAt(calib_array[i], i, 1);
        }

    }

    public static void set_cal(int cal, int channel) {
        calib_array[channel]= cal;
        loadCalibration();
    }

    public static int get_cal(int channel) {
        return calib_array[channel - 1];
    }

    public static int get_channel() {
        return calib_button;
    }

    public static byte[] set_speed(int req_speed, int channel) {

        String channel_hex= Integer.toHexString(channel);
        channel_hex= channel_hex.toUpperCase();
        if (channel_hex.length() % 2 != 0) {
            channel_hex= '0' + channel_hex;

        }
        String hex_command= Integer.toHexString(req_speed);
        hex_command= hex_command.toUpperCase();
        if (hex_command.length() % 2 != 0) {
            hex_command= '0' + hex_command;
        }

        byte[] command= hexStringToByteArray(
            "AAA05503" + channel_hex + "01" + hex_command);
//        System.out.println(
//            "AAA05503" + channel_hex + "01" + hex_command);

        return command;

    }

    public static byte[] open_panel(float angle, int channel) {

        float temp= angle;
        // float start_pot= (angle + 90) / 180 * 1260 + 865;
        // int start= (int) start_pot;
        System.out.println("openning");
        System.out.println(angle);
        String channel_hex= Integer.toHexString(channel);
        channel_hex= channel_hex.toUpperCase();
        if (channel_hex.length() % 2 != 0) {
            channel_hex= '0' + channel_hex;

        }
        String hex_command= Integer.toHexString((int) temp);
        hex_command= hex_command.toUpperCase();
        if (hex_command.length() % 2 != 0) {
            hex_command= '0' + hex_command;
        }
        // String byte_count= Integer.toString(hex_command.length());
        String high_byte= hex_command.substring(0, 2);
        String low_byte= hex_command.substring(2);

        byte[] command= hexStringToByteArray(
            "AAA05501" + channel_hex + "02" + low_byte + high_byte);
//        System.out.println(
//            "AAA05501" + channel_hex + "02" + low_byte + high_byte);

        return command;

    }

    public static byte[] hexStringToByteArray(String s) {
        int len= s.length();
        byte[] data= new byte[len / 2];
        for (int i= 0; i < len; i+= 2) {
            data[i / 2]= (byte) ((Character.digit(s.charAt(i), 16) << 4) +
                Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }
}
