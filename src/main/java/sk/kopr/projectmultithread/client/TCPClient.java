package sk.kopr.projectmultithread.client;

import sk.kopr.projectmultithread.utils.Constants;
import sk.kopr.projectmultithread.utils.DirInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TCPClient implements Runnable {

    private int numberOfConnections;
    private ConcurrentMap<Socket, Boolean> connections = new ConcurrentHashMap<>();
    public Socket comConnection = null;

    public TCPClient(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
    }

    @Override
    public void run() {
        createComConnection();
    }

    public Socket createComConnection() {
        while (true) {
            try {
                System.out.println("\r\nConnecting to " + InetAddress.getLocalHost() + ":" + 6868);
                comConnection = new Socket(InetAddress.getLocalHost(), 6868);
                System.out.println("Connected to " + comConnection.getInetAddress() + ":" + comConnection.getPort() + " !");
                break;
            } catch (UnknownHostException e) {
                System.err.println("Unknown host!");
//                e.printStackTrace();
            } catch (ConnectException e) {
                System.err.println("Could not connect to server. Check if server is running!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return comConnection;
    }

    public void createConnections() {
        try {
            System.out.println("\r\nRunning client: " + "Host=" + InetAddress.getLocalHost() + " Port=" + 6868);
            for(int i = 0; i < numberOfConnections; i++) {
                connections.put(new Socket(InetAddress.getLocalHost(), 6868), false);
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

    public void returnConnection(Socket oldConnection) {
        for(Map.Entry<Socket, Boolean> connection : connections.entrySet()) {
            if(connection.getKey().equals(oldConnection)) connections.replace(oldConnection, false);
        }
    }

    public void sendFilePathToServer() {
        try {
            if(comConnection != null) {
                DataOutputStream output = new DataOutputStream(comConnection.getOutputStream());
                output.writeUTF(Constants.START_DIR);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DirInfo getDirInfoFromServer() {
        DirInfo dirInfo = null;
        try {
            if(comConnection != null) {
                DataInputStream input = new DataInputStream(comConnection.getInputStream());

                System.out.println("Waiting for dir info from server");
                int fileCount = input.readInt();
                long totalDirSize = input.readLong();
                dirInfo = new DirInfo(fileCount, totalDirSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return dirInfo;
        }
    }

    public void clearConnections() {
        try {
            for(Map.Entry<Socket, Boolean> connection : connections.entrySet()) {
                connection.getKey().shutdownInput();
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
