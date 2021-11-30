package sk.kopr.projectmultithread.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient implements Runnable {

    private Socket socket;
    private Scanner scanner;

    private void start() {
        String input;
        while (true) {
            input = scanner.nextLine();
//            PrintWriter out = null;
            try {
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                out.println(input);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TCPClient() {
        try {
            this.socket = new Socket(InetAddress.getLocalHost(), 6868);
            this.scanner = new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("\r\nConnected to Server: " + socket.getInetAddress());
        start();
    }
}
