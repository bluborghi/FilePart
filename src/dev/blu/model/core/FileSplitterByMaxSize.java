package dev.blu.model.core;

import java.io.*;

import dev.blu.model.enums.SplitOption;
import dev.blu.model.helpers.FileHelper;
import dev.blu.model.interfaces.FileAction;
import dev.blu.model.interfaces.FileSplitter;

public class FileSplitterByMaxSize implements FileSplitter, FileAction {
    protected File f;
    protected long maxSize;
    protected int bufferLength;
    protected long[] bytesTransfered = new long[1];

    private FileSplitterByMaxSize(File f, long maxSize, int bufferLength) {
        setFile(f);
        setMaxSize(maxSize);
        setBufferLength(bufferLength);
        bytesTransfered[0] = 0;
    }

    private FileSplitterByMaxSize(File f, long maxSize) {
        this(f,maxSize,1024*1024); //1MiB standard buffer
    }
    
    public FileSplitterByMaxSize(FileConfiguration config) {
    	this(
    		config.getFile(),
    		config.getSplitConfig().getPartSize()*config.getSplitConfig().getUnit().getMultiplier()
    	);
    }

	@Override
    public int split() {
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
                    FileHelper.transfer(fis, fos, part_length, bytesTransfered);
                } else
                    FileHelper.transfer(fis, fos, last_part_length, bytesTransfered);

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

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

	@Override
	public void start() {
		split();
	}

	@Override
	public double getPercentage() {
		return ((double) bytesTransfered[0]/(getFile().length())) * 100;
	}

	@Override
	public String checkForErrors() {
		String errors = "";
		if (getFile() == null)
			errors.concat("File not specified").concat(System.lineSeparator());
		else if (!getFile().exists()) {
			errors.concat("File not found").concat(System.lineSeparator());
		}
		if (getBufferLength()<0)
			errors.concat("Invalid buffer length").concat(System.lineSeparator());
		if (getMaxSize()<0)
			errors.concat("Invalid part size length").concat(System.lineSeparator());
		
		if (errors.isEmpty()) return null;
		else return errors;
	}

	@Override
	public SplitOption getSplitOption() {
		return SplitOption.SplitByMaxSize;
	}
	
}
