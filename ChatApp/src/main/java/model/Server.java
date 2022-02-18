package model;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server extends Thread implements Runnable {
    private final int server_port;
    private ServerSocket server;
    private final Logger logger;
    private final HashMap<String, ClientHandler> active_users_data;
    private final HashMap<String, ClientHandler> inactive_users_data;
    private final Set<String> banned_users;
    private Thread server_thread;


    public Server(int port) {
        server_port = port;
        logger = Logger.getLogger(getClass().getName());
        active_users_data = new HashMap<>();
        inactive_users_data = new HashMap<>();
        banned_users = new HashSet<>();
        startLogging();
        startServer();
    }

    public HashMap<String, ClientHandler> getActiveUsersData() {
        return active_users_data;
    }

    public HashMap<String, ClientHandler> getInactiveUsersData() {
        return inactive_users_data;
    }

    public Set<String> getInactiveUserNames() {
        return inactive_users_data.keySet();
    }

    public ClientHandler getInactiveClient(String name) {
        return getInactiveUsersData().get(name);
    }

    public void deleteInactiveUser(String name) {
        inactive_users_data.remove(name);
    }

    public Set<String> getActiveUsersNames() {
        return active_users_data.keySet();
    }

    public void addInactiveUser(String name, ClientHandler temp) {
        inactive_users_data.put(name, temp);
    }

    public void deleteActiveUser(String name) {
        active_users_data.remove(name);
        logger.info("Client " + name + " disconnected");
    }

    public void addUserToDB(String name, ClientHandler temp) {
        active_users_data.put(name, temp);
        logger.info("Client " + name + " added to active client list");
    }

    public void addBannedUser(String name, int status) {
        disconnectUser(name, status);
        banned_users.add(name);
    }

    public Set<String> getBannedUsers() {
        return banned_users;
    }

    public void sendMsgToUsers(String info, String msg) {
        for (ClientHandler temp : active_users_data.values()) {
            try {
                temp.getOut().writeUTF(info);
                temp.getOut().writeUTF(msg);
            } catch (IOException e) {
                logger.info("Error while sending msg to users");
            }
        }
    }

    public void sendMsgToUser(String name, String info) {
        ClientHandler receiver = active_users_data.get(name);
        try {
            receiver.getOut().writeUTF(info);
        } catch (IOException e) {
            logger.info("Error while sending msg to user");
        }
    }

    public void sendActiveUsersList() {
        if (!active_users_data.isEmpty()) {
            String list = "";
            for (ClientHandler obj : active_users_data.values()) {
                list = list + obj.getNickname() + " ";
            }
            sendMsgToUsers(GMConnection.getACTIVE_USERS(), list);
        }
    }

    public int checkUserStatus(String name) {
        if (banned_users.contains(name)) {
            return -2; //banned
        } else if (active_users_data.containsKey(name)) {
            return 1;// user active
        } else if (inactive_users_data.containsKey(name)) {
            return 0; // user inactive
        } else {
            return -1; // user not registered
        }
    }


    public void disconnectUser(String name, int status) {
        if (status == -2) {
            logger.info("Client " + name + " banned");
        } else if (status == 1) {
            sendMsgToUser(name, GMConnection.getCLOSE_SESSION());
            active_users_data.get(name).logOff();
            inactive_users_data.put(name, active_users_data.remove(name));
            sendActiveUsersList();
            logger.info("Client " + name + " disconnected");
        } else if (status == -0) {
            inactive_users_data.get(name).logOff();
        } else if (status == -1) {
            logger.info("Client " + name + " does not exist");
        }
    }

    public void log(Level level, String info) {
        logger.log(level, info);
    }

    public void addUser(DataInputStream in, DataOutputStream out, Socket socket) {
        ClientHandler temp = new ClientHandler(this, in, out, socket);
        Thread client_thread = new Thread(temp);
        temp.setThread(client_thread);
        logger.info("Client accepted");
        client_thread.start();
    }

    public void startServer() {
        int counter = 0;
        int max_tries = 5;
        while (counter <= max_tries) {
            if (counter == max_tries) {
                logger.severe("Unable to start server");
                System.exit(-1);
            }
            try {
                server = new ServerSocket(server_port);
                break;
            } catch (IOException e) {
                logger.severe("Failed to start server, retrying in 10sec");
                counter++;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    logger.severe("Server thread interrupted while retrying to start server socket");
                }
            }
        }
        server_thread = new Thread(this);
        server_thread.start();
        logger.info("Server started");
    }

    public void stopServer() {
        logger.info("Server stopped");
        System.exit(0);
    }

    public void startLogging() {
        try {
            FileHandler handler = new FileHandler("server.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            System.out.println("Error IO log file");
        }
    }

    @Override
    public void run() {
        Socket socket;
        while (true) {
            try {
                socket = server.accept();
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                addUser(in, out, socket);
            } catch (IOException e) {
                logger.info(String.valueOf(e));
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(2137);
        CommandHandler cmd = new CommandHandler(server);
    }
}