package sk.kopr.projectmultithread.utils;

import java.io.File;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class DirInfo {

    private String path;
    private int fileCount;
    private Long totalSize;
    private BlockingDeque<String> allFiles;

    public DirInfo(String startingPath) {
        this.path = startingPath;
        this.fileCount = 0;
        this.totalSize = 0L;
        this.allFiles = new LinkedBlockingDeque<>();
    }

    public DirInfo(int fileCount, Long totalSize) {
        this.fileCount = fileCount;
        this.totalSize = totalSize;
    }

    public DirInfo(String path, int fileCount, Long totalSize, BlockingDeque<String> allFiles) {
        this.path = path;
        this.fileCount = fileCount;
        this.totalSize = totalSize;
        this.allFiles = allFiles;
    }

    public void addFile(File file) {
        allFiles.offerLast(file.getPath());
        totalSize += file.length();
        fileCount++;
    }

    public void addDirInfo(DirInfo dirInfo) {
        totalSize += dirInfo.getTotalSize();
        fileCount += dirInfo.getFileCount();
        allFiles.addAll(dirInfo.getAllFiles());
    }

    public String getPath() {
        return path;
    }

    public int getFileCount() {
        return fileCount;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public BlockingDeque<String> getAllFiles() {
        return new LinkedBlockingDeque<>(allFiles);
    }
}
