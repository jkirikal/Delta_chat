import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Scanner;

public class MessageWriter implements Runnable{
    private final DataOutputStream dataOut;
    private final String name;
    public MessageWriter(DataOutputStream dataOut, String name){
        this.dataOut = dataOut;
        this.name=name;
    }
    public void run(){
        String msg = "";
        BufferedReader messageReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("You are now in a chat. Write 'exit' if u want to leave.");
        try {
            //if user writes "exit" to console, the thread stops running
            while (!msg.equalsIgnoreCase("EXIT")) {
                //reads an entry from command line and writes to output stream
                msg = messageReader.readLine();
                dataOut.writeUTF("["+this.name+"] "+msg);
            }
            dataOut.close();
        }
        catch (Exception e){
            System.out.println("Channel closed");
        }
    }
}
