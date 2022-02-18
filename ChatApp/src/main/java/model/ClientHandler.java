package model;

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class ClientHandler extends ClientData implements Runnable {
    private final Server server;
    private Thread thread;
    private boolean flag;

    public ClientHandler(Server server, DataInputStream in, DataOutputStream out, Socket socket) {
        super(in, out, socket);
        this.server = server;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void logIn() {
        try {
            while (true) {
                String name = getIn().readUTF();
                String password = getIn().readUTF();
                String action = getIn().readUTF();
                if (server.getBannedUsers().contains(name)) {
                    //banned
                    sendMessage(GMConnection.getBANNED());
                    continue;
                }
                // log in existing user
                if (action.equals(GMConnection.getLOG_IN()) && server.checkUserStatus(name) == 0) {
                    ClientHandler temp = server.getInactiveClient(name);
                    // if data equals
                    if (temp.getNickname().equals(name) && temp.getPassword().equals(password)) {
                        server.deleteInactiveUser(name);
                        setNickname(name);
                        setPassword(password);
                        server.addUserToDB(name, this);
                        setStatus(true);
                        sendMessage(GMConnection.getLOG_IN_OK());
                        server.sendActiveUsersList();
                        flag = true;
                        break;
                    } else {
                        // incorrect password
                        sendMessage(GMConnection.getINCORRECT_PASSWORD());
                    }
                    // sign up new user
                } else if (action.equals(GMConnection.getSIGN_UP())) {
                    // user already exists
                    if (server.getActiveUsersNames().contains(name) || server.getInactiveUserNames().contains(name)) {
                        sendMessage(GMConnection.getUSER_EXIST());
                    } else {
                        setNickname(name);
                        setPassword(password);
                        server.addUserToDB(name, this);
                        setStatus(true);
                        sendMessage(GMConnection.getLOG_IN_OK());
                        server.sendActiveUsersList();
                        flag = true;
                        break;
                    }
                } else {
                    // incorrect data
                    sendMessage(GMConnection.getINCORRECT_DATA());
                }
            }
        } catch (IOException e) {
            server.log(Level.INFO, e.getMessage());
        }
    }

    public void sendRegisterDate() {
        try {
            sendMessage(GMConnection.getREGISTER_DATE());
            sendMessage(server.getInactiveUsersData().get(getNickname()).getRegister_date());
        } catch (NullPointerException n) {
            try {
                sendMessage(server.getActiveUsersData().get(getNickname()).getRegister_date());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logOff() {
        try {
            getIn().close();
            getOut().close();
            setStatus(false);
            getSocket().close();
            thread = null;
        } catch (IOException e) {
            server.log(Level.INFO, e.getMessage());
        }
    }

    @Override
    public void run() {
        logIn();
        if (flag) {
            sendRegisterDate();
            handleClient();
        }
    }

    public void sendMessage(ClientHandler receiver, String msg) throws IOException {
        receiver.getOut().writeUTF(msg + "#" + getNickname());
    }

    public void sendMessage(String msg) throws IOException {
        getOut().writeUTF(msg);
    }

    public void handleClient() {
        String received;
        try {
            while (true) {
                received = getIn().readUTF();
                if (received.equals(GMConnection.getCHANGE_NAME())) {
                    received = getIn().readUTF();
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String old_name = st.nextToken();
                    String new_name = st.nextToken();
                    setNickname(new_name);
                    ClientHandler temp = server.getActiveUsersData().remove(old_name);
                    temp.setNickname(new_name);
                    server.getActiveUsersData().put(new_name, temp);
                    server.sendMsgToUsers(GMConnection.getCHANGE_NAME(), received);
                    server.sendActiveUsersList();
                    server.log(Level.INFO, "User " + old_name + " changed name to " + new_name);
                } else if (received.equals(GMConnection.getCHANGE_PASSWORD())) {
                    received = getIn().readUTF();
                    setPassword(received);
                    server.getActiveUsersData().get(getNickname()).setPassword(received);
                } else if (received.equals(GMConnection.getCHECK_NAME())) {
                    received = getIn().readUTF();
                    if (!server.getActiveUsersData().containsKey(received) && !server.getInactiveUsersData().containsKey(received)) {
                        sendMessage(GMConnection.getCHANGE_NAME_OK());
                    } else {
                        sendMessage(GMConnection.getCHANGE_NAME_FAIL());
                    }
                } else {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String msg = st.nextToken();
                    String recipient = st.nextToken();
                    for (ClientHandler temp : server.getActiveUsersData().values()) {
                        if (temp.getNickname().equals(recipient) && temp.getStatus()) {
                            sendMessage(temp, msg);
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            int status = server.checkUserStatus(getNickname());
            if (status == 1) {
                server.deleteActiveUser(getNickname());
                server.addInactiveUser(getNickname(), this);
                server.sendActiveUsersList();
            }
        } catch (IOException e) {
            server.log(Level.INFO, e.getMessage());

        }

    }

}
