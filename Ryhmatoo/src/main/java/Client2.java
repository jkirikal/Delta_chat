import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client2 {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 1337)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            MessageReader reader = new MessageReader(in);
            Thread readMessages = new Thread(reader);
            readMessages.start();
            MessageWriter writer = new MessageWriter(out);
            Thread writeMessages = new Thread(writer);
            writeMessages.start();
            writeMessages.join();
            out.close();
            readMessages.join();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
