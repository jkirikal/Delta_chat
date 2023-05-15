package main.java;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomCounter {
    private HashMap<Integer, ArrayList<String>> roomsAndChatters;

    public RoomCounter() {
        this.roomsAndChatters = new HashMap<>();
    }

    public HashMap<Integer, ArrayList<String>> getRoomsAndChatters() {
        return roomsAndChatters;
    }

    public void addParticipant(int room, String name){
        roomsAndChatters.get(room).add(name);
    }

    public void removeParticipant(int room, String name){
        roomsAndChatters.get(room).remove(name);
    }
}
