package sk.kopr.projectmultithread.client;

import javafx.beans.property.DoubleProperty;
import sk.kopr.projectmultithread.utils.DirInfo;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTaskHandler implements Runnable {

    private ExecutorService executorService;
    private BlockingDeque<String> filesToSend = new LinkedBlockingDeque<>();
    private TCPClient client;
    private DoubleProperty progress;
    private AtomicLong actualTotalSize = new AtomicLong(0L);
    private int numberOfConnections;


    @Override
    public void run() {
        try {
            start();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ClientTaskHandler(int threadCount, DoubleProperty progress) {
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.numberOfConnections = threadCount;
        this.client = new TCPClient(numberOfConnections);
        this.progress = progress;
    }

    public void connect() {
        executorService.submit(client);
    }

    public void start() throws ExecutionException, InterruptedException {
        while (true) {
            System.out.println("tu som");
            client.sendFilePathToServer();
            DirInfo dirInfo = client.getDirInfoFromServer();
            client.createConnections();

            List<Future<?>> futures = new ArrayList<>();
            Socket connection = null;
            AtomicInteger error = new AtomicInteger(0);
            Semaphore semaphore = new Semaphore(numberOfConnections);
            while (dirInfo.getTotalSize().compareTo(actualTotalSize.get()) != 0 && error.get() <= numberOfConnections) {
                connection = client.getConnection();
                if (connection != null) {
                    semaphore.acquire();
                    futures.add(executorService.submit(new FileReceiveTask(connection, client, progress, dirInfo, actualTotalSize, semaphore, error)));
                }
            }
            for(Future<?> future : futures) {
                future.get();
            }
            client.clearConnections();
            if(error.get() < numberOfConnections) {
                System.out.println("All files was copied!");
                break;
            } else {
                System.err.println("Connection with server was lost!");
                client.createComConnection();
            }
        }
        System.out.println("end");
    }

}
