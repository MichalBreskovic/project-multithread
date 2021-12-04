package sk.kopr.projectmultithread.exceptions;

import java.io.File;

public class FileNotReceivedException extends RuntimeException {

    private static final long serialVersionUID = -2231800164242876779L;
    private File dir;

    public FileNotReceivedException(File dir) {
        this.dir = dir;
    }
    public File getDir() {
        return dir;
    }

}
