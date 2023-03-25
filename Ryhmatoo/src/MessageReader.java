import java.io.DataInputStream;


public class MessageReader implements Runnable{
    DataInputStream in;
    public MessageReader(DataInputStream in){
        this.in = in;
    }

    public void run(){
        try{
            while(true){
                String received = in.readUTF();
                if(received.equals("exit")){
                    in.close();
                    System.out.println("The other participant left, write 'exit' to leave");
                    break;
                }
                System.out.println(received);
            }
        }
        catch (Exception e){
            System.out.println("You left the chat");
        }
    }
}
