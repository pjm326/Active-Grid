package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import com.fazecast.jSerialComm.SerialPort;

public class Command_center {

    public static Hashtable<Integer, Parameter_Set> Parameter_Sets= Main.Parameter_Sets;
    public static TreeMap<Integer, byte[][]> command_map= new TreeMap<>();

    public static void create_command_schedule() {

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

                        byte[] speed_command= set_speed(i, movement, panel_number);
                        byte[] move_command= open_panel(i, movement, panel_number);

                        if (command_map.get(movement[i][0]) != null) {
                            System.out.println(command_map.get(movement[i][0])[0]);
                            System.out.println(command_map.get(movement[i][0])[1]);
                            byte[][] old_command_arr= command_map.get(movement[i][0]);
                            byte[][] new_command_arr= Arrays.copyOf(old_command_arr,
                                old_command_arr.length + 2);
                            new_command_arr[new_command_arr.length - 2]= speed_command;
                            new_command_arr[new_command_arr.length - 1]= move_command;
                            command_map.put(movement[i][0], new_command_arr);

                        } else {
                            System.out.println("here in else");

                            byte[][] insert_commands= new byte[2][];
                            insert_commands[0]= speed_command;
                            insert_commands[1]= move_command;
                            command_map.put(movement[i][0], insert_commands);

                        }

                    }
                    // add movements for all these buttons

                }
            }
        }

    }

    public static void send_commands() {
        String set_mode= "AAA055040164";
        for (int i= 0; i < 64; i++ ) {
            set_mode= set_mode + "19";
        }
        byte[] set_mode_command= hexStringToByteArray(set_mode);

        SerialPort port= SerialPort.getCommPort("COM3");
        port.closePort();
        port.openPort();
        port.setBaudRate(115200);
        port.setNumDataBits(8);
        port.setNumStopBits(2);
        port.setParity(0);
        OutputStream out= port.getOutputStream();
        BufferedReader in= new BufferedReader(new InputStreamReader(port.getInputStream()));
        // InputStream in= port.getInputStream();
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        // port.addEventListener();
        // port.notifyOnDataAvailable(true);

        if (port.isOpen()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
            return;
        }

        try {
            System.out.println("sending set mode");
            out.write(set_mode_command);
            Thread.sleep(1000);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("set mode didn't work");
        }

        System.out.println("EXECUTING");
        LocalTime start_time= java.time.LocalTime.now();

        for (Map.Entry<Integer, byte[][]> entry : command_map.entrySet()) {
            // new Timer(10, updater).start();
            Integer key= entry.getKey();
            byte[][] commands= entry.getValue();
            int size= commands.length;
            Boolean gate1= true;
            Boolean gate2= true;
            System.out.println(commands.length);
            while (gate1) {
                int time_ms= getTime(start_time);

                if (time_ms > key && gate2) {

                    for (int i= 0; i < size - 1; i++ ) {
                        System.out.println(commands[i]);

                        try {

                            out.write(commands[i]);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        gate2= false;
                    }

                    gate1= false;
                }

            }

            TreeMap<Integer, byte[][]> command_map= new TreeMap<>();
        }

    }

    public static byte[] set_speed(int row, int[][] movement, int channel) {

        float start_angle= movement[row][1];
        float start_pot= (start_angle + 90) / 180 * 1200 + 900;
        float end_angle= movement[row + 1][1];
        float end_pot= (end_angle + 90) / 180 * 1200 + 900;

        float angle_delta= end_pot - start_pot;

        int time_delta= movement[row + 1][0] - movement[row][0];
        float req_speed= angle_delta * 20 / time_delta;
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
//        System.out.println(
//            "AAA05503" + channel_hex + "01" + hex_command);

        return command;

    }

    public static byte[] open_panel(int row, int[][] movement, int channel) {

        float angle= movement[row + 1][1];
        float pot= (angle + 90) / 180 * 1270 + 860;
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
