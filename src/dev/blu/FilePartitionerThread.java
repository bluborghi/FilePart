package dev.blu;

import java.io.IOException;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MILLIS;

public class FilePartitionerThread extends Thread {
    private String fileDir;
    private long parts;

    public FilePartitionerThread(String fileDir, long parts) {
        setFileDir(fileDir);
        setParts(parts);
    }

    @Override
    public void run() {
        try {
            LocalTime t0 = LocalTime.now();
            FilePartitioner.splitByNumberOfParts(getFileDir(),getParts());
            LocalTime t1 = LocalTime.now();
            System.out.println("("+getName()+") Split time: "+ MILLIS.between(t0,t1)+"ms");
            FilePartitioner.merge(getFileDir());
            LocalTime t2 = LocalTime.now();
            System.out.println("("+getName()+") Merge time: "+ MILLIS.between(t1,t2)+"ms");
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
}
