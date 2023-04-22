//package main.java;
// 127.0.0.1 =  "localhost"


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client3 {
    public static void main(String[] args) throws Exception {
        String name = null;
        int portNumberAuth = 2337;
        int portNumberChat = 1337;
        try (Socket socket = new Socket("127.0.0.1", portNumberAuth)) {
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            AuthenticationClient authClient = new AuthenticationClient(dataIn,dataOut);
            authClient.run();
            name = authClient.getUsername();
        }
        catch (Exception e){
            System.out.println(e);
        }

        MulticastSocket multicastSocket = new MulticastSocket(portNumberChat);
        InetAddress groupChat = InetAddress.getByName("239.0.0.0");
        multicastSocket.joinGroup(groupChat);
        MessageReaderGroup readerGroup = new MessageReaderGroup(multicastSocket, name, groupChat, portNumberChat);
        Thread readMessages = new Thread(readerGroup);
        Scanner sc = new Scanner(System.in);
        readMessages.start();
        while(true)
        {
            String message;
            message = sc.nextLine();
            if(message.equalsIgnoreCase("exit"))
            {
                multicastSocket.leaveGroup(groupChat);
                multicastSocket.close();
                break;
            }
            message = name + ": " + message;
            byte[] buffer = message.getBytes();
            DatagramPacket datagram = new
                    DatagramPacket(buffer,buffer.length,groupChat,portNumberChat);
            multicastSocket.send(datagram);
        }
    }
}