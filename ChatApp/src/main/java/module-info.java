module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens gui to javafx.fxml;
    exports gui;
}