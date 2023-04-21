//package main.java;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Scanner;

public class AuthenticationServer implements Runnable{
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private final String filePath = "src/main/data/data.txt";


    public AuthenticationServer(DataInputStream dataIn, DataOutputStream dataOut) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }

    private boolean auth(String username, String password) {
        File file = new File(filePath);
        HashMap<String,String> data = new HashMap<>();
        try (Scanner scanner = new Scanner(file,"UTF-8")) {
            while (scanner.hasNextLine()) {
                String[] credentials = scanner.nextLine().trim().split(":");
                data.put(credentials[0],credentials[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (data.containsKey(username) && data.get(username).equals(password)) {
            return true;
        }
        else {
            return false;
        }
    }
    private void setUser() {
        try {
            dataOut.writeUTF("Your new awesome username: ");
            String username = dataIn.readUTF();
            dataOut.writeUTF("Amazing! Now it's time for extrasecure password: ");
            String password = dataIn.readUTF();
            while (password.contains(" ")) {
                dataOut.writeUTF("Password shouldn't contain the space. Suggest new password: ");
                password = dataIn.readUTF();
            }
            Files.writeString(Path.of(filePath),username+":"+password+"\n",
                    StandardCharsets.UTF_8,StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String[] authCredentials() {
        try {
            dataOut.writeUTF("Enter your username: ");
            String username = dataIn.readUTF();
            dataOut.writeUTF("Enter your password: ");
            String password = dataIn.readUTF();
            String[] authCredentials = {username,password};
            return authCredentials;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            dataOut.writeUTF("Do you have own user in DeltaChat Universe? (YES/NO): ");
            String answer = dataIn.readUTF();
            while (!answer.equalsIgnoreCase("YES") && !answer.equalsIgnoreCase("NO")) {
                dataOut.writeUTF("Try again. Do you have own user in DeltaChat Universe? (YES/NO): ");
                answer = dataIn.readUTF();
            }
            if (answer.equalsIgnoreCase("NO")) {
                setUser();
            }
            String[] authCredentials = authCredentials();
            boolean authStatus = auth(authCredentials[0], authCredentials[1]);
            int i = 0;
            while (!authStatus) {
                if (i>=3) {
                    dataOut.writeUTF("Maybe you should create new user? (YES/N0): ");
                    if (dataIn.readUTF().trim().equalsIgnoreCase("YES")) {
                        setUser();
                    }
                }
                authCredentials = authCredentials();
                authStatus = auth(authCredentials[0], authCredentials[1]);
                i++;
            }
            dataOut.writeUTF("Welcome to DeltaChat!");
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
