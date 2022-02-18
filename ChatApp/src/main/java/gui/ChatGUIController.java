package gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Client;
import model.GMConnection;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChatGUIController extends Thread {

    @FXML
    private PasswordField change_password_pf;

    @FXML
    private TextField change_username_tf;

    @FXML
    private Text current_user;

    @FXML
    private TextField input_message;

    @FXML
    private Text registration_date;

    @FXML
    private ScrollPane messages_scrollpane;

    @FXML
    private Text recipient_tf;

    @FXML
    private Pane active_user_icon;

    @FXML
    private Pane settings_panel;

    @FXML
    private VBox active_users_vbox;

    @FXML
    private ScrollPane active_users_sp;

    private Client client;
    private Thread thread;
    private String current_receiver;
    private String new_name;
    private HashMap<String, VBox> messages_VBoxes;
    private HashMap<String, Pane> active_users_Panes;
    private HashMap<String, HBox> active_users_HBoxes;

    public void initData(Client client) {
        this.client = client;
        messages_VBoxes = new HashMap<>();
        active_users_Panes = new HashMap<>();
        active_users_HBoxes = new HashMap<>();
        messages_scrollpane.setStyle("-fx-background-color: #4A4A4A;");
        newChatFrame(client.getNickname());
        current_user.setText(client.getNickname());
        thread = new Thread(this);
        thread.start();
    }

    public void newChatFrame(String name) {
        VBox temp = new VBox();
        temp.setPrefHeight(498);
        temp.setPrefWidth(520);
        temp.setVisible(false);
        temp.setStyle("-fx-background-color: #4A4A4A;");
        temp.heightProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) {
                messages_scrollpane.setVvalue((Double) newValue);
            }
        });
        messages_VBoxes.put(name, temp);
    }

    public void newUserHBox(String name) {
        Pane pane = new Pane();
        pane.setPrefHeight(55);
        pane.setPrefWidth(210);

        Text last_msg = new Text("");
        Text date = new Text("");
        Text sender = new Text(name);
        last_msg.setFill(Color.WHITE);
        sender.setFill(Color.WHITE);
        date.setFill(Color.WHITE);

        last_msg.setFont(new Font("Marlett", 13));
        sender.setFont(new Font("Marlett", 13));
        date.setFont(new Font("Marlett", 13));
        last_msg.maxWidth(10);
        last_msg.setLayoutX(12);
        last_msg.setLayoutY(46);
        sender.setLayoutX(12);
        sender.setLayoutY(25);
        date.setLayoutX(173);
        date.setLayoutY(45);
        last_msg.setId("last_msg");
        sender.setId("sender");
        date.setId("date");
        pane.getChildren().add(last_msg);
        pane.getChildren().add(sender);
        pane.getChildren().add(date);

        HBox hbox = new HBox();
        hbox.setPrefHeight(65);
        hbox.setPrefWidth(209);
        hbox.setId(name);

        hbox.setStyle("-fx-border-color: #252525");
        hbox.getChildren().add(pane);
        setHBoxMouseClickedEvent(hbox, name);
        active_users_HBoxes.put(name, hbox);
        active_users_Panes.put(name, pane);
        Platform.runLater(() -> active_users_vbox.getChildren().add(hbox));
    }

    public void deleteUserHBox(String user) {
        Platform.runLater(() -> {
            active_users_vbox.getChildren().remove(active_users_HBoxes.get(user));
            VBox temp = messages_VBoxes.get(client.getNickname());
            temp.setVisible(true);
            messages_scrollpane.setContent(temp);
            messages_VBoxes.remove(user);
            recipient_tf.setText("");
            active_user_icon.setVisible(false);
        });
    }

    public void changePassword() {
        client.sendMessage(GMConnection.getCHANGE_PASSWORD());
        client.sendMessage(change_password_pf.getText());
        change_password_pf.clear();
    }

    public void changeName() {
        String new_name = change_username_tf.getText();
        if (new_name.equals(client.getNickname())) {
            return;
        } else {
            client.sendMessage(GMConnection.getCHECK_NAME());
            client.sendMessage(new_name);
            String respond= client.readMessage();
            this.new_name=new_name;
        }
    }

    public void setNewName(String new_name) {
        client.sendMessage(GMConnection.getCHANGE_NAME());
        // old_name#new_name
        client.sendMessage(client.getNickname() + "#" + new_name);
        current_user.setText(new_name);
        client.setNickname(new_name);
        change_username_tf.clear();
    }


    public void endSession() {
        Platform.exit();
        System.exit(0);
    }

    public void leaveSettings() {
        settings_panel.setVisible(false);
    }

    public void sendMessage() {
        String msg_to_send = input_message.getText();
        String recipient = recipient_tf.getText();
        if (msg_to_send.equals("")) {
            return;
        }
        if(msg_to_send.contains("#")){
            msg_to_send.replaceAll("#","");
        }
        if (!recipient.isEmpty()) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_RIGHT);
            hbox.setPadding(new Insets(5, 5, 5, 10));
            Text text = new Text(msg_to_send);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #0b717e" + ";-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0.934, 0.945, 0.996));
            hbox.getChildren().add(textFlow);
            messages_VBoxes.get(recipient).getChildren().add(hbox);
            client.sendMessage(msg_to_send + "#" + recipient);
            if (msg_to_send.length() > 20) {
                msg_to_send = msg_to_send.substring(0, 20);
            }
            updateUserHBox(recipient, msg_to_send, getCurrentTime());
            input_message.clear();
        }
    }

    public String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return format.format(date);
    }

    public void updateUserHBox(String name, String msg, String date) {
        for (Node n : active_users_Panes.get(name).getChildren()) {
            if (n.getId().equals("last_msg")) {
                ((Text) n).setText(msg);
            } else if (n.getId().equals("date")) {
                ((Text) n).setText(date);
            }
        }
    }

    public void addReceivedMessageLabel(String msg) {
        StringTokenizer st = new StringTokenizer(msg, "#");
        String mess = st.nextToken();
        String recipient = st.nextToken();
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));
        Text text = new Text(mess);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #2a4858" + ";-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.934, 0.945, 0.996));
        hbox.getChildren().add(textFlow);
        if (mess.length() > 20) {
            mess = mess.substring(0, 20);
        }
        updateUserHBox(recipient, mess, getCurrentTime());
        Platform.runLater(() -> messages_VBoxes.get(recipient).getChildren().add(hbox));

        if (current_receiver == null || !current_receiver.equals(recipient)) {
            active_users_Panes.get(recipient).setEffect(new Glow(0.7));
        } else {
            active_users_Panes.get(recipient).setEffect(new Glow(0));
        }
    }

    public void refreshActiveUsers() {
        String received;
        received = client.readMessage();
        String[] tab = received.trim().split("\\s+");
        Set<String> names = new HashSet<>(Arrays.asList(tab));
        for (String i : names) {
            if (i.equals(client.getNickname())) {
                newChatFrame(client.getNickname());
            } else if (!active_users_Panes.containsKey(i)) {
                newUserHBox(i);
                newChatFrame(i);
            }
        }
        for (String temp : active_users_Panes.keySet()) {
            if (!names.contains(temp)) {
                deleteUserHBox(temp);
            }
        }
    }

    public void settingsWindow() {
        settings_panel.setVisible(true);
    }

    public void handleUserChangeName(String old_name, String new_name) {
        if (recipient_tf.getText().equals(old_name)) {
            recipient_tf.setText(new_name);
        }
        active_users_Panes.put(new_name, active_users_Panes.remove(old_name));

        messages_VBoxes.put(new_name, messages_VBoxes.remove(old_name));
        HBox temp = active_users_HBoxes.remove(old_name);
        active_users_HBoxes.put(new_name, temp);
        setHBoxMouseClickedEvent(temp, new_name);
        for (Node n : active_users_Panes.get(new_name).getChildren()) {
            if (n.getId().equals("sender")) {
                ((Text) n).setText(new_name);
            }
        }
    }

    public void setHBoxMouseClickedEvent(HBox hbox, String name) {
        hbox.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!messages_scrollpane.isVisible()) {
                    messages_scrollpane.setVisible(true);
                }
                if (!recipient_tf.isVisible()) {
                    recipient_tf.setVisible(true);
                }
                if (!active_user_icon.isVisible()) {
                    active_user_icon.setVisible(true);
                }
                messages_scrollpane.setContent(messages_VBoxes.get(name));
                messages_VBoxes.get(name).setVisible(true);
                recipient_tf.setText(name);
                current_receiver = name;
                active_users_Panes.get(name).setEffect(new Glow(0));
            }
        });
    }

    @Override
    public void run() {
        String received;
        while (true) {
            received = client.readMessage();
            if (received != null) {
                if (received.equals(GMConnection.getACTIVE_USERS())) {
                    refreshActiveUsers();
                } else if (received.equals(GMConnection.getCHANGE_NAME())) {
                    String names = client.readMessage();
                    StringTokenizer st = new StringTokenizer(names, "#");
                    String old_name = st.nextToken();
                    String new_name = st.nextToken();
                    if (new_name.equals(client.getNickname())) {
                        continue;
                    }
                    handleUserChangeName(old_name, new_name);
                } else if (received.equals(GMConnection.getREGISTER_DATE())) {
                    String date = client.readMessage();
                    registration_date.setText(date);

                } else if (received.equals(GMConnection.getCLOSE_SESSION())) {
                    System.exit(0);
                } else if (received.equals(GMConnection.getCHANGE_NAME_OK())) {
                    setNewName(new_name);
                } else if (received.equals(GMConnection.getCHANGE_NAME_FAIL())) {
                    change_username_tf.setText("Taki użytkownik istnieje");
                } else {
                    addReceivedMessageLabel(received);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
