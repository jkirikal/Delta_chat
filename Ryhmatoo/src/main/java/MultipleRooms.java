import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultipleRooms implements Runnable{
    private final int roomPort;
    public MultipleRooms(int roomPort) {
        this.roomPort = roomPort;
    }

    @Override
    public void run(){
        try (ServerSocket ssRooms = new ServerSocket(roomPort)) {
            while (true) {
                Socket socketRoom = ssRooms.accept();
                DataInputStream dataIn = new DataInputStream(socketRoom.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socketRoom.getOutputStream());
                RoomChanger roomChange = new RoomChanger(dataIn, dataOut);
                Thread roomChanger = new Thread(roomChange);
                roomChanger.start();
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }


}
