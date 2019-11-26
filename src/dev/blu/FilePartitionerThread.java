package dev.blu;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MILLIS;

public class FilePartitionerThread extends Thread {
    private String fileDir;
    private File f;
    private long parts;

    public FilePartitionerThread(String fileDir, long parts) {
        setFileDir(fileDir);
        setParts(parts);
        setFile(new File(fileDir));
        super.setName(getFile().getName()+" "+getFile().length()/1024/1024+" MiB");
    }

    @Override
    public void run() {
        try {
            FileSplitter fs = new FileSplitterByPartNumber(new File(getFileDir()),getParts());
            FileMerger fm = new FileMerger(new File(getFileDir()));
            LocalTime t0 = LocalTime.now();
            fs.spilt();
            LocalTime t1 = LocalTime.now();
            fm.merge();
            LocalTime t2 = LocalTime.now();
            System.out.println("("+getName()+") Split time: "+ MILLIS.between(t0,t1)+"ms");
            System.out.println("("+getName()+") Merge time: "+ MILLIS.between(t1,t2)+"ms");
            System.out.println("("+getName()+") Total time: "+ MILLIS.between(t0,t2)+"ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public long getParts() {
        return parts;
    }

    public void setParts(long parts) {
        this.parts = parts;
    }

    public File getFile() {
        return f;
    }

    public void setFile(File f) {
        this.f = f;
    }
}
