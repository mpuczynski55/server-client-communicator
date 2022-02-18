package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    private double x_offset;
    private double y_offset;

    @Override
    public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        FXMLLoader fxmlLoader = new FXMLLoader(LogInController.class.getResource("StartWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("window_icon.png"))));
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(event -> {
            x_offset = stage.getX() - event.getScreenX();
            y_offset = stage.getY() - event.getScreenY();
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + x_offset);
            stage.setY(event.getScreenY() + y_offset);
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

