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
            while(!msg.equals("exit")){
                msg = messageReader.readLine();
                out.writeUTF(msg);
            }
        }
        catch (Exception e){
            System.out.println("Channel closed");
        }
    }
}
