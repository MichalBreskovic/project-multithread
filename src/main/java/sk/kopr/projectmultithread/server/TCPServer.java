package sk.kopr.projectmultithread.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {

    ServerSocket serverSocket;

    public TCPServer() {
        try {
            serverSocket = new ServerSocket(6868, 1 , InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws Exception {
        String data = null;
        Socket client = this.serverSocket.accept();
        String clientAddress = client.getInetAddress().getHostAddress();
        System.out.println("\r\nNew connection from " + clientAddress);

        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        while ( (data = in.readLine()) != null ) {
            System.out.println("\r\nMessage from " + clientAddress + ": " + data);
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("\r\nRunning Server: " + "Host=" + InetAddress.getLocalHost() + " Port=" + 6868);
            listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
