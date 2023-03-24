import java.io.*;
import java.net.Socket;
public class User {

    //Trying to understand, for these Strings, what would be the most logical - access modifier, default value and place to initialize.
    private String whatUserSees = " - ";
    String receivedReply = "y";
    String msgToSend = "";


    public String getWhatUserSees() {
        return whatUserSees;
    }

    public void userProcesses() throws IOException {
        StringBuffer sb = new StringBuffer();

        try (Socket s = new Socket("localhost", 1033)) {

            DataInputStream receivedReplyDataStream = new DataInputStream(s.getInputStream());
            DataOutputStream msgSendingStream  = new DataOutputStream(s.getOutputStream());
            //For reading user input from terminal(?)
            BufferedReader messageReader = new BufferedReader(new InputStreamReader(System.in));

            msgToSend = messageReader.readLine();
            //msgSendingStream.writeUTF("test Hello server! äöõü¤%&/()");
            msgSendingStream.writeUTF(msgToSend);

            receivedReply = receivedReplyDataStream.readUTF();
            sb.append(receivedReply);
            whatUserSees = sb.toString();
        }
    }
}
