package main.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomChanger implements Runnable{
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private RoomCounter counter;
    private String filePath = "Ryhmatoo/src/main/data/rooms.txt";
    public RoomChanger(DataInputStream dataIn, DataOutputStream dataOut, RoomCounter counter) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
        this.counter = counter;
    }

    @Override
    public void run() {
        try{
            //Gets the username of a connected user and removes the person from all rooms
            String name = dataIn.readUTF();
            counter.getRoomsAndChatters().forEach((key, value)-> counter.getRoomsAndChatters().get(key).remove(name));

            dataOut.writeUTF("Choose a room to join by entering the corresponding name.");
            dataOut.writeUTF("To see available commands, write 'help'.");

            LinkedHashMap<String, Integer> rooms = readRooms(filePath);
            displayRooms(rooms);
            String message = newMessage(dataIn, dataOut);
            while (true) {
                if (message.equalsIgnoreCase("help")) {
                    writeHelp(dataOut);
                    message = newMessage(dataIn, dataOut);
                } else if (message.equalsIgnoreCase("new")) {
                    createRoom(rooms);
                    rooms = readRooms(filePath);
                    displayRooms(rooms);
                    message = newMessage(dataIn, dataOut);
                } else if (rooms.containsKey(message)) {
                    int port = rooms.get(message);
                    dataOut.writeUTF("Port:" + port);
                    counter.getRoomsAndChatters().get(port).add(name);
                    break;
                } else if (message.contains("show")) {
                    showUsers(message, rooms, dataOut);
                    message = newMessage(dataIn, dataOut);
                } else if (message.equalsIgnoreCase("exit")) {
                    int port = -1;
                    System.out.println("A user has left");
                    dataOut.writeUTF("Port:" + port);
                    break;
                } else if (message.equalsIgnoreCase("refresh")) {
                    rooms = readRooms(filePath);
                    displayRooms(rooms);
                    message = newMessage(dataIn, dataOut);
                } else {
                    dataOut.writeUTF("Please enter a correct command.");
                    message = newMessage(dataIn, dataOut);
                }
            }
        }
        catch (SocketException e){
            System.out.println("A user has left");
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    //creates a new chatroom, assigns a port to it and saves the room to the data file.
    private void createRoom(LinkedHashMap<String, Integer> rooms) throws Exception{
       dataOut.writeUTF("Enter a name for the new chat room: ");
       dataOut.writeUTF("READ");
       String name = dataIn.readUTF();
       int newPort;
       if (rooms.size() == 0) newPort = 1234;
       else {
           ArrayList<Integer> ports = new ArrayList<>();
           rooms.forEach((key, value) -> ports.add(value));
           newPort = Collections.max(ports) + 1;
       }
        Files.writeString(Path.of(filePath),name+":"+newPort+"\n",
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);
       dataOut.writeUTF("Room created!");
    }

    //Displays available rooms
    private void displayRooms(LinkedHashMap<String, Integer> rooms) throws Exception{
        dataOut.writeUTF("Available rooms: ");
        if (rooms.size() == 0) dataOut.writeUTF("No rooms available. Write 'new' to create one!");
        else {
            AtomicInteger i = new AtomicInteger(1);
            rooms.forEach((key, value) -> {
                if(!counter.getRoomsAndChatters().containsKey(value)) counter.getRoomsAndChatters().put(value,new ArrayList<>());
                try {
                    dataOut.writeUTF(i + ". " + key+" ("+counter.getRoomsAndChatters().get(value).size()+")");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                i.getAndIncrement();
            });
        }

    }

    //Reads all the room data in from a data file
    private LinkedHashMap<String, Integer> readRooms(String fileName) throws Exception{
        LinkedHashMap<String, Integer> rooms = new LinkedHashMap<>();
        File file = new File(fileName);
        try(Scanner sc = new Scanner(file, StandardCharsets.UTF_8)){
            while(sc.hasNext()){
                String[] lineApart = sc.nextLine().split(":");
                rooms.put(lineApart[0], Integer.parseInt(lineApart[1]));
            }
        }
        return rooms;
    }

    //First writes 'READ' to output, to let the user know that server is waiting for a new command.
    //Reads in a new command from the user.
    private String newMessage(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
        dataOut.writeUTF("READ");
        return dataIn.readUTF();
    }

    private void writeHelp(DataOutputStream dataOut) throws IOException {
        dataOut.writeUTF("Available commands: ");
        dataOut.writeUTF("\t'[room name]' -- joins a room");
        dataOut.writeUTF("\t'new' -- creates a new room ");
        dataOut.writeUTF("\t'refresh' -- refreshes the available room list and active user counts");
        dataOut.writeUTF("\t'show [room name]' -- shows who is in a specific room");
        dataOut.writeUTF("\t'exit' -- exit the program");
    }

    private void showUsers(String message, LinkedHashMap<String, Integer> rooms, DataOutputStream dataOut) throws IOException {
        String room = message.split(" ")[1];
        if(!rooms.containsKey(room)) {
            dataOut.writeUTF("Please enter the correct room.");
        }
        else{
            int port = rooms.get(room);
            dataOut.writeUTF("People in "+room+": ");
            for (String s : counter.getRoomsAndChatters().get(port)) {
                dataOut.writeUTF("\t"+s+",");
            }
        }
    }
}
