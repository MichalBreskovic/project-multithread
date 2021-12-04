package sk.kopr.projectmultithread.server;

import sk.kopr.projectmultithread.utils.DirInfo;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskHandler {

    private ExecutorService executorService;
    private TCPServer server;
    private DirInfo dirInfo = null;
    private int numberOfConnections;

    public TaskHandler(int threadCount) {
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.numberOfConnections = threadCount;
        this.server = new TCPServer(numberOfConnections);
    }

    public DirInfo getDirInfo(File startDir) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FileSearcher task = new FileSearcher(startDir);
        forkJoinPool.submit(task);
        DirInfo info = task.join();
        forkJoinPool.shutdown();
        return info;
    }

    public void start() throws InterruptedException, ExecutionException {
        while (true) {
            server.listenForComConnection();
            String startPath = server.getFilePathFromClient();
            if(dirInfo != null) System.out.println(startPath + " " + dirInfo.getPath() + " " + !startPath.equals(dirInfo.getPath()));
            if (dirInfo == null || !startPath.equals(dirInfo.getPath())) {
                dirInfo = getDirInfo(new File(startPath));
                System.out.println("Founded " + dirInfo.getFileCount() + " files with total size " + dirInfo.getTotalSize());
            }
            server.sendDirInfoToClient(dirInfo);
            server.listenForConnections();

            Socket connection = null;
            String file = null;
            BlockingDeque<String> files = dirInfo.getAllFiles();
            Semaphore semaphore = new Semaphore(numberOfConnections);
            List<Future<?>> futures = new ArrayList<>();
            AtomicInteger error = new AtomicInteger(0);
            while (!files.isEmpty() && error.get() < 10) {
                semaphore.acquire();
                if (connection == null) {
                    connection = server.getConnection();
                }
                if (file == null) {
                    file = files.peekFirst();
                }
                if (connection != null && file != null) {
                    futures.add(executorService.submit(new FileTransferTask(connection, new File(files.pollFirst()), server, files, semaphore, error)));
                    file = null;
                    connection = null;
                }
            }
            for(Future<?> future : futures) {
                future.get();
            }
            server.clearConnections();
            if(error.get() < 10) {
                System.out.println("All files was sent!");
            } else {
                System.err.println("Connection with server was lost!");
            }
        }
    }
}
