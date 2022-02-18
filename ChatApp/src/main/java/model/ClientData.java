package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientData {
    private String nickname;
    private String password;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private String register_date;
    private boolean status;

    public ClientData(DataInputStream in, DataOutputStream out, Socket socket) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date();
        register_date = format.format(date);
    }

    public ClientData() {
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
