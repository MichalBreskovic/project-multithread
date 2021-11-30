package sk.kopr.projectmultithread.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.kopr.projectmultithread.client.TCPClient;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
        Thread server = new Thread(new TCPServer());
        server.start();
    }

    public static void main(String[] args) {
        launch();
    }
}