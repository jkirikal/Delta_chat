//package main.java;
// 127.0.0.1 =  "localhost"


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Client1 {
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
        InetAddress groupChat = InetAddress.getByName("224.0.0.10");
        multicastSocket.joinGroup(groupChat);
        Thread readMessages = new Thread(() -> {
            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(receivePacket);
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    if (!receivePacket.getAddress().equals(InetAddress.getLocalHost())) {
                        System.out.println(message);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        readMessages.start();
        MessageWriterGroup writerGroup = new MessageWriterGroup(name,portNumberChat,groupChat,multicastSocket);
        Thread writeMessages = new Thread(writerGroup);
        writeMessages.start();
        multicastSocket.leaveGroup(groupChat);
        multicastSocket.close();
    }
}