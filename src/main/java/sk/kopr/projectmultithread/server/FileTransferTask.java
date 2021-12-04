package sk.kopr.projectmultithread.server;

import sk.kopr.projectmultithread.exceptions.FileNotReceivedException;
import sk.kopr.projectmultithread.utils.Constants;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileTransferTask implements Runnable {

    private Socket clientSocket;
    private File file;
    private TCPServer server;
    private BlockingDeque<String> filesToSend;
    private Semaphore semaphore;
    private AtomicInteger error;

    public FileTransferTask(Socket clientSocket, File file, TCPServer server, BlockingDeque<String> filesToSend, Semaphore semaphore, AtomicInteger error) {
        this.clientSocket = clientSocket;
        this.file = file;
        this.server = server;
        this.filesToSend = filesToSend;
        this.semaphore = semaphore;
        this.error = error;
    }

    @Override
    public void run() {
        try {
            sendFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encode(String rawString) {
        byte[] bytes = rawString.getBytes(StandardCharsets.UTF_8);
        String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
        return utf8EncodedString;
    }

    private void sendFile() throws IOException {
        BufferedInputStream bis = null;
        DataOutputStream output;
        DataInputStream input;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            output = new DataOutputStream(clientSocket.getOutputStream());
            input = new DataInputStream(clientSocket.getInputStream());

            Long fileSize = file.length();
            String fileName = encode(file.getName());

            // SEND FILE INFO
            output.writeUTF(encode(file.getAbsolutePath().substring(Constants.START_DIR.length() + 1)));
            output.flush();
            output.writeUTF(fileName);
            output.flush();
            output.writeLong(fileSize);
            output.flush();

            // GET FILE SIZE FROM CLIENT
            Long actualFileSize = input.readLong();

            // CHECKS WHAT PART OF DATA IS NEEDED TO SEND
            if(actualFileSize.compareTo(0L) == 0) {
                System.out.println("Sending file " + file.getName() + " with size " + file.length());
            }
            if(actualFileSize.compareTo(fileSize) < 0 && actualFileSize.compareTo(0L) != 0) {
                System.out.println("Sending remaining of file " + file.getName() + " with size " + file.length());
            }
            if(actualFileSize.compareTo(fileSize) == 0) {
                System.out.println("File " + fileName + " already copied!");
            } else {
                // SEND FILE
                int size = 10000;
                Long byteCount = actualFileSize;
                byte[] bytes;
                while (byteCount != fileSize) {
                    if (fileSize - byteCount >= size)
                        byteCount += size;
                    else {
                        size = (int) (fileSize - byteCount);
                        byteCount = fileSize;
                    }
                    bytes = new byte[size];
                    bis.read(bytes, Math.toIntExact(actualFileSize), size);
                    output.write(bytes);
                    output.flush();
                }
            }
            error.set(0);
        } catch (IOException | IndexOutOfBoundsException e) {
            System.err.println("An error occurred trying to send file " + file.getAbsolutePath());
//            e.printStackTrace();
            filesToSend.offerLast(file.getPath());
            error.incrementAndGet();
        } finally {
            // RELEASE CONNECTION
            semaphore.release();
            server.returnConnection(clientSocket);
            if(bis != null) {
                bis.close();
            }
        }
    }
}
