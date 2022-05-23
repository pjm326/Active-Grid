package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

// look at documentation for JFileChooser
public class Read_Write {

    public static Hashtable<Integer, Button> panel_buttons= Main.panel_buttons;
    public static Hashtable<Integer, Button> parameter_set_buttons= Main.parameter_set_buttons;
    public static Hashtable<Integer, Color> button_colors= Main.button_colors;
    public static Hashtable<Integer, Parameter_Set> Parameter_Sets= Main.Parameter_Sets;
    public static Counter num_sets= Main.num_sets;
    public static int num_panels= Main.num_panels;

    public static void read_file(File config_file) {

        // initialize variables from file
        FileInputStream stream= null;

        try {
            stream= new FileInputStream(config_file.getPath());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader reader= new BufferedReader(new InputStreamReader(stream));
        String strLine;
        ArrayList<String> lines= new ArrayList<>();

        try {
            strLine= reader.readLine();
            while (strLine != null) {
                lines.add(strLine);
                strLine= reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int buttons_header= lines.indexOf("Buttons");
        int end_buttons= lines.indexOf("End Buttons");
        int end_movements= lines.indexOf("End Movements");

        for (int i= buttons_header + 1; i < end_buttons; i++ ) {

            String data= lines.get(i);
            int colon= data.indexOf(":");
            int button_key= Integer.parseInt(data.substring(0, colon));
            int button_set= Integer.parseInt(data.substring(colon + 1, data.length()));
            panel_buttons.get(button_key).setSet(button_set);

            if (button_set != 0 && button_set != -1) {

                if (Parameter_Sets.get(button_set) == null) {
                    Parameter_Set new_param_set= new Parameter_Set(button_set,
                        button_colors.get(button_set));
                    new_param_set.addButton(button_key, panel_buttons.get(button_key));
                    Parameter_Sets.put(button_set, new_param_set);

                } else {
                    Parameter_Sets.get(button_set).addButton(button_key,
                        panel_buttons.get(button_key));

                }

            }

            if (button_set > num_sets.getCount()) {
                num_sets.setCount(button_set);
            }

        }

        for (int i= 1; i <= num_sets.getCount(); i++ ) {

            if (Parameter_Sets.get(i) == null) {
                Parameter_Set new_param_set= new Parameter_Set(i,
                    button_colors.get(i));
                Parameter_Sets.put(i, new_param_set);

            }

            Button new_set= new Button();
            int new_X= panel_buttons.get(i).getX();
            new_set.setBounds(new_X, 110, 50, 20);
            Color background= button_colors.get(i);
            new_set.setBackground(background);
            new_set.setBorderPainted(false);
            new_set.setOpaque(true);
            new_set.setFont(new Font("Arial", Font.BOLD, 6));
            new_set.setText("Set " + Integer.toString(i));
            new_set.setSet(i);
            new_set.setFocusPainted(false);
            parameter_set_buttons.put(i, new_set);
            Main.main_frame.add(new_set);

            new_set.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    int set= new_set.getSet();
                    int active_set= Active_Set.get_active_set();
                    if (set == active_set) {
                        Active_Set.set_active_set(-1);
                    } else {
                        Active_Set.set_active_set(set);
                    }
                }
            });
        }

        for (int i= end_buttons + 1; i < end_movements; i++ ) {

            String data= lines.get(i);
            int space= data.indexOf(" ");

            if (space == -1) {
                continue;
            }

            String data_ref= data.substring(0, space);
            int set_ref= Integer.parseInt(data.substring(space + 1, data.length()));
            String time_data= "";
            String move_data= "";
            String time_data_extra= "";
            String move_data_extra= "";
            int time_comma_idx= 0;
            int move_comma_idx= 0;
            int data_count= 0;

            if (data_ref.equals("Movements")) {
                time_data= lines.get(i + 1);
                move_data= lines.get(i + 2);

                data_count= (int) time_data.chars().filter(ch -> ch == ',').count();

                int[][] load_movements= new int[data_count][2];

                for (int j= 0; j < data_count; j++ ) {

                    time_comma_idx= time_data.indexOf(',');
                    move_comma_idx= move_data.indexOf(",");

                    int time_splice= Integer.parseInt(time_data.substring(0, time_comma_idx));
                    int move_splice= Integer.parseInt(move_data.substring(0, move_comma_idx));
                    load_movements[j][0]= time_splice;
                    load_movements[j][1]= move_splice;

                    time_data_extra= time_data.substring(time_comma_idx + 1, time_data.length());
                    move_data_extra= move_data.substring(move_comma_idx + 1, move_data.length());
                    time_data= time_data_extra;
                    move_data= move_data_extra;

                }

                Parameter_Sets.get(set_ref).setMovement(load_movements);

            }

        }

        Active_Set.update();
        Main.main_frame.repaint();
        Main.main_frame.setVisible(true);
    }

    public static void write_file(File state_file) {

        ArrayList<String> data_to_save= new ArrayList<>();
        data_to_save.add("Buttons");
        for (int i= 0; i < num_panels; i++ ) {
            int j= i + 1;
            int button_set= Main.panel_buttons.get(j).getSet();
            data_to_save.add(Integer.toString(j) + ":" + Integer.toString(button_set));
        }

        data_to_save.add("End Buttons");

        int[][] load_movements;
        String times= "";
        String moves= "";

        Enumeration<Integer> f= Parameter_Sets.keys();

        while (f.hasMoreElements()) {
            int set= f.nextElement();
            data_to_save.add("Movements " + Integer.toString(set));
            load_movements= Parameter_Sets.get(set).getMovement();
            for (int i= 0; i < load_movements.length; i++ ) {
                times+= Integer.toString(load_movements[i][0]);
                times+= ",";
                moves+= Integer.toString(load_movements[i][1]);
                moves+= ",";

            }

            data_to_save.add(times);
            data_to_save.add(moves);

            times= "";
            moves= "";

        }

        data_to_save.add("End Movements");

        FileOutputStream stream= null;

        try {
            stream= new FileOutputStream(state_file.getPath());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedWriter writer= new BufferedWriter(new OutputStreamWriter(stream));

        for (int i= 0; i < data_to_save.size(); i++ ) {
            try {
                writer.write(data_to_save.get(i));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                writer.newLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        try {
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
