import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
    private String filePath = "src/main/data/rooms.txt";
    public RoomChanger(DataInputStream dataIn, DataOutputStream dataOut) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }

    @Override
    public void run() {
        try{
            LinkedHashMap<String, Integer> rooms = readRooms(filePath);
            displayRooms(rooms);
            dataOut.writeUTF("READ");
            String message = dataIn.readUTF();
            while(message.equalsIgnoreCase("new")||!rooms.containsKey(message)) {
                while (message.equalsIgnoreCase("new")) {
                    createRoom(rooms);
                    rooms = readRooms(filePath);
                    displayRooms(rooms);
                    dataOut.writeUTF("READ");
                    message = dataIn.readUTF();
                }
                if(!rooms.containsKey(message)){
                    dataOut.writeUTF("Please enter the correct room.");
                    dataOut.writeUTF("READ");
                    message = dataIn.readUTF();
                }
            }
            if(rooms.containsKey(message)){
                int port = rooms.get(message);
                dataOut.writeUTF("Port:"+port);
            }

        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public void createRoom(LinkedHashMap<String, Integer> rooms) throws Exception{
       dataOut.writeUTF("Enter a name for the new chat room: ");
       dataOut.writeUTF("READ");
       String name = dataIn.readUTF();
       int newPort;

       if(rooms.size()==0) newPort = 1234;
       else{
           ArrayList<Integer> ports = new ArrayList<>();
           rooms.forEach((key, value) -> ports.add(value));
           newPort = Collections.max(ports) +1;
       }
        Files.writeString(Path.of(filePath),name+":"+newPort+"\n",
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);
       dataOut.writeUTF("Room created!");
    }

    public void displayRooms(LinkedHashMap<String, Integer> rooms) throws Exception{
        dataOut.writeUTF("Choose the room to join by entering the corresponding name OR create a new room by writing 'new'.");
        dataOut.writeUTF("Available rooms: ");
        if (rooms.size() == 0) dataOut.writeUTF("No rooms available. Write 'new' to create one!");
        else {
            AtomicInteger i = new AtomicInteger(1);
            rooms.forEach((key, value) -> {
                try {
                    dataOut.writeUTF(i + ". " + key);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                i.getAndIncrement();
            });
        }

    }

    public LinkedHashMap<String, Integer> readRooms(String fileName) throws Exception{
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
}
