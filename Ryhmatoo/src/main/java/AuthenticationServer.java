package main.java;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class AuthenticationServer implements Runnable{
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private final String dbURL = "jdbc:postgresql://localhost/deltachat";
    private final String user = "auth";
    private final String pass = "deltachatauth";

    public AuthenticationServer(DataInputStream dataIn, DataOutputStream dataOut) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }


    private boolean auth(String username, String password) {
        HashMap<String, String> data = new HashMap<>();
        try {
            Connection conn = DriverManager.getConnection(dbURL, user, pass);
            PreparedStatement ps = conn.prepareStatement("select username, password from users;");
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    String chatUser = resultSet.getString(1);
                    String chatPassword = resultSet.getString(2);
                    data.put(chatUser, chatPassword);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        if (data.containsKey(username) && data.get(username).equals(password)) {
            return true;
        } else {
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
            Connection conn = DriverManager.getConnection(dbURL, user, pass);
            PreparedStatement ps = conn.prepareStatement("insert into users (username, password) values (?, ?);");
            ps.setString(1,username);
            ps.setString(2,password);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
