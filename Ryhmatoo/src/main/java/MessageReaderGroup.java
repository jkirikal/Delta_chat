package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MessageReaderGroup  implements Runnable{
    private final byte[] buffer = new byte[256];
    private final MulticastSocket multicastSocket;
    private String name;
    int port;
    InetAddress groupChat;
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
