package main.java;

import javax.xml.stream.FactoryConfigurationError;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

//reads messages sent to a chatroom
public class MessageReaderGroup  implements Runnable {
    private final MulticastSocket multicastSocket;
    private String name;
    private int port;
    private InetAddress groupChat;
    BlockingQueue<String> askFileDownload;
    private final String dbURL = "jdbc:postgresql://localhost/deltachat";
    private final String user = "admin";
    private final String pass = "deltachatadmin";

    public MessageReaderGroup(MulticastSocket multicastSocket, String name, InetAddress groupChat, int port, BlockingQueue<String> askFileDownload) {
        this.multicastSocket = multicastSocket;
        this.name = name;
        this.port = port;
        this.groupChat = groupChat;
        this.askFileDownload = askFileDownload;
    }

    @Override
    public void run() {
        boolean fileSent = false;
        try {
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length, groupChat, port);
                multicastSocket.receive(receivePacket);
                String message = new String(buffer, 0, receivePacket.getLength(), "UTF-8");
                if (fileSent) fileSent = false;
                else if (!message.startsWith(name)) {
                    if (message.contains("file")) {
                        acceptFile(message);
                    } else {
                        System.out.println(message);
                    }
                } else if (message.split(":").length == 3) fileSent = true;
            }
        } catch (IOException e) {
            System.out.println("You left the chat");
        }
    }

    //accepts or rejects a file sent by another user
    private void acceptFile (String message){
        try {
            String[] apart = message.split(":");
            String extension = apart[1].split("\\.")[1];
            String sender = apart[0];
            int nrOfBytes = Integer.parseInt(apart[2]);
            byte[] buffer = new byte[nrOfBytes];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length, groupChat, port);
            multicastSocket.receive(receivePacket);
            byte[] bytes = receivePacket.getData();

            System.out.println(sender + " sent a " + extension + " file. Download ('file:accept') or ignore ('file:ignore') the file");
            askFileDownload.add("file request");

            while (askFileDownload.contains("file request")) {
            }
            String action = askFileDownload.take();
            if (action.equals("file:accept")) {
                System.out.println("Downloading file");
                String loc = "Ryhmatoo\\downloads\\received." + extension;
                Path path = Paths.get(loc);
                Files.write(path, bytes);
            } else System.out.println("Didn't download file");


        } catch (ArrayIndexOutOfBoundsException e) {
        } catch (NumberFormatException e) {
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}