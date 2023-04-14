package main.java;

import java.io.*;
public class AuthenticationClient{
    private final DataInputStream dataIn;
    private final DataOutputStream dataOut;
    private String username;

    public AuthenticationClient(DataInputStream dataIn, DataOutputStream dataOut) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }

    public String getUsername() {
        return username;
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String income = dataIn.readUTF();
            System.out.println(income);
            while (!income.equals("Welcome to DeltaChat!")) {
                String outcome = reader.readLine();
                if (income.contains("username")) {
                    this.username = outcome;
                }
                dataOut.writeUTF(outcome);
                income = dataIn.readUTF();
                System.out.println(income);
            }
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
