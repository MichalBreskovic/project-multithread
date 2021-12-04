package sk.kopr.projectmultithread.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.kopr.projectmultithread.utils.Constants;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Server extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
        TaskHandler taskHandler = new TaskHandler(Constants.THREAD_COUNT);
        try {
            taskHandler.start();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
