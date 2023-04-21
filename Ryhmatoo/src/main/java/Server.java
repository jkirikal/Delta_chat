//package main.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
    private static ArrayList<Socket> sockets = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        System.out.println("Server running");
        int portNumber = 2337;

        // create ServerSocket to wait for authentication done.
        // this ServerSocket is for chatting

        //waits for both clients to connect with the port, creates two separate sockets
        //creates input and output streams for both of the sockets
        // this ServerSocket is for authentication
        try (ServerSocket ssAuth = new ServerSocket(portNumber)) {
            int i = 1;
            while (true) {
                Socket socketAuth = ssAuth.accept();
                System.out.println("Authentication started for user nr. " + i);
                DataInputStream dataIn = new DataInputStream(socketAuth.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socketAuth.getOutputStream());
                AuthenticationServer authServer = new AuthenticationServer(dataIn, dataOut);
                Thread authentication = new Thread(authServer);
                authentication.start();
                System.out.println("Authentication done for user nr. " + i);
                i++;
            }
        }
        //waits for both clients to connect with the port, creates two separate sockets
        //creates input and output streams for both of the sockets
    }
}