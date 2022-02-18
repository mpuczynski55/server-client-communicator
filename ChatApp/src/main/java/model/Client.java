package model;

import java.net.*;
import java.io.*;

public class Client extends ClientData {

    private String message;

    public Client(String address, int port) {
        super();
        try {
            setSocket(new Socket(address, port));
            setIn(new DataInputStream(getSocket().getInputStream()));
            setOut(new DataOutputStream(getSocket().getOutputStream()));
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    public void sendMessage(String msg) {
        try {
            getOut().writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() {
        try {
            message = getIn().readUTF();
        } catch (SocketException s) {
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

}