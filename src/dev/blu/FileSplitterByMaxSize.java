package dev.blu;

import java.io.*;

public class FileSplitterByMaxSize extends FileSplitter {
    protected File f;
    protected long maxSize;
    protected int bufferLength;

    public FileSplitterByMaxSize(File f, long maxSize, int bufferLength) {
        setFile(f);
        setMaxSize(maxSize);
        setBufferLength(bufferLength);
    }

    public FileSplitterByMaxSize(File f, long maxSize) {
        this(f,maxSize,1024*1024); //1MiB standard buffer
    }

    @Override
    public int spilt() {
        long parts = (f.length() + maxSize - 1) / maxSize;//formula to round up an integer division (positive only members)
        long file_length = f.length();
        long part_length = maxSize;
        long last_part_length = (file_length - part_length * (parts - 1));
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);

            int extension_length = String.valueOf(parts - 1).length();
            if (extension_length < 3) extension_length = 3;


            int i = 1;
            while (i <= parts) {
                File outputFile = new File(f.getAbsolutePath() + "." + String.format("%0" + extension_length + "d", i));
                FileOutputStream fos = null;
                fos = new FileOutputStream(outputFile);


                if (i < parts) {
                    transfer(fis, fos, part_length);
                } else
                    transfer(fis, fos, last_part_length);

                fos.close();

                i++;
            }

            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public File getFile() {
        return f;
    }

    protected void setFile(File f) {
        this.f = f;
    }

    public long getMaxSize() {
        return maxSize;
    }

    protected void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int getBufferLength() {
        return bufferLength;
    }

    @Override
    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }
}
