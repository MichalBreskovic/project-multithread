package sk.kopr.projectmultithread.server.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GUIServer {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
