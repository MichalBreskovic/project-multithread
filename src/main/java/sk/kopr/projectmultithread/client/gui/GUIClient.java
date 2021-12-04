package sk.kopr.projectmultithread.client.gui;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import sk.kopr.projectmultithread.client.ClientTaskHandler;
import sk.kopr.projectmultithread.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUIClient {

    DoubleProperty progress = new SimpleDoubleProperty(100);
    ExecutorService executorService;
    ClientTaskHandler taskHandler;


    @FXML
    private Label connectedLabel;

    @FXML
    private Circle connectedLight;

    @FXML
    private Label fileProgressLabel;

    @FXML
    private ProgressBar filesProgressBar;

    @FXML
    private ProgressBar sizeProgressBar = new ProgressBar();

    @FXML
    private Label sizeProgressLabel;

    @FXML
    private ChoiceBox<?> socketCount;

    @FXML
    private Button startCopyBtn;

    public GUIClient() {
        sizeProgressBar.progressProperty().bind(progress.asObject());
        executorService = Executors.newSingleThreadExecutor();
        taskHandler = new ClientTaskHandler(Constants.THREAD_COUNT, progress);
        taskHandler.connect();
        progress.addListener((observable, oldValue, newValue) -> sizeProgressBar.setProgress(newValue.doubleValue()));
    }

    @FXML
    void onStartCopyBtnClick(MouseEvent event) {
        executorService.submit(taskHandler);
    }

}
