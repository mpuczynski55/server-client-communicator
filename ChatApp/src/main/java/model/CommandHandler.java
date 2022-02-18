package model;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;

public class CommandHandler extends Thread {
    private final Server server;
    private final Thread cmd;
    private final ArrayList<String> available_commands;
    private final String UNKNOWN_COMMAND;

    public CommandHandler(Server server) {
        this.server = server;
        UNKNOWN_COMMAND = "Unknown command, type ? to show available commands";
        available_commands = new ArrayList<>();
        available_commands.add("disconnect-user [user_name]");
        available_commands.add("ban-user [user_name]");
        available_commands.add("show-active-users");
        available_commands.add("show-inactive-users");
        available_commands.add("show-banned-users");
        available_commands.add("show-user-status [user_name]");
        available_commands.add("stop-server");
        cmd = new Thread(this);
        cmd.start();
    }

    @Override
    public void run() {
        while (true) {
            Scanner cmd_scanner = new Scanner(System.in);
            String full_command = cmd_scanner.nextLine();
            server.log(Level.INFO, "Administrator typed: " + full_command);
            String[] tab = full_command.trim().split("\\s+");
            String command = tab[0];
            int flag = -1;
            String user_name = "";
            try {
                for (int i = 1; i <= tab.length; i++) {
                    user_name = user_name + tab[i];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                if (user_name.length() != 0) {
                    flag = 1;
                }
            } finally {
                boolean command_check = checkCommand(command);
                if (command.equals("?")) {
                    ListIterator<String> list_iter = available_commands.listIterator(0);
                    StringBuilder commands_list = new StringBuilder();
                    while (list_iter.hasNext()) {
                        commands_list.append(list_iter.next() + "\n");
                    }
                    server.log(Level.INFO, commands_list.toString());
                } else if (flag == -1 && command_check) {
                    processCommand(command);
                } else if (flag == 1 && command_check) {
                    processCommand(command, user_name);
                } else {
                    server.log(Level.INFO, UNKNOWN_COMMAND);
                }
            }
        }
    }

    public boolean checkCommand(String command) {
        for (String temp : available_commands) {
            if (temp.contains(command)) {
                return true;
            }
        }
        return false;
    }

    public void processCommand(String command, String user_name) {
        if (command.compareToIgnoreCase("disconnect-user") == 0) {
            int status = server.checkUserStatus(user_name);
            if (status == 1) {
                server.disconnectUser(user_name, status);
            }
        } else if (command.compareToIgnoreCase("show-user-status") == 0) {
            int temp = server.checkUserStatus(user_name);
            if (temp == 1) {
                server.log(Level.INFO, "User: " + user_name + " active");
            } else if (temp == 0) {
                server.log(Level.INFO, "User: " + user_name + " inactive");
            } else if (temp == -2) {
                server.log(Level.INFO, "User: " + user_name + " banned");
            } else {
                server.log(Level.INFO, "User does not exist");
            }
        } else if (command.compareToIgnoreCase("ban-user") == 0) {
            int temp = server.checkUserStatus(user_name);
            if (temp == 1) {
                server.addBannedUser(user_name, temp);
                server.log(Level.INFO, "Client " + user_name + " banned");
            } else if (temp == 0) {
                server.addBannedUser(user_name, temp);
                server.log(Level.INFO, "Client " + user_name + " banned");
            }
        } else {
            server.log(Level.INFO, UNKNOWN_COMMAND);
        }
    }

    public void processCommand(String command) {
        if (command.compareToIgnoreCase("show-active-users") == 0) {
            Set<String> names = server.getActiveUsersNames();
            if (names.size() == 0) {
                server.log(Level.INFO, "No active users");
            } else {
                server.log(Level.INFO, "Active users: " + names);
            }
        } else if (command.compareToIgnoreCase("show-inactive-users") == 0) {
            Set<String> names = server.getInactiveUserNames();
            if (names.size() == 0) {
                server.log(Level.INFO, "No inactive users");
            } else {
                server.log(Level.INFO, "Inactive users: " + names);
            }
        } else if (command.compareToIgnoreCase("show-banned-users") == 0) {
            Set<String> banned_users = server.getBannedUsers();
            if (banned_users.size() == 0) {
                server.log(Level.INFO, "No banned users");
            } else {
                server.log(Level.INFO, "Banned users: " + banned_users);
            }

        } else if (command.compareToIgnoreCase("stop-server") == 0) {
            server.stopServer();
        } else {
            server.log(Level.INFO, UNKNOWN_COMMAND);
        }
    }
}
