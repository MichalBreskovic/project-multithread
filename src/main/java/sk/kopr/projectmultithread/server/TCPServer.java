package sk.kopr.projectmultithread.server;

import sk.kopr.projectmultithread.utils.DirInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TCPServer implements Runnable {

    private int numberOfConnections;
    private ServerSocket serverSocket;
    private ConcurrentMap<Socket, Boolean> connections = new ConcurrentHashMap<>();
    public Socket comConnection = null;

    public TCPServer(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
        try {
            serverSocket = new ServerSocket(6868, 1, InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        listenForConnections();
    }

    public Socket listenForComConnection() {
        try {
            System.out.println("\r\nRunning communication server: " + "Host=" + InetAddress.getLocalHost() + " Port=" + 6868);
            comConnection = this.serverSocket.accept();
            System.out.println("Accepted connection from " + comConnection.getInetAddress() + ", port: " + comConnection.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return comConnection;
        }
    }

    public void listenForConnections() {
        try {
            System.out.println("\r\nRunning Server: " + "Host=" + InetAddress.getLocalHost() + " Port=" + 6868);
            for(int i = 0; i < numberOfConnections; i++) {
                Socket clientSocket = this.serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress() + ", port: " + clientSocket.getPort());
                connections.put(clientSocket, false);
            }

            System.out.println("All " + numberOfConnections + " connections established");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getConnection() {
        for(Map.Entry<Socket, Boolean> connection : connections.entrySet()) {
            if(!connection.getValue()) {
                connections.replace(connection.getKey(), true);
                return connection.getKey();
            }
        }
        return null;
    }

    public Socket getComConnection() {
        return comConnection;
    }

    public boolean isComClosed() {
        return comConnection.isClosed();
    }

    public void returnConnection(Socket oldConnection) {
        for(Map.Entry<Socket, Boolean> connection : connections.entrySet()) {
            if(connection.getKey().equals(oldConnection)) connections.replace(oldConnection, false);
        }
    }

    public String getFilePathFromClient() {
        String filePath = null;
        try {
            if(comConnection != null) {
                DataInputStream input = new DataInputStream(comConnection.getInputStream());
                System.out.println("Waiting for path from client");
                filePath = input.readUTF();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return filePath;
        }
    }

    public void sendDirInfoToClient(DirInfo info) {
        try {
            if(comConnection != null) {
                DataOutputStream output = new DataOutputStream(comConnection.getOutputStream());

                output.writeInt(info.getFileCount());
                output.writeLong(info.getTotalSize());
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearConnections() {
        try {
            for(Map.Entry<Socket, Boolean> connection : connections.entrySet()) {
                connection.getKey().close();
            }
            comConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connections.clear();
        comConnection = null;
    }
}
