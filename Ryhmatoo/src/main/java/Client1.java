import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

//Note - Client1 and Client2 have duplicate code for now.
//  in the future that should be avoided
public class Client1 {
    public static void main(String[] args) {
        try (Socket socket = new Socket("192.168.0.100", 1337)) {//closes the socket after code has finished

            //creates input/output streams for the socket
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            /*separate threads for dealing with writing output and reading input
                  because if we read the input and write output on the same thread,
                  the reading of stream input would need to wait after the user to write something
                  as output*/
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
