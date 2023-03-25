import java.io.DataInputStream;


public class MessageReader implements Runnable{
    DataInputStream in;
    public MessageReader(DataInputStream in){
        this.in = in;
    }

    public void run(){
        try{
            while(true){
                //reads from inputstream
                String received = in.readUTF();
                if(received.equals("exit")){
                    //if other participant wrote "exit" to his/her output, then this user closes
                    //own input and ends this thread
                    in.close();
                    System.out.println("The other participant left, write 'exit' to leave");
                    break;
                }
                //prints to the console the received message
                System.out.println(received);
            }
        }
        catch (Exception e){
            System.out.println("You left the chat");
        }
    }
}
