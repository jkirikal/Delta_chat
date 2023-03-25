import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ServerReaderWriter implements Runnable{
    DataInputStream dataIn;
    DataOutputStream dataOut;
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
