package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Client;
import model.GMConnection;

import java.io.IOException;
import java.util.Objects;

public class LogInController extends Thread {

    @FXML
    private Button log_in_btn;

    @FXML
    private TextField nickField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label userNotFoundLabel;

    private final Client client;
    private final Thread read_messaege;
    private double x_offset;
    private double y_offset;

    public LogInController() {
        client = new Client("localhost", 2137);
        read_messaege = new Thread(this);
        read_messaege.start();
    }

    @FXML
    public void userLogIn() {
        boolean temp = handleLogIn();
        if (temp) {
            client.sendMessage(GMConnection.getLOG_IN());
        }
    }

    @FXML
    public void userSignUp() {
        boolean temp = handleLogIn();
        if (temp) {
            client.sendMessage(GMConnection.getSIGN_UP());
        }
    }

    public boolean handleLogIn() {
        String nick = nickField.getText();
        String pass = passwordField.getText();
        if (!nick.isEmpty() && !pass.isEmpty() && nick.length() <= 10) {
            client.sendMessage(nick);
            client.setNickname(nick);
            client.sendMessage(pass);
            client.setPassword(pass);
            return true;
        } else if (nick.length() > 10) {
            nickField.setText("Max 10 znaków");
        }
        return false;
    }

    public void setErrorLabel(String msg) {
        Platform.runLater(() -> userNotFoundLabel.setText(msg));
        userNotFoundLabel.setVisible(true);
    }

    public void endSession() {
        System.exit(0);
    }

    public void changeWindow() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage stage_close = (Stage) log_in_btn.getScene().getWindow();
                    stage_close.close();
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("ChatAppGUI.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 750, 600);
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.getIcons().add(new Image(Objects.requireNonNull(LogInController.class.getResourceAsStream("window_icon.png"))));
                    scene.setOnMousePressed(event -> {
                        x_offset = stage.getX() - event.getScreenX();
                        y_offset = stage.getY() - event.getScreenY();
                    });
                    scene.setOnMouseDragged(event -> {
                        stage.setX(event.getScreenX() + x_offset);
                        stage.setY(event.getScreenY() + y_offset);
                    });
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                    ChatGUIController controller = fxmlLoader.getController();
                    controller.initData(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run() {
        String received;
        while (true) {
            received = client.readMessage();
            if (received != null) {
                if (received.equals(GMConnection.getINCORRECT_DATA())) {
                    setErrorLabel("Złe dane");
                } else if (received.equals(GMConnection.getINCORRECT_PASSWORD())) {
                    setErrorLabel("Niepoprawne hasło");
                } else if (received.equals(GMConnection.getBANNED())) {
                    setErrorLabel("Konto zbanowane");
                } else if (received.equals(GMConnection.getUSER_EXIST())) {
                    setErrorLabel("Taki uzytkownik już istnieje");
                } else if (received.equals(GMConnection.getLOG_IN_OK())) {
                    changeWindow();
                    break;
                }
            }
        }
    }
}
