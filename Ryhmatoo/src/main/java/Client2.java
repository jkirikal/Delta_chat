import main.java.AuthenticationClient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

//Note - Client1 and Client2 have duplicate code for now.
//  in the future that should be avoided
public class Client2 {
    public static void main(String[] args) throws Exception {
        String name = null;
        try (Socket socket = new Socket("localhost", 2337)) {
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            AuthenticationClient authClient = new AuthenticationClient(dataIn,dataOut);
            authClient.run();
            name = authClient.getUsername();
        }
        catch (Exception e){
            System.out.println(e);
        }
        try (Socket socket = new Socket("localhost", 1337)) {//closes the socket after code has finished

            //creates input/output streams for the socket
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());

            /*separate threads for dealing with writing output and reading input
                  because if we read the input and write output on the same thread,
                  the reading of stream input would need to wait after the user to write something
                  as output*/

            MessageReader reader = new MessageReader(dataIn);
            Thread readMessages = new Thread(reader);
            readMessages.start();
            MessageWriter writer = new MessageWriter(dataOut,name);
            Thread writeMessages = new Thread(writer);
            writeMessages.start();
            writeMessages.join();
            dataOut.close();
            readMessages.join();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
