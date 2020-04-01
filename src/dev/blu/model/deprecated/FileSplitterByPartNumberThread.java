package dev.blu.model.deprecated;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import dev.blu.model.core.FileSplitterByPartNumber;
import dev.blu.model.interfaces.FileSplitter;

import static java.time.temporal.ChronoUnit.MILLIS;

public class FileSplitterByPartNumberThread { //extends Thread {
  /*  private String fileDir;
    private File f;
    private long parts;

    public FileSplitterByPartNumberThread(String fileDir, long parts) {
        setFileDir(fileDir);
        setParts(parts);
        setFile(new File(fileDir));
        super.setName(getFile().getName()+" "+getFile().length()/1024/1024+" MiB");
    }
    
    public FileSplitterByPartNumberThread(File f, long parts) {
    	setFileDir(f.getAbsolutePath());
        setParts(parts);
        setFile(f);
        super.setName(getFile().getName()+" "+getFile().length()/1024/1024+" MiB");
	}

    @Override
    public void run() {
        FileSplitter fs = new FileSplitterByPartNumber(new File(getFileDir()),getParts());
		fs.split();
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
    */
}
