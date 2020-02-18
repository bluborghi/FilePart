package dev.blu.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileMerger implements FileAction{
    protected File f;
    protected int bufferLength;
    protected long[] bytesTransfered = new long[1];
	private long totalBytes;

    public FileMerger(File f, int bufferLength) {
        setFile(f);
        setBufferLength(bufferLength);
        bytesTransfered[0] = 0;
    }

    public FileMerger(File f){
        this(f,0); //use default
    }

    public int merge() throws IOException {
        /*String abs = getFile().getAbsolutePath();                                           //C:\files\photo.png
        File starter = null;
        int MAX_LENGTH = String.valueOf(Integer.MAX_VALUE).length();
        for (int i = 1; i <= MAX_LENGTH; i++) {
            File tmp = new File(abs + "." + String.format("%0" + i + "d", 1));
            if (tmp.exists()) {
                starter = tmp;
                break;
            }
        }*/
    	File starter = getFile();
        if (starter == null) return -1;

        String full = starter.getAbsolutePath();                        //C:\files\photo.png.001
        String name = starter.getName();                                //photo.png.001
        String ext = FileHelper.getFileExtension(name);                            //001
        String prefix = FileHelper.removeFileExtension(name);                      //photo.png
        String parent = FileHelper.getParentDirectory(full) + "\\";                  //C:\files\

        File output = new File(parent + "_" + prefix);          //C:\files\_photo.png
        output.createNewFile();
        FileOutputStream fos = new FileOutputStream(output);
        File tmp;
        int i = 1;
        while ((tmp = new File(parent + prefix + "." + String.format("%0" + ext.length() + "d", i))).exists()) { //C:\files\photo.png.i
            FileInputStream fis = new FileInputStream(tmp);

            if (getBufferLength()==0) //if bufferLength is set to default (0)
                FileHelper.transfer(fis, fos, tmp.length(),bytesTransfered);
            else
                FileHelper.transfer(fis,fos,tmp.length(),getBufferLength(),bytesTransfered);

            fis.close();
            i++;
        }
        fos.close();
        return 0;
    }

    public File getFile() {
        return f;
    }

    protected void setFile(File f1) {
        this.f = f1;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        if (bufferLength<0)
            this.bufferLength = 0; //if invalid, set to default
        else
            this.bufferLength = bufferLength;
    }

	@Override
	public void start() {
		try {
			merge();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getPercentage() {
		long tot = getTotalBytes();
		return ((double)bytesTransfered[0]/tot)*100;
	}

	private long getTotalBytes() {
		return totalBytes;
	}

	private void calcTotalBytes() {
		totalBytes = 0;
		int i = 1;
		File tmp;
		File starter = getFile();
        if (starter == null) return;
        String full = starter.getAbsolutePath();                        //C:\files\photo.png.001
        String name = starter.getName();                                //photo.png.001
        String ext = FileHelper.getFileExtension(name);                            //001
        String prefix = FileHelper.removeFileExtension(name);                      //photo.png
        String parent = FileHelper.getParentDirectory(full) + File.separatorChar;                  //C:\files\
		while ((tmp = new File(parent + prefix + "." + String.format("%0" + ext.length() + "d", i))).exists()) {
			this.totalBytes += tmp.length();
		}
	}
    
    
    
}

