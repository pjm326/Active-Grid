package gui;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import com.fazecast.jSerialComm.SerialPort;

public class Command_center {

    public static SerialPort port= SerialPort.getCommPort("COM3");
    public static OutputStream out= port.getOutputStream();
    public static Hashtable<Integer, Parameter_Set> Parameter_Sets= Main.Parameter_Sets;
    public static TreeMap<Integer, byte[][]> speed_map;
    public static TreeMap<Integer, byte[][]> position_map;
    public static boolean run_state= false;

    public static boolean getState() {
        return run_state;
    }

    public static void setState(boolean state) {
        run_state= state;
    }

    public static void create_command_schedule() {

        speed_map= new TreeMap<>();
        position_map= new TreeMap<>();

        if (Parameter_Sets != null) {
            Enumeration<Integer> e= Parameter_Sets.keys();
            while (e.hasMoreElements()) {
                int key_number= e.nextElement();
                Parameter_Set current_set= Parameter_Sets.get(key_number);
                int[][] movement= current_set.getMovement();
                int rows= movement.length;
                Enumeration<Integer> f= current_set.getSet().keys();

                while (f.hasMoreElements()) {
                    int panel_number= f.nextElement();
                    for (int i= 0; i < rows - 1; i++ ) {

                        int timestamp= movement[i][0];
                        byte[] speed_command= set_speed(i, movement, panel_number);
                        byte[] position_command= open_panel(i, movement, panel_number);

                        if (speed_map.get(timestamp) != null) {

                            byte[][] old_speed_arr= speed_map.get(movement[i][0]);
                            byte[][] old_position_arr= position_map.get(movement[i][0]);
                            byte[][] new_speed_arr= Arrays.copyOf(old_speed_arr,
                                old_speed_arr.length + 1);
                            byte[][] new_position_arr= Arrays.copyOf(old_position_arr,
                                old_speed_arr.length + 1);

                            new_speed_arr[new_speed_arr.length - 1]= speed_command;
                            new_position_arr[new_position_arr.length - 1]= position_command;
                            speed_map.remove(timestamp);
                            position_map.remove(timestamp);
                            speed_map.put(timestamp, new_speed_arr);
                            position_map.put(timestamp, new_position_arr);

                        } else {

                            byte[][] speed_command_array= new byte[1][speed_command.length];
                            byte[][] position_command_array= new byte[1][position_command.length];

                            speed_command_array[0]= speed_command;
                            position_command_array[0]= position_command;

                            speed_map.put(timestamp, speed_command_array);
                            position_map.put(timestamp, position_command_array);

                        }
                    }
                }
            }
        }

    }

    public static void send_commands() {

        run_state= true;

        if (!serialControl(port)) {
            System.out.println("There is a problem with the serial port");
            return;
        }

        String set_mode= "AAA055040164";
        for (int i= 0; i < 64; i++ ) {
            set_mode= set_mode + "19";
        }
        byte[] set_mode_command= hexStringToByteArray(set_mode);

        try {
            // System.out.println("sending set mode");
            out.write(set_mode_command);
            for (int i= 1; i <= 64; i++ ) {
                out.write(set_speed_direct(50, i));
                out.write(open_panel_direct(Calibrate.get_cal(i), i));
            }
            Thread.sleep(500);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("set mode didn't work");
        }

        System.out.println("EXECUTING");
        LocalTime start_time= java.time.LocalTime.now();

        for (Map.Entry<Integer, byte[][]> entry : speed_map.entrySet()) {
            // new Timer(10, updater).start();
            Integer key= entry.getKey();
            byte[][] speed_command= speed_map.get(key);
            byte[][] position_command= position_map.get(key);
            int size= speed_command.length;
            Boolean gate1= true;

            while (gate1) {

                int time_ms= getTime(start_time);

                if (time_ms > key) {

                    for (int i= 0; i < size; i++ ) {

                        if (run_state) {

                            try {

                                out.write(speed_command[i]);
                                out.write(position_command[i]);

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else {

                            gate1= false;
                            return;
                        }

                    }

                    gate1= false;

                }
            }

            // TreeMap<Integer, byte[][]> command_map= new TreeMap<>();
        }

        run_state= false;

    }

    public static boolean serialControl(SerialPort port) {

        if (port.isOpen()) {

            return true;

        } else {

            port.openPort();
            port.setBaudRate(115200);
            port.setNumDataBits(8);
            port.setNumStopBits(2);
            port.setParity(0);
            port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

            if (port.isOpen()) {
                System.out.println("Port is open :)");
                return true;
            } else {
                System.out.println("Failed to open port :(");
                return false;
            }

        }

    }

    public static void openAll() {

        serialControl(port);

        String set_mode= "AAA055040164";
        for (int i= 0; i < 64; i++ ) {
            set_mode= set_mode + "19";
        }
        byte[] set_mode_command= hexStringToByteArray(set_mode);

        try {
            // System.out.println("sending set mode");
            out.write(set_mode_command);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("set mode didn't work");
        }

        for (int i= 1; i <= 64; i++ ) {
            byte[] speed_command= set_speed_direct(50, i);
            byte[] pos_command= open_panel_direct(Calibrate.get_cal(i), i);
            try {

                out.write(speed_command);
                out.write(pos_command);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void closeAll() {
        serialControl(port);

        String set_mode= "AAA055040164";
        for (int i= 0; i < 64; i++ ) {
            set_mode= set_mode + "19";
        }
        byte[] set_mode_command= hexStringToByteArray(set_mode);

        try {
            // System.out.println("sending set mode");
            out.write(set_mode_command);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("set mode didn't work");
        }

        for (int i= 1; i <= 64; i++ ) {
            byte[] speed_command= set_speed_direct(50, i);
            byte[] pos_command= open_panel_direct(Calibrate.get_cal(i) + 745, i);
            try {

                out.write(speed_command);
                out.write(pos_command);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static byte[] set_speed(int row, int[][] movement, int channel) {

        int center= Calibrate.get_cal(channel);
        int min= center - 745;
        float start_angle= movement[row][1];

        float start_pot= (start_angle + 90) / 180 * 1490 + min;
        float end_angle= movement[row + 1][1];
        float end_pot= (end_angle + 90) / 180 * 1490 + min;

        float angle_delta= Math.abs(end_pot - start_pot);

        int time_delta= movement[row + 1][0] - movement[row][0];
        float req_speed= angle_delta * 22 / time_delta;
        if (req_speed > 255) {
            req_speed= 255;
        }

        String channel_hex= Integer.toHexString(channel);
        channel_hex= channel_hex.toUpperCase();
        if (channel_hex.length() % 2 != 0) {
            channel_hex= '0' + channel_hex;

        }
        String hex_command= Integer.toHexString((int) req_speed);
        hex_command= hex_command.toUpperCase();
        if (hex_command.length() % 2 != 0) {
            hex_command= '0' + hex_command;
        }

        byte[] command= hexStringToByteArray(
            "AAA05503" + channel_hex + "01" + hex_command);

        return command;

    }

    public static byte[] set_speed_direct(int req_speed, int channel) {

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

        return command;

    }

    public static byte[] open_panel(int row, int[][] movement, int channel) {

        int center= Calibrate.get_cal(channel);
        int min= center - 745;
        float angle= movement[row + 1][1];
        float pot= (angle + 90) / 180 * 1490 + min;
        int loc= (int) pot;
        String channel_hex= Integer.toHexString(channel);
        channel_hex= channel_hex.toUpperCase();
        if (channel_hex.length() % 2 != 0) {
            channel_hex= '0' + channel_hex;

        }
        String hex_command= Integer.toHexString(loc);
        hex_command= hex_command.toUpperCase();
        if (hex_command.length() % 2 != 0) {
            hex_command= '0' + hex_command;
        }
        // String byte_count= Integer.toString(hex_command.length());
        String high_byte= hex_command.substring(0, 2);
        String low_byte= hex_command.substring(2);

        byte[] command= hexStringToByteArray(
            "AAA05501" + channel_hex + "02" + low_byte + high_byte);

        return command;

    }

    public static byte[] open_panel_direct(float angle, int channel) {

        float temp= angle;

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

    public static int getTime(LocalTime start_time) {
        LocalTime current_time= java.time.LocalTime.now();
        int hour= start_time.getHour();
        int minute= start_time.getMinute();
        int second= start_time.getSecond();
        int nano= start_time.getNano();

        LocalTime curr= current_time.minusHours(hour).minusMinutes(minute).minusSeconds(second)
            .minusNanos(nano);
        int time_ms= (int) (curr.getHour() * 3.6 * Math.pow(10, 6) +
            curr.getMinute() * 60000 + curr.getSecond() * 1000 + curr.getNano() * Math.pow(10, -6));

        return time_ms;

    }

}
