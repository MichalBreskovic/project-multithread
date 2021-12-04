package sk.kopr.projectmultithread.client;

import javafx.beans.property.DoubleProperty;
import sk.kopr.projectmultithread.utils.Constants;
import sk.kopr.projectmultithread.utils.DirInfo;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FileReceiveTask implements Runnable {

    private Socket clientSocket;
    private TCPClient client;
    private DoubleProperty progress;
    private DirInfo dirInfo;
    AtomicLong actualTotalSize;
    AtomicInteger error;
    Semaphore semaphore;

    public FileReceiveTask(Socket clientSocket, TCPClient client, DoubleProperty progress, DirInfo dirInfo, AtomicLong actualTotalSize, Semaphore semaphore, AtomicInteger error) {
        this.clientSocket = clientSocket;
        this.client = client;
        this.progress = progress;
        this.dirInfo = dirInfo;
        this.actualTotalSize = actualTotalSize;
        this.semaphore = semaphore;
        this.error = error;
    }

    @Override
    public void run() {
        try {
            receiveFile();
        } catch (IOException e) {
            if(e.getCause() instanceof SocketException) {
                System.err.println(e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }

    private void receiveFile() throws IOException {
        File outputFile = null;
        DataInputStream input = null;
        DataOutputStream output = null;
        BufferedOutputStream outputStream = null;
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            // READ FILE INFO
            String filePath = input.readUTF();
            String fileName = input.readUTF();
            Long totalFileSize = input.readLong();

            // CHECKS IF FILE EXISTS AND SENDS ACTUAL SIZE
            outputFile = new File(Constants.END_DIR + filePath);
            if(!outputFile.exists()) {
                Files.createDirectories(Path.of(Constants.END_DIR + filePath.substring(0, filePath.length() - fileName.length())));
                outputFile.createNewFile();
                System.out.println("Created new file " + fileName);
            }
            Long actualFileSize = outputFile.length();

            // SEND ACTUAL FILE SIZE
            output.writeLong(actualFileSize);
            output.flush();

            // CHECKS IF FILE IS ALREADY COPIED
            if(actualFileSize.equals(totalFileSize)) {
                System.out.println("File " + fileName + " already copied!" + actualFileSize);
                synchronized (this) {
                    progress.setValue((double) actualTotalSize.addAndGet(actualFileSize) / dirInfo.getTotalSize());
                }
            } else {
                // WRITE INTO FILE
                outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                int size = 10000;
                Long byteCount = actualFileSize;
                while (byteCount != totalFileSize) {
                    if (totalFileSize - byteCount >= size)
                        byteCount += size;
                    else {
                        size = (int) (totalFileSize - byteCount);
                        byteCount = totalFileSize;
                    }
                    byte[] bytes = new byte[size];
                    input.read(bytes, Math.toIntExact(actualFileSize), size);
                    outputStream.write(bytes);
                    synchronized (this) {
                        progress.setValue((double) actualTotalSize.addAndGet(size) / dirInfo.getTotalSize());
                    }
                }

                error.set(0);
                System.out.println("File " + fileName + " received!");
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            if(outputFile != null) {
                System.err.println("An error occurred in copying file " + outputFile.getName() + "!");
            } else {
                System.err.println("An error occurred!");
            }
            error.incrementAndGet();
//            e.printStackTrace();
        } finally {
            // RELEASING CONNECTION
            semaphore.release();
            client.returnConnection(clientSocket);
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
