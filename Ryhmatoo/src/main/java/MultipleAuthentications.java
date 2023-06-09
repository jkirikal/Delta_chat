package main.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//creates a thread for every connection to a socket
//The threads deal with authentication-related operations
public class MultipleAuthentications implements Runnable{
    private int authPort;
    public MultipleAuthentications(int authPort) {
        this.authPort = authPort;
    }

    @Override
    public void run() {
        try (ServerSocket ssAuth = new ServerSocket(authPort)) {
            int i = 1;
            while (true) {
                Socket socketAuth = ssAuth.accept();
                System.out.println("Authentication started for user nr. " + i);
                DataInputStream dataIn = new DataInputStream(socketAuth.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socketAuth.getOutputStream());
                AuthenticationServer authServer = new AuthenticationServer(dataIn, dataOut);
                Thread authentication = new Thread(authServer);
                authentication.start();
                System.out.println("Authentication ended for user nr. " + i);
                i++;
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
