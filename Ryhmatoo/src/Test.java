import java.io.IOException;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        Server server = new Server();
        User user = new User();


        Thread thread1 = new Thread(() -> {

            try {
                server.serverProcesses();
                System.out.println("[Server's view]: " + server.getWhatServerSees());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = new Thread(() -> {

            try {
                user.userProcesses();
                System.out.println("[User's view]: " + user.getWhatUserSees());

            } catch (IOException e) {
                throw new RuntimeException(e); }
        });
        int x = 0;

        /* Trying to figure out how to implement threads. At this moment (in class Main) one can only send a single message.
        while (x < 5) {
            System.out.print("[Pretend You are the User.] Try typing a message here: ");
            thread1.start();
            thread2.wait();
            thread1.notify();
            thread2.start();
            thread2.notify();
            thread2.wait();
            x += 1;
        }
        */

    }
}

