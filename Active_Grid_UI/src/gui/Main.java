package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class Main {

    public static Hashtable<Integer, Button> panel_buttons= new Hashtable<>();
    public static Hashtable<Integer, JButton> parameter_set_buttons= new Hashtable<>();
    public static Hashtable<Integer, Color> button_colors= new Hashtable<>();
    public static Hashtable<Integer, Parameter_Set> Parameter_Sets= new Hashtable<>();
    public static Color original_color= new Color(202, 172, 139);
    public static Counter num_sets= new Counter();
    public static DefaultTableModel default_table= new DefaultTableModel(5, 2);

    public static JFrame init_frame= new JFrame();
    public static JFrame main_frame= new JFrame();

    public static int num_panels= 64;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        button_colors.put(0, new Color(153, 153, 153));
        button_colors.put(1, new Color(255, 0, 0));
        button_colors.put(2, new Color(0, 255, 0));
        button_colors.put(3, new Color(51, 153, 255));
        button_colors.put(4, new Color(255, 128, 0));
        button_colors.put(5, new Color(204, 0, 204));
        button_colors.put(6, new Color(51, 255, 255));
        button_colors.put(7, new Color(255, 255, 51));
        button_colors.put(8, new Color(103, 254, 104));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        init_window();
    }

    public static void init_window() {

        JFileChooser read_fc= new JFileChooser();
        int size_x= 1200;
        int size_y= 750;
        int width_x= 300;
        int width_y= 120;
        init_frame.setTitle("Active Grid GUI");
        init_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init_frame.setSize(1200, 750);
        init_frame.setLayout(null);
        init_frame.setResizable(false);
        JButton new_session= new JButton();
        JButton load_session= new JButton();
        ImageIcon image= new ImageIcon(Main.class.getResource("grid_pic.png"));
        JLabel grid_pic= new JLabel(image);
        int pic_x= 500;
        int pic_y= 500;
        grid_pic.setBounds(size_x / 2, size_y / 2 - pic_y / 2, 500, 400);
        grid_pic.setOpaque(true);

        load_session.setBounds(size_x / 4 - width_x / 2, size_y / 2 + 50, width_x,
            width_y);
        load_session.setBackground(new Color(154, 123, 168));
        load_session.setBorderPainted(false);
        load_session.setOpaque(true);
        load_session.setFont(new Font("Arial", Font.BOLD, 25));
        load_session.setText("Load Session");
        load_session.setFocusPainted(false);

        new_session.setBounds(size_x / 4 - width_x / 2, size_y / 2 - width_y - 50, width_x,
            width_y);
        new_session.setBackground(new Color(154, 123, 168));
        new_session.setBorderPainted(false);
        new_session.setOpaque(true);
        new_session.setFont(new Font("Arial", Font.BOLD, 25));
        new_session.setText("New Session");
        new_session.setFocusPainted(false);

        init_frame.add(load_session);
        init_frame.add(new_session);
        init_frame.add(grid_pic);
        init_frame.revalidate();
        init_frame.setVisible(true);

        new_session.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                init_frame.setVisible(false);
                main_window();

            }
        });

        load_session.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal= read_fc.showOpenDialog(main_frame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file= read_fc.getSelectedFile();
                    Read_Write.read_file(file);

                }
            }
        });

    }

    public static void main_window() {

        JFileChooser write_fc= new JFileChooser();
        int size_x= 1200;
        int size_y= 750;
        int gap= 10;
        int button_size= 50;

        main_frame.setTitle("Main Frame Active Grid GUI");
        main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_frame.setSize(size_x, size_y);
        main_frame.setResizable(false);
        main_frame.setLayout(null);

        UIManager.put("Button.select", original_color);

        // loop creates all the buttons
        for (int i= 0; i < num_panels; i++ ) {
            final int j= i;
            float[] pos= new float[2];
            pos= button_location(i, gap, num_panels, button_size, size_x, size_y);
            Button btn= new Button();
            btn.setBounds((int) pos[0], (int) pos[1], button_size, button_size);
            btn.setBackground(original_color);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setNum(j);
            panel_buttons.put(j + 1, btn);
            main_frame.add(btn);

            btn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // Color color= btn.getBackground();

                    int active_set= Active_Set.get_active_set();
                    int current_set= btn.getSet();
                    btn.setFont(new Font("Arial", Font.BOLD, 25));
                    // UIManager.put("Button.select", button_colors.get(active_set));

                    if (current_set > 0 && current_set == active_set) {
                        Parameter_Sets.get(active_set).removeButton(j + 1);

                    } else if (current_set > 0 && current_set != active_set) {

                    } else if (current_set == 0 && active_set > 0) {
                        Parameter_Sets.get(active_set).addButton(j + 1, btn);

                    } else if (active_set == -1 && btn.getText() == "x" && current_set == -1) {
                        btn.setText("");
                        btn.setSet(0);
                        // set movement for buttons individually
                    } else if (active_set == -1 && current_set == 0) {
                        btn.setText("x");
                        btn.setSet(-1);

                    }

                    // need to also remove this button from the set

                }
            });

        }

        Button add_set= new Button();
        int add_set_X= panel_buttons.get(8).getX();
        int add_set_Y= panel_buttons.get(8).getY();
        add_set.setBounds(add_set_X + 70, add_set_Y, 120, 40);
        add_set.setBackground(button_colors.get(0));
        add_set.setBorderPainted(false);
        add_set.setOpaque(true);
        add_set.setFont(new Font("Arial", Font.BOLD, 12));
        add_set.setText("Add Set (+)");
        add_set.setFocusPainted(false);

        Button clear_set= new Button();
        clear_set.setBounds(add_set_X + 70, add_set_Y + 50, 120, 40);
        clear_set.setBackground(button_colors.get(0));
        clear_set.setBorderPainted(false);
        clear_set.setOpaque(true);
        clear_set.setFont(new Font("Arial", Font.BOLD, 12));
        clear_set.setText("Clear Set");
        clear_set.setFocusPainted(false);

        Button reset= new Button();
        reset.setBounds(add_set_X + 70, add_set_Y + 150, 120, 40);
        reset.setBackground(button_colors.get(0));
        reset.setBorderPainted(false);
        reset.setOpaque(true);
        reset.setFont(new Font("Arial", Font.BOLD, 12));
        reset.setText("Reset");
        reset.setFocusPainted(false);

        Button remove_set= new Button();
        remove_set.setBounds(add_set_X + 70, add_set_Y + 100, 120, 40);
        remove_set.setBackground(button_colors.get(0));
        remove_set.setBorderPainted(false);
        remove_set.setOpaque(true);
        remove_set.setFont(new Font("Arial", Font.BOLD, 12));
        remove_set.setText("Remove Set (-)");
        remove_set.setFocusPainted(false);

        Button run= new Button();
        run.setBounds(size_x - 220, size_y - 140, 120, 40);
        run.setBackground(new Color(0, 153, 0));
        run.setBorderPainted(false);
        run.setOpaque(true);
        run.setFont(new Font("Arial", Font.BOLD, 12));
        run.setText("RUN");
        run.setFocusPainted(false);

        Button save_config= new Button();
        save_config.setBounds(size_x - 360, size_y - 140, 120, 40);
        save_config.setBackground(new Color(255, 153, 153));
        save_config.setBorderPainted(false);
        save_config.setOpaque(true);
        save_config.setFont(new Font("Arial", Font.BOLD, 12));
        save_config.setText("Save Config");
        save_config.setFocusPainted(false);

        Button add_row= new Button();
        add_row.setBounds(size_x - 360, add_set_Y + 410, 30, 30);
        add_row.setBackground(button_colors.get(0));
        add_row.setBorderPainted(false);
        add_row.setOpaque(true);
        add_row.setFont(new Font("Arial", Font.BOLD, 20));
        add_row.setText("+");
        add_row.setFocusPainted(false);
        add_row.setMargin(new Insets(5, 5, 5, 5));

        Button delete_row= new Button();
        delete_row.setBounds(size_x - 320, add_set_Y + 410, 30, 30);
        delete_row.setBackground(button_colors.get(0));
        delete_row.setBorderPainted(false);
        delete_row.setOpaque(true);
        delete_row.setFont(new Font("Arial", Font.BOLD, 20));
        delete_row.setText("-");
        delete_row.setFocusPainted(false);
        delete_row.setMargin(new Insets(5, 5, 5, 5));

        Button save_mov= new Button();
        save_mov.setBounds(size_x - 280, add_set_Y + 410, 60, 30);
        save_mov.setBackground(button_colors.get(0));
        save_mov.setBorderPainted(false);
        save_mov.setOpaque(true);
        save_mov.setFont(new Font("Arial", Font.BOLD, 14));
        save_mov.setText("Save");
        save_mov.setFocusPainted(false);
        save_mov.setMargin(new Insets(5, 5, 5, 5));

        Button edit_mov= new Button();
        edit_mov.setBounds(size_x - 210, add_set_Y + 410, 60, 30);
        edit_mov.setBackground(button_colors.get(0));
        edit_mov.setBorderPainted(false);
        edit_mov.setOpaque(true);
        edit_mov.setFont(new Font("Arial", Font.BOLD, 14));
        edit_mov.setText("Edit");
        edit_mov.setFocusPainted(false);
        edit_mov.setMargin(new Insets(5, 5, 5, 5));

        main_frame.add(reset);
        main_frame.add(remove_set);
        main_frame.add(save_config);
        main_frame.add(run);
        main_frame.add(add_set);
        main_frame.add(clear_set);
        main_frame.add(add_row);
        main_frame.add(delete_row);
        main_frame.add(save_mov);
        main_frame.add(edit_mov);
        main_frame.setVisible(true);

        JScrollPane scroll_pane= new JScrollPane();
        String[] columnNames= { "Time (ms)",
                "Angle (-90 to 90 degrees)" };
        default_table.setColumnIdentifiers(columnNames);
        JTable movement_table= new JTable(default_table);
        scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_pane.setBounds(size_x - 360, add_set_Y, 200, 400);
        main_frame.add(scroll_pane);
        scroll_pane.getViewport().add(movement_table);
        movement_table.setEnabled(false);

        clear_set.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int active_set= Active_Set.get_active_set();
                if (active_set > 0) {
                    Hashtable<Integer, Button> clear_set= Parameter_Sets.get(active_set).button_set;
                    Enumeration<Integer> f= clear_set.keys();
                    while (f.hasMoreElements()) {
                        Parameter_Sets.get(active_set).removeButton(f.nextElement());
                    }
                }

            }

        });

        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                main_window();

            }

        });

        remove_set.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int active_set= Active_Set.get_active_set();
                if (active_set > 0) {
                    Hashtable<Integer, Button> clear_set= Parameter_Sets.get(active_set).button_set;
                    Enumeration<Integer> f= clear_set.keys();
                    while (f.hasMoreElements()) {
                        Parameter_Sets.get(active_set).removeButton(f.nextElement());

                    }
                    main_frame.remove(parameter_set_buttons.get(active_set));
                    main_frame.repaint();
                    Active_Set.set_active_set(-1);
                    Parameter_Sets.remove(active_set);
                    parameter_set_buttons.remove(active_set);
                    System.out.print("removed " + Integer.toString(active_set));
                }

            }

        });

        add_set.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                movement_table.setEnabled(true);

                num_sets.increment();
                Button new_set= new Button();
                int new_X= panel_buttons.get(num_sets.getCount()).getX();
                new_set.setBounds(new_X, 110, 50, 20);
                Color background= button_colors.get(num_sets.getCount());
                new_set.setBackground(background);
                new_set.setBorderPainted(false);
                new_set.setOpaque(true);
                new_set.setFont(new Font("Arial", Font.BOLD, 6));
                new_set.setText("Set " + Integer.toString(num_sets.getCount()));
                new_set.setSet(num_sets.getCount());
                parameter_set_buttons.put(num_sets.getCount(), new_set);
                Parameter_Set button_set= new Parameter_Set(num_sets.getCount(), background);
                Parameter_Sets.put(num_sets.getCount(), button_set);
                new_set.setVisible(true);
                new_set.setFocusPainted(false);
                main_frame.add(new_set);
                main_frame.repaint();

                Active_Set.set_active_set(num_sets.getCount());
                update_click_color();

                if (num_sets.getCount() % 8 == 0) {

                    Enumeration<Integer> g= Parameter_Sets.keys();
                    while (g.hasMoreElements()) {
                        int key= g.nextElement();
                        int old_x= parameter_set_buttons.get(key).getX();
                        int old_y= parameter_set_buttons.get(key).getY();
                        parameter_set_buttons.get(key).setBounds(old_x, old_y - 30, 50, 20);
                        main_frame.add(parameter_set_buttons.get(key));
                        main_frame.repaint();

                    }
                }

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
        });

        save_config.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Active_Set.save_movements();
                int returnVal= write_fc.showSaveDialog(main_frame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File state_file= write_fc.getSelectedFile();
                    Read_Write.write_file(state_file);

                }

            }
        });

        add_row.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row= movement_table.getSelectedRow();
                if (row < 0) {
                    int rows= default_table.getRowCount();
                    default_table.insertRow(rows, new Object[] { null, null });
                } else {
                    default_table.insertRow(row + 1, new Object[] { null, null });
                }
            }

        });

        delete_row.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row= movement_table.getSelectedRow();
                if (row < 0) {
                    int rows= default_table.getRowCount();
                    default_table.removeRow(rows - 1);
                } else {
                    default_table.removeRow(row);
                }

            }

        });

        edit_mov.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movement_table.setEnabled(true);

            }

        });

        save_mov.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // movement_table.setDefaultEditor(Object.class, null);
                movement_table.setEnabled(false);

            }

        });

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Active_Set.save_movements();
                // two things need to be done
                // (1) create a matrix of colors that represents the movement matrix
                // --> update button colors accordingly during experiment
                // (2) create a matrix of commands and corresponding times
                // for each set
                // (3) set active set to -1 so that the most recent movement matrix updates
                Command_center.create_command_schedule();
                Command_center.send_commands();

            }

        });

        main_frame.setVisible(true);
    }

    // given a panel number will return the desired button coordinates (x,y of the top left corner)
    public static float[] button_location(int i, int gap, int num_panels, int button_size,
        int size_x, int size_y) {
        int x_coord= i % 8;
        int y_coord= i / 8;
        int quad= (int) Math.sqrt(num_panels);
        float x_pos= (float) (size_x / 2 - button_size * (quad / 2 - x_coord) -
            (quad / 2 - x_coord - 0.5) * gap);
        float y_pos= (float) (size_y / 2 - button_size * (quad / 2 - y_coord) -
            (quad / 2 - y_coord - 0.5) * gap);
        float[] pos= new float[2];
        pos[0]= x_pos - size_x / 4;
        pos[1]= y_pos;
        return pos;

    }

    public static void update_click_color() {
        int active_set= Active_Set.get_active_set();
        UIManager.put("Button.select", button_colors.get(active_set));
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path,
        String description) {
        java.net.URL imgURL= getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

}
