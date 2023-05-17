package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

//reads messages sent to a chatroom
public class MessageReaderGroup  implements Runnable {
    private final MulticastSocket multicastSocket;
    private String name;
    private int port;
    InetAddress groupChat;
    private final String dbURL = "jdbc:postgresql://localhost/deltachat";
    private final String user = "admin";
    private final String pass = "deltachatadmin";
    public MessageReaderGroup(MulticastSocket multicastSocket,String name, InetAddress groupChat, int port) {
        this.multicastSocket = multicastSocket;
        this.name = name;
        this.port = port;
        this.groupChat = groupChat;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length, groupChat, port);
                multicastSocket.receive(receivePacket);
                String message = new String(buffer,0,receivePacket.getLength(),"UTF-8");
                if (!message.startsWith(name)) {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            System.out.println("You left the chat");
        }
    }
}
