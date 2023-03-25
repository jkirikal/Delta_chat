import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class MessageWriter implements Runnable{
    DataOutputStream out;
    public MessageWriter(DataOutputStream out){
        this.out = out;
    }

    public void run(){
        String msg = "";
        BufferedReader messageReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            //if user writes "exit" to console, the thread stops running
            while(!msg.equals("exit")){
                //reads an entry from command line and writes to output stream
                msg = messageReader.readLine();
                out.writeUTF(msg);
            }
        }
        catch (Exception e){
            System.out.println("Channel closed");
        }
    }
}
