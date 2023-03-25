import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("Server running");
        int portNumber = 1337;
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            //waits for both clients to connect with the port, creates two separate sockets
            try (Socket socket1 = ss.accept()) {
                try(Socket socket2 = ss.accept()){
                    //creates input and output streams for both of the sockets
                    DataInputStream dataIn1 = new DataInputStream(socket1.getInputStream());
                    DataOutputStream dataOut1 = new DataOutputStream(socket1.getOutputStream());
                    DataInputStream dataIn2 = new DataInputStream(socket2.getInputStream());
                    DataOutputStream dataOut2 = new DataOutputStream(socket2.getOutputStream());

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
        }
    }
}