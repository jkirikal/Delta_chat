import java.io.DataInputStream;
import java.io.DataOutputStream;

//writes input of Client1 to the output of Client2 and vice versa
public class ServerReaderWriter implements Runnable{
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    public ServerReaderWriter(DataInputStream dataIn, DataOutputStream dataOut){
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }

    public void run(){
        try{
            while (true){
                String sonum = dataIn.readUTF();
                dataOut.writeUTF(sonum);
            }
        }
        catch (Exception e){
            System.out.println("Channels closed");
        }
    }
}
