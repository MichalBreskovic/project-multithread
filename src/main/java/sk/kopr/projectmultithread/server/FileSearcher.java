package sk.kopr.projectmultithread.server;

import sk.kopr.projectmultithread.utils.DirInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FileSearcher extends RecursiveTask<DirInfo> {

    File startingDir;
    DirInfo dirInfo;

    public FileSearcher(File startingDir) {
        this.startingDir = startingDir;
        dirInfo = new DirInfo(startingDir.getPath());
    }

    @Override
    public DirInfo compute() {
        dirSearcher(startingDir);
        return dirInfo;
    }

    public void dirSearcher(File dir) {
        File[] files = dir.listFiles();
        List<FileSearcher> tasks = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                dirInfo.addFile(file);
                System.out.println("Found file " + file.getPath());
            }
            if (file.isDirectory()) {
                FileSearcher subTask = new FileSearcher(file);
                subTask.fork();
                tasks.add(subTask);
            }
        }
        for (FileSearcher subTask: tasks) {
            DirInfo dirInfo = subTask.join();
            this.dirInfo.addDirInfo(dirInfo);
        }
    }

}
