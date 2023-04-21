//package main.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MessageWriterGroup implements Runnable{
    private final String name;
    private final int portNumber;
    private final InetAddress groupChat;
    private final MulticastSocket multicastSocket;

    public MessageWriterGroup(String name, int portNumber, InetAddress groupChat, MulticastSocket multicastSocket) {
        this.name = name;
        this.portNumber = portNumber;
        this.groupChat = groupChat;
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        BufferedReader messageReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("You are now in a chat. Write 'exit' if u want to leave.");
        try {
            String msg = messageReader.readLine();
            while (true) {
                if (msg.equalsIgnoreCase("EXIT")) {
                    messageReader.close();
                    System.out.println("Bye-bye!");
                    break;
                }
                String message = "["+this.name+"] " + msg;
                byte[] buffer = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length,groupChat,portNumber);
                multicastSocket.send(sendPacket);
                msg = messageReader.readLine();
            }
            messageReader.close();
        } catch (Exception e) {
            System.out.println("Channel closed");
        }
    }
}
