package main.java;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("Server running");
        int authPort = 2337;
        int roomPort = 2338;

        //starts separate threads for authentications and room changes
        Thread authentications = new Thread(new MultipleAuthentications(authPort));
        Thread roomChanges = new Thread(new MultipleRooms(roomPort));
        authentications.start();
        roomChanges.start();
        authentications.join();
        roomChanges.join();
    }
}