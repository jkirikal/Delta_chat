import main.java.AuthenticationServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("Server running");
        int portNumber1 = 2337;
        int portNumber2 = 1337;

        // create ServerSocket to wait for authentication done.
        // this ServerSocket is for chatting
        ServerSocket ssChat = new ServerSocket(portNumber2);

        //waits for both clients to connect with the port, creates two separate sockets
        //creates input and output streams for both of the sockets
        // this ServerSocket is for authentication
        try (ServerSocket ssAuth = new ServerSocket(portNumber1)) {
            try (Socket socketAuth1 = ssAuth.accept()) {
                try (Socket socketAuth2 = ssAuth.accept()) {
                    System.out.println("Authentication started");
                    DataInputStream dataIn1 = new DataInputStream(socketAuth1.getInputStream());
                    DataOutputStream dataOut1 = new DataOutputStream(socketAuth1.getOutputStream());
                    DataInputStream dataIn2 = new DataInputStream(socketAuth2.getInputStream());
                    DataOutputStream dataOut2 = new DataOutputStream(socketAuth2.getOutputStream());
                    AuthenticationServer authServer1 = new AuthenticationServer(dataIn1,dataOut1);
                    AuthenticationServer authServer2 = new AuthenticationServer(dataIn2,dataOut2);
                    Thread authentication1 = new Thread(authServer1);
                    Thread authentication2 = new Thread(authServer2);
                    authentication1.start();
                    authentication2.start();
                    authentication2.join();
                    authentication1.join();
                }
            }
        }
        System.out.println("Authentication done");

        //waits for both clients to connect with the port, creates two separate sockets
        //creates input and output streams for both of the sockets
        try (Socket socketChat1 = ssChat.accept()) {
            try (Socket socketChat2 = ssChat.accept()) {
                DataInputStream dataIn1 = new DataInputStream(socketChat1.getInputStream());
                DataOutputStream dataOut1 = new DataOutputStream(socketChat1.getOutputStream());
                DataInputStream dataIn2 = new DataInputStream(socketChat2.getInputStream());
                DataOutputStream dataOut2 = new DataOutputStream(socketChat2.getOutputStream());
                System.out.println("Creating chat...");
                //creates threads for reading and writing to streams
                //threads are needed for dealing separately with the clients' inputs
                ServerReaderWriter rw1 = new ServerReaderWriter(dataIn1, dataOut2);
                ServerReaderWriter rw2 = new ServerReaderWriter(dataIn2, dataOut1);
                Thread communication1 = new Thread(rw1);
                Thread communication2 = new Thread(rw2);
                communication2.start();
                communication1.start();
                //when both of the threads are created, clients can start sending messages
                System.out.println("Chat created");
                communication1.join();
                communication2.join();
                System.out.println("Server closed");
            }
        }
        ssChat.close();
    }
}