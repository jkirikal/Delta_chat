package main.java;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    public static void main(String[] args) throws Exception {
        String name = null;
        int portNumberAuth = 2337;
        int portNumberRooms = 2338;
        int chatPort = 0;
        try (Socket socket = new Socket("127.0.0.1", portNumberAuth)) {
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            name = authentication(dataIn, dataOut);
        }
        catch (Exception e){
            System.out.println(e);
        }

        try (Socket socket = new Socket("127.0.0.1", portNumberRooms)) {
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            chatPort = chooseRoom(dataIn, dataOut, name, socket);

        }
        catch (Exception e){
            System.out.println(e);
        }



        MulticastSocket multicastSocket = new MulticastSocket(chatPort);
        InetAddress groupChat = InetAddress.getByName("239.0.0.0");
        multicastSocket.joinGroup(groupChat);
        MessageReaderGroup readerGroup = new MessageReaderGroup(multicastSocket, name, groupChat, chatPort);
        Thread readMessages = new Thread(readerGroup);
        Scanner sc = new Scanner(System.in);
        readMessages.start();
        while (true)
        {
            String message;
            message = sc.nextLine();
            if(message.equalsIgnoreCase("exit"))
            {
                multicastSocket.leaveGroup(groupChat);
                multicastSocket.close();
                //to remove room counter
                try(Socket socket = new Socket("127.0.0.1", portNumberRooms)){
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    exitRoom(dataOut, name);
                }
                break;
            }
            else if(message.equalsIgnoreCase("change")){
                multicastSocket.leaveGroup(groupChat);
                multicastSocket.close();

                try (Socket socket = new Socket("127.0.0.1", portNumberRooms)) {
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                    chatPort = chooseRoom(dataIn, dataOut,name, socket);
                    multicastSocket = new MulticastSocket(chatPort);
                    groupChat = InetAddress.getByName("239.0.0.0");
                    multicastSocket.joinGroup(groupChat);
                    readerGroup = new MessageReaderGroup(multicastSocket, name, groupChat, chatPort);
                    readMessages = new Thread(readerGroup);
                    readMessages.start();
                    continue;
                }
                catch (Exception e){
                    System.out.println(e);
                }

            }
            message = name + ": " + message;
            byte[] buffer = message.getBytes();
            DatagramPacket datagram = new
                    DatagramPacket(buffer,buffer.length,groupChat,chatPort);
            multicastSocket.send(datagram);
        }
    }

    public static String authentication(DataInputStream dataIn, DataOutputStream dataOut){
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

    public static int chooseRoom(DataInputStream dataIn, DataOutputStream dataOut,String name, Socket socket) throws Exception{
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

    public static void exitRoom(DataOutputStream dataOut, String name) throws Exception{
        dataOut.writeUTF(name);
        dataOut.close();
    }
}