package main.java;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client3 {
    public static void main(String[] args) throws Exception {
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
        try (Socket socket = new Socket("127.0.0.1", portNumberRooms)) {
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            chatPort = chooseRoom(dataIn, dataOut, name, socket);

        }
        catch (Exception e){
            System.out.println(e);
        }
        //------------------------------------------------------------------


        //Creates the connection to a chatroom
        MulticastSocket multicastSocket = createConnection(chatPort, name, groupChat);

        //Takes user input from console
        Scanner sc = new Scanner(System.in);
        String message;
        while(true)
        {
            message = sc.nextLine();

            //if user wants to exit the program -------------------------------
            if(message.equalsIgnoreCase("exit"))
            {
                leaveGroup(multicastSocket, groupChat);
                try(Socket socket = new Socket("127.0.0.1", portNumberRooms)){
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    exitRoom(dataOut, name);
                }
                break;
            }
            //------------------------------------------------------------------

            //if user wants to change the chatroom -----------------------------
            else if(message.equalsIgnoreCase("change")){
                leaveGroup(multicastSocket, groupChat);
                try (Socket socket = new Socket("127.0.0.1", portNumberRooms)) {
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                    chatPort = chooseRoom(dataIn, dataOut,name, socket);
                    multicastSocket = createConnection(chatPort, name, groupChat);
                    continue;
                }
                catch (Exception e){
                    System.out.println(e);
                }
            }
            //-------------------------------------------------------------------

            //Sends the message -------------------------------------------------
            message = name + ": " + message;
            byte[] buffer = message.getBytes();
            DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,groupChat,chatPort);
            multicastSocket.send(datagram);
            //--------------------------------------------------------------------
        }
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

    private static int chooseRoom(DataInputStream dataIn, DataOutputStream dataOut,String name, Socket socket) throws Exception{
        dataOut.writeUTF(name);
        int port = 0;
        String input = null;
        Scanner sc = new Scanner(System.in);
        String income = dataIn.readUTF();
        while(!income.contains("Port:")){
            while(!income.contains("READ")){
                System.out.println(income);
                income = dataIn.readUTF();
            }
            input = sc.nextLine();
            dataOut.writeUTF(input);
            income = dataIn.readUTF();
        }
        port = Integer.parseInt(income.split(":")[1]);
        if(port==-1){
            System.out.println("You closed the program.");;
            socket.close();
            System.exit(0);
        }
        System.out.println("Welcome to room: "+input);
        System.out.println("To leave the chat, write 'exit', to change the room, write 'change'.");
        dataOut.close();
        dataIn.close();
        return port;
    }

    private static void exitRoom(DataOutputStream dataOut, String name) throws Exception{
        dataOut.writeUTF(name);
        dataOut.close();
    }

    private static MulticastSocket createConnection(int chatPort, String name, InetAddress groupChat) throws IOException {
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