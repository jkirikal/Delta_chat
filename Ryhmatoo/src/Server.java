import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;


public class Server {

    //Chose a random integer that is not 1-1024
    private int portNr = 1033;

    //Same problem with Strings as in class User
    private String whatServerSees;
    private String receivedMsg = "x";
    public String getWhatServerSees() {
        return whatServerSees;
    }

    public void serverProcesses() throws IOException {
        //serversocket waits for a connection
        try (ServerSocket ss = new ServerSocket(portNr)) {
            //socket attempts to accept and establish connection
            try (Socket s = ss.accept()) {

                //using DataStreams because unlike Streams they have readUTF and writeUTF
                DataInputStream receivedMsgDataStream = new DataInputStream(s.getInputStream());
                DataOutputStream replyStreamToReceivedMsg = new DataOutputStream(s.getOutputStream());

                //StringBuffer object, unlike String, is mutable
                StringBuffer sb = new StringBuffer();


                receivedMsg = receivedMsgDataStream.readUTF();

                if (receivedMsg.equals("stop")) {
                    replyStreamToReceivedMsg.writeUTF("Server received stop call");
                    return;
                }
                sb.append("User has successfully sent this message to server: '").append(receivedMsg).append("'");
                whatServerSees = sb.toString();

                replyStreamToReceivedMsg.writeUTF(
                        "Server has received your last message (msg contents: '"
                                + receivedMsg + "')");
            }
        }
    }
}
