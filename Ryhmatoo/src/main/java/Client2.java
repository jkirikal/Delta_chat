package main.java;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Scanner;

public class Client2 {
    public static void main(String[] args) throws Exception {
        final String dbURL = "jdbc:postgresql://localhost/deltachat";
        final String user = "messages";
        final String pass = "deltachatmessages";
        String name = null;
        int portNumberAuth = 2337;
        int portNumberRooms = 2338;
        int chatPort = 0;
        InetAddress groupChat = InetAddress.getByName("239.0.0.0");

        //user authentication ------------------------------------------------
        try (Socket socket = new Socket("127.0.0.1", portNumberAuth)) {
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            name = authentication(dataIn, dataOut);
        }
        catch (Exception e){
            System.out.println(e);
        }
        //------------------------------------------------------------------

        //Choosing a chatroom ----------------------------------------------
        try {

            Connection conn = DriverManager.getConnection(dbURL, user, pass);

            String roomName = null;
            try (Socket socket = new Socket("127.0.0.1", portNumberRooms)) {
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                String[] room = chooseRoom(dataIn, dataOut, name, socket);
                roomName = room[0];
                chatPort = Integer.parseInt(room[1]);
            } catch (Exception e) {
                System.out.println(e);
            }
            //------------------------------------------------------------------

            //Creates the connection to a chatroom
            MulticastSocket multicastSocket = createConnection(chatPort, name, groupChat);
            boolean exists = tableExists(conn,roomName);
            if (!exists) {
                PreparedStatement ps = conn.prepareStatement("create table " + roomName +
                        " (name varchar(100), message varchar(1000));");
                ps.executeQuery();
            }

            //Takes user input from console
            Scanner sc = new Scanner(System.in);
            String message;
            while (true) {
                message = sc.nextLine();

                //if user wants to exit the program -------------------------------
                if (message.equalsIgnoreCase("exit")) {
                    leaveGroup(multicastSocket, groupChat);
                    try (Socket socket = new Socket("127.0.0.1", portNumberRooms)) {
                        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                        exitRoom(dataOut, name);
                    }
                    break;
                }
                //------------------------------------------------------------------

                //if user wants to change the chatroom -----------------------------
                else if (message.equalsIgnoreCase("change")) {
                    leaveGroup(multicastSocket, groupChat);
                    try (Socket socket = new Socket("127.0.0.1", portNumberRooms)) {
                        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                        DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                        String[] room = chooseRoom(dataIn, dataOut, name, socket);
                        roomName = room[0];
                        chatPort = Integer.parseInt(room[1]);
                        multicastSocket = createConnection(chatPort, name, groupChat);
                        continue;
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                //-------------------------------------------------------------------

                //Sends the message -------------------------------------------------
                String messageToSend = name + ": " + message;
                byte[] buffer = messageToSend.getBytes();
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, groupChat, chatPort);
                multicastSocket.send(datagram);
                //--------------------------------------------------------------------

                // Writes the message into the table in database --------------------------------
                PreparedStatement ps = conn.prepareStatement("insert into " + roomName +
                        " (name, message) values (?, ?)");
                ps.setString(1,name);
                ps.setString(2,message);
                ps.executeUpdate();
                // -------------------------------------------------------------------
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean tableExists(Connection conn, String roomName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT EXISTS " +
                "(SELECT FROM information_schema.tables WHERE table_name = ?);");
        ps.setString(1,roomName);
        ResultSet rs = ps.executeQuery();
        rs.next();
        boolean exists = rs.getBoolean(1);
        return exists;
    }
    private static String authentication(DataInputStream dataIn, DataOutputStream dataOut){
        String username = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String income = dataIn.readUTF();
            System.out.println(income);
            while (!income.equals("Welcome to DeltaChat!")) {
                String outcome = reader.readLine();
                if (income.contains("username")) {
                    username = outcome;
                }
                dataOut.writeUTF(outcome);
                income = dataIn.readUTF();
                System.out.println(income);
            }
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return username;
    }


    private static String[] chooseRoom(DataInputStream dataIn, DataOutputStream dataOut,String name, Socket socket)
            throws Exception {
        dataOut.writeUTF(name);
        String roomName = "";
        String port = "";
        String input = null;
        Scanner sc = new Scanner(System.in);
        String income = dataIn.readUTF();
        while (!income.contains("Port:")) {
            while (!income.contains("READ")) {
                System.out.println(income);
                income = dataIn.readUTF();
            }
            input = sc.nextLine();
            dataOut.writeUTF(input);
            income = dataIn.readUTF();
        }
        port = income.split(":")[1];
        if (Integer.parseInt(port) == -1) {
            System.out.println("You closed the program.");;
            socket.close();
            System.exit(0);
        }
        roomName = input;
        System.out.println("Welcome to room: " + roomName);
        System.out.println("To leave the chat, write 'exit', to change the room, write 'change'.");
        dataOut.close();
        dataIn.close();
        return new String[]{roomName,port};
    }

    private static void exitRoom(DataOutputStream dataOut, String name) throws Exception{
        dataOut.writeUTF(name);
        dataOut.close();
    }

    private static MulticastSocket createConnection(int chatPort, String name, InetAddress groupChat)
            throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(chatPort);
        multicastSocket.joinGroup(groupChat);
        MessageReaderGroup readerGroup = new MessageReaderGroup(multicastSocket, name, groupChat, chatPort);
        Thread readMessages = new Thread(readerGroup);
        readMessages.start();
        return multicastSocket;
    }

    private static void leaveGroup(MulticastSocket multicastSocket, InetAddress groupChat) throws IOException {
        multicastSocket.leaveGroup(groupChat);
        multicastSocket.close();
    }
}