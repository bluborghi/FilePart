package dev.blu.model;

import java.io.File;

public class FileSplitterByPartNumber extends FileSplitterByMaxSize {
    private long parts;

    public FileSplitterByPartNumber(File f, long parts) {
        super(f,0);
        setParts(parts);
        long size = calcMaxSize();
        setMaxSize(size);
    }

    public long getParts() {
        return parts;
    }

    protected void setParts(long parts) {
        this.parts = parts;
    }

    protected long calcMaxSize(){
        long file_length = getFile().length();
        return (file_length+parts-1)/parts;//formula to round up an integer division (positive only members)
    }
}
